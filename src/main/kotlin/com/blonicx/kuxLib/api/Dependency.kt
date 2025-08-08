package com.blonicx.kuxLib.api

/**
 * Represents a dependency with its slug and optional version
 *
 * @property slug The unique identifier for the dependency
 * @property version The versions of the dependency, if specified
 */
data class Dependency(val slug: String, val version: String? = null)