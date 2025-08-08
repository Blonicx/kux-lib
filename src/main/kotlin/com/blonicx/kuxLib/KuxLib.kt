package com.blonicx.kuxLib

import com.blonicx.kuxLib.core.DependencyManager
import org.bukkit.plugin.java.JavaPlugin

class KuxLib : JavaPlugin() {

    private lateinit var dependencyManager: DependencyManager

    override fun onLoad() {
        // Plugin loading logic
        dependencyManager = DependencyManager(dataFolder)
    }

    override fun onEnable() {
        // Download any dependencies that are missing
        dependencyManager.downloadAllRegistered()
    }


    override fun onDisable() {
        // Plugin shutdown logic
    }
}
