package com.blonicx.kuxLib.core

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
        }

        file.writeText(gson.toJson(DependencyManager(dataFolder).loadAllDependencies()))
    }
}