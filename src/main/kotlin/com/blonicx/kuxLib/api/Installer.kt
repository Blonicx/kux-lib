package com.blonicx.kuxLib.api

import com.blonicx.kuxLib.core.DependencyManager
import org.bukkit.Bukkit

/**
 * Installer class for the kux-lib plugin.
 * This class checks if the kux-lib plugin is installed and installs it if not.
 */
class Installer {
    init {
        if (!isInstalled()) {
            install()
        }
    }

    /**
     * Checks if the kux-lib plugin is installed.
     */
    private fun isInstalled(): Boolean {
        if (Bukkit.getPluginManager().getPlugin("kux-lib") != null) {
            return true
        } else {
            Bukkit.getLogger().warning("kux-lib plugin is not installed. Installing now...")
            return false
        }
    }

    /**
     * Installs the kux-lib plugin.
     */
    private fun install() {
        DependencyManager().downloadPlugin("kux-lib")
        Bukkit.getLogger().info("kux-lib was successfully installed. Please restart the server to complete the installation.")
    }
}