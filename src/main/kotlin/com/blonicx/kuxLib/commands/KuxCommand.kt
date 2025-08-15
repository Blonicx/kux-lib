package com.blonicx.kuxLib.commands

import com.blonicx.kuxLib.api.Dependencies
import com.blonicx.kuxLib.api.Dependency
import com.blonicx.kuxLib.core.DependencyManager
import com.blonicx.kuxLib.core.GlobalDependency
import com.blonicx.kuxLib.core.errors.InstallingError
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import com.mojang.brigadier.Command
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

internal object KuxCommand {

    val root: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("kux-lib")

    fun register() {
        root.then(
            Commands.literal("list-all").executes { ctx ->
                val source = ctx.source
                val dependencies = Dependencies.getAllDependencies() // List<String>

                if (dependencies.isEmpty()) {
                    source.sender.sendMessage("No dependencies found.")
                } else {
                    dependencies.forEach { dep ->
                        source.sender.sendMessage("Dependency: $dep")
                    }
                }
                Command.SINGLE_SUCCESS
            }
        )

        root.then(
            Commands.literal("install")
                .then(
                    Commands.argument("slug", StringArgumentType.string())
                        .executes { ctx ->
                            val source = ctx.source

                            if (!source.sender.hasPermission("kuxlib.admin")) {
                                source.sender.sendMessage("You do not have permission to use this command.")
                                return@executes Command.SINGLE_SUCCESS
                            }

                            val slug = StringArgumentType.getString(ctx, "slug")
                            val plugin: Plugin = Bukkit.getPluginManager().getPlugin("kux-lib")
                                ?: run {
                                    source.sender.sendMessage("Plugin instance not found!")
                                    return@executes Command.SINGLE_SUCCESS
                                }

                            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                                try {
                                    // Create a DependencyManager once and reuse it, or just instantiate here
                                    val dm = DependencyManager(plugin.dataFolder)
                                    dm.downloadPlugin(slug, null) // null = latest version
                                    // Send success message back on main thread
                                    Bukkit.getScheduler().runTask(plugin, Runnable {
                                        source.sender.sendMessage("Plugin '$slug' installed successfully.")
                                        GlobalDependency().addDependency(Dependency(slug, null))
                                    })
                                } catch (ex: Exception) {
                                    // Send failure message back on main thread
                                    Bukkit.getScheduler().runTask(plugin, Runnable {
                                        source.sender.sendMessage("Failed to install plugin '$slug': ${ex.message}")
                                        throw InstallingError(slug, "Unknown")
                                    })
                                }
                            })

                            Command.SINGLE_SUCCESS
                        }
                )
        )
    }
}