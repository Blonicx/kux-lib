package com.blonicx.kuxLib.api

/**
 * Represents a dependency with its slug and optional version
 *
 * @property slug The unique identifier for the dependency
 * @property version The versions of the dependency, if specified
 * @property checkForUpdate Flag indicating whether to check for updates for this dependency
 */
data class Dependency(val slug: String, val version: String? = null, val checkForUpdate: Boolean) {
    constructor(slug: String, version: String?) : this(slug, version, false)
}