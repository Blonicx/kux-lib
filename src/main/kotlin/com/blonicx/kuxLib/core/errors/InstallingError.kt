package com.blonicx.kuxLib.core.errors

class InstallingError(
    private val dependency: String,
    private val version: String,
    message: String? = null
) : Exception(message ?: "Error installing $dependency, version $version")
