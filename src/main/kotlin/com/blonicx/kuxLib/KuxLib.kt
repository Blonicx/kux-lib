package com.blonicx.kuxLib

import com.blonicx.kuxLib.commands.KuxCommand
import com.blonicx.kuxLib.core.DependencyManager
import com.blonicx.kuxLib.core.GlobalDependency
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

class KuxLib : JavaPlugin() {

    private lateinit var dependencyManager: DependencyManager
    private lateinit var globalDependency: GlobalDependency

    override fun onLoad() {
        // Plugin loading logic
        dependencyManager = DependencyManager(dataFolder)
        globalDependency = GlobalDependency()
    }

    override fun onEnable() {
        // Download any dependencies that are missing
        dependencyManager.downloadAllRegistered()

        // Write the dependencies to a file
        globalDependency.writeDependenciesFile()

        // Register Commands
        KuxCommand.register()

        this.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(KuxCommand.root.build())
        }
    }


    override fun onDisable() {
        // Plugin shutdown logic
    }
}
