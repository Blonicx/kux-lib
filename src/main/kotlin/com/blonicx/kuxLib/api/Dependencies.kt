package com.blonicx.kuxLib.api

import com.google.gson.Gson
import org.bukkit.Bukkit
import java.io.File

object Dependencies {
    private val gson = Gson()

    private val dataFolder: File = Bukkit.getPluginManager().getPlugin("kux-lib")?.dataFolder
        ?: throw IllegalStateException("Plugin data folder not found!")

    private val file = File( dataFolder, "dependencies.json")

    public fun getAllDependencies(): List<String> {
        if (!file.exists()) {
            throw IllegalStateException("Dependencies file does not exist.")
        }

        val jsonContent: String = file.readText()
        val dependencies: List<String> = gson.fromJson(jsonContent, Array<String>::class.java).toList()

        return dependencies
    }
}