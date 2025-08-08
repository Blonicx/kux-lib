package com.blonicx.kuxLib.commands


import com.blonicx.kuxLib.api.Dependencies
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import com.mojang.brigadier.Command

object KuxCommand {
    val root: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("kux-lib")

    fun register() {
        root.then(
            Commands.literal("all").executes { ctx ->
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
    }
}
