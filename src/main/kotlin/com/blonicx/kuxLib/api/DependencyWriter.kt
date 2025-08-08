package com.blonicx.kuxLib.api

import com.google.gson.Gson
import org.bukkit.plugin.Plugin
import java.io.File

/**
 * A class to write dependencies to a JSON file.
 *
 * @param plugin The plugin instance to access the data folder.
 * @param dependencies A list of dependencies to write to the file.
 */
class DependencyWriter(private val plugin: Plugin, private val dependencies: List<Dependency>) {
    private val gson = Gson()

    /**
     * Writes the dependencies to a JSON file named "dependency.json" in the plugin's data folder.
     * The file will be created if it doesn't exist, and the parent directories will be created if necessary.
     */
    fun writeDependencies() {
        val file = File(plugin.dataFolder, "dependency.json")
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(dependencies))
    }
}