package com.movtery.buildkeys.core

import java.io.Serializable

data class ItemConfig(
    val type: String,
    val key: String,
    val value: String,
    val hidden: Boolean = false
) : Serializable
