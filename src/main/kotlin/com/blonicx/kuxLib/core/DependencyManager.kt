package com.blonicx.kuxLib.core

import com.blonicx.kuxLib.api.Dependency
import com.google.gson.Gson
import com.google.gson.JsonArray
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bukkit.Bukkit
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CopyOnWriteArraySet

internal class DependencyManager(private val dataFolder: File) {

    private val httpClient = OkHttpClient()
    private val gson = Gson()

    public val registeredPlugins = CopyOnWriteArraySet<String>()

    private fun parseVersion(version: String): List<Int> {
        return version.split(".").mapNotNull { it.toIntOrNull() }
    }

    private fun compareVersions(v1: List<Int>, v2: List<Int>): Int {
        val maxLength = maxOf(v1.size, v2.size)
        for (i in 0 until maxLength) {
            val part1 = v1.getOrNull(i) ?: 0
            val part2 = v2.getOrNull(i) ?: 0
            if (part1 != part2) return part1.compareTo(part2)
        }
        return 0
    }

    fun loadAllDependencies(): List<Dependency> {
        val dependenciesMap = mutableMapOf<String, MutableList<Dependency>>()
        val pluginsFolder: File = Bukkit.getPluginManager().getPlugin("kux-lib")?.dataFolder?.parentFile
            ?: return emptyList()

        pluginsFolder.listFiles()?.filter { it.isDirectory }?.forEach { pluginDir ->
            val depFile = File(pluginDir, "dependency.json")
            if (depFile.exists()) {
                val dependencies: Array<Dependency> = gson.fromJson(depFile.readText(), Array<Dependency>::class.java)
                dependencies.forEach { dependency ->
                    dependenciesMap.computeIfAbsent(dependency.slug) { mutableListOf() }.add(dependency)
                }
            }
        }

        return dependenciesMap.map { (slug, versions) ->
            val highest = versions
                .filter { it.version != null }
                .maxWithOrNull { a, b ->
                    compareVersions(parseVersion(a.version!!), parseVersion(b.version!!))
                } ?: versions.first()

            Dependency(slug, highest.version, true)
        }
    }

    fun downloadAllRegistered() {
        val dependencies = loadAllDependencies()
        Bukkit.getLogger().info("Downloading ${dependencies.size} plugin dependencies...")
        dependencies.forEach { dep ->
            try {
                downloadPlugin(dep.slug, dep.version)
            } catch (ex: Exception) {
                Bukkit.getLogger().severe("Failed to download dependency '${dep.slug}': ${ex.message}")
            }
        }
    }

    fun downloadPlugin(slug: String, exactVersion: String? = null) {
        val pluginsFolder: File = Bukkit.getPluginManager().getPlugin("kux-lib")?.dataFolder?.parentFile
            ?: throw Exception("Plugins folder not found")

        // Get the Minecraft version from the server //
        val rawVersion = Bukkit.getVersion()
        val regex = """MC: (\d+\.\d+)""".toRegex()
        val gameVersion = regex.find(rawVersion)?.groupValues?.get(1) ?: "1.21"

        // Get the Modrinth API URL for the version //
        val versionsUrl = "https://api.modrinth.com/v2/project/$slug/version"
        val request = Request.Builder().url(versionsUrl).build()

        // Make the request to fetch versions //
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch versions: ${response.code}")

            val versions = gson.fromJson(response.body?.string(), JsonArray::class.java)

            val compatibleVersion = if (exactVersion != null) {
                versions.firstOrNull { element ->
                    val obj = element.asJsonObject
                    obj["version_number"].asString == exactVersion
                }?.asJsonObject ?: throw Exception("No version '$exactVersion' found for $slug")
            } else {
                versions.firstOrNull { element ->
                    val obj = element.asJsonObject
                    val gameVersions = obj["game_versions"].asJsonArray.map { it.asString }
                    val loaders = obj["loaders"].asJsonArray.map { it.asString }
                    gameVersion in gameVersions && "paper" in loaders
                }?.asJsonObject ?: throw Exception("No compatible version found for $slug")
            }

            val fileObj = compatibleVersion["files"].asJsonArray.firstOrNull()?.asJsonObject
                ?: throw Exception("No files found for version")

            val fileName = fileObj["filename"].asString
            val targetFile = File(pluginsFolder, fileName)

            if (targetFile.exists()) {
                Bukkit.getLogger().info("Plugin $fileName already exists, skipping download.")
                return
            }

            val downloadUrl = fileObj["url"].asString

            val downloadRequest = Request.Builder().url(downloadUrl).build()
            httpClient.newCall(downloadRequest).execute().use { downloadResponse ->
                if (!downloadResponse.isSuccessful) throw Exception("Failed to download $slug: ${downloadResponse.code}")

                val body = downloadResponse.body ?: throw Exception("Empty response body")

                val contentLength = body.contentLength()
                if (contentLength == -1L) {
                    // Unknown length, fallback to simple download without progress
                    FileOutputStream(targetFile).use { out ->
                        body.byteStream().copyTo(out)
                    }
                    Bukkit.getLogger().info("✅ Plugin $fileName installed to ${targetFile.absolutePath} (unknown size)")
                } else {
                    FileOutputStream(targetFile).use { out ->
                        val inputStream = body.byteStream()
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Long = 0
                        var read: Int
                        var lastLoggedPercent = 0

                        while (inputStream.read(buffer).also { read = it } != -1) {
                            out.write(buffer, 0, read)
                            bytesRead += read

                            val percent = ((bytesRead * 100) / contentLength).toInt()
                            if (percent >= lastLoggedPercent + 10 || percent == 100) {
                                Bukkit.getLogger().info("Downloading $fileName: $percent%")
                                lastLoggedPercent = percent
                            }
                        }
                    }
                    Bukkit.getLogger().info("✅ Plugin $fileName installed to ${targetFile.absolutePath}")
                }
            }
        }
    }
}
