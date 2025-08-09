package com.blonicx.kuxLib.api

import com.google.gson.Gson
import org.bukkit.Bukkit
import java.io.File

object Dependencies {
    private val gson = Gson()

    private val dataFolder: File = Bukkit.getPluginManager().getPlugin("kux-lib")?.dataFolder
        ?: throw IllegalStateException("Plugin data folder not found!")

    private val file = File( dataFolder, "dependencies.json")

    /**
     * Retrieves all dependencies that are loaded by the plugin.
     * This method reads the `dependencies.json` file
     * located in the plugin's data folder and returns a list of dependencies
     * as a list of strings.
     * @return List<String> The list of dependencies loaded by the plugin.
     * @throws IllegalStateException If the dependencies file doesn't exist.
     */
    fun getAllDependencies(): List<String> {
        if (!file.exists()) {
            throw IllegalStateException("Dependencies file does not exist.")
        }

        val jsonContent: String = file.readText()
        val dependencies: Array<Dependency> = gson.fromJson(jsonContent, Array<Dependency>::class.java)
        return dependencies.map { it.slug }
    }
}