package com.blonicx.kuxLib.core

import com.blonicx.kuxLib.api.Dependency
import com.google.gson.Gson
import org.bukkit.Bukkit
import java.io.File

internal class GlobalDependency {
    private val gson = Gson()

    private val dataFolder: File = Bukkit.getPluginManager().getPlugin("kux-lib")?.dataFolder
        ?: throw IllegalStateException("Plugin data folder not found!")

    private val file = File( dataFolder, "dependencies.json")

    fun writeDependenciesFile() {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText("[]") // initialize empty array so gson can parse later
        }

        // Load existing dependencies from file
        val existingDependencies: MutableList<Dependency> = try {
            val json = file.readText()
            if (json.isBlank()) mutableListOf()
            else gson.fromJson(json, Array<Dependency>::class.java).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }

        // Load dependencies from DependencyManager
        val loadedDependencies = DependencyManager(dataFolder).loadAllDependencies()

        // Merge without duplicates (assuming Dependency has proper equals/hashCode)
        val combinedDependencies = (existingDependencies + loadedDependencies).distinct()

        // Write merged list back to file
        file.writeText(gson.toJson(combinedDependencies))
    }

    fun addDependency(dependency: Dependency) {
        val dependencies: MutableList<Dependency> = if (file.exists()) {
            val json: String = file.readText()
            gson.fromJson(json, Array<Dependency>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        dependencies.add(dependency)
        file.writeText(gson.toJson(dependencies))
    }
}