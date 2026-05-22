package com.movtery.buildkeys.core

/**
 * buildKeys {} DSL 扩展
 */
open class BuildKeysExtension {
    /**
     * 生成的包名，默认取 android.namespace
     */
    var packageName: String? = null
    /**
     * 自定义 [com.movtery.buildkeys.core.obfuscation.StringObfuscator] 全限定类名
     */
    var obfuscatorClass: String = "com.movtery.buildkeys.core.obfuscation.DefaultStringObfuscator"

    internal val items = mutableListOf<ItemConfig>()



    fun string(key: String, value: String, hidden: Boolean = false) {
        require(key.isNotBlank()) { "string key must not be blank" }
        items.add(ItemConfig(type = "String", key = key, value = value, hidden = hidden))
    }

    fun int(key: String, value: Int) {
        require(key.isNotBlank()) { "int key must not be blank" }
        items.add(ItemConfig(type = "Int", key = key, value = value.toString()))
    }

    fun float(key: String, value: Float) {
        require(key.isNotBlank()) { "float key must not be blank" }
        items.add(ItemConfig(type = "Float", key = key, value = value.toString()))
    }

    fun double(key: String, value: Double) {
        require(key.isNotBlank()) { "double key must not be blank" }
        items.add(ItemConfig(type = "Double", key = key, value = value.toString()))
    }

    fun long(key: String, value: Long) {
        require(key.isNotBlank()) { "long key must not be blank" }
        items.add(ItemConfig(type = "Long", key = key, value = value.toString()))
    }

    fun boolean(key: String, value: Boolean) {
        require(key.isNotBlank()) { "boolean key must not be blank" }
        items.add(ItemConfig(type = "Boolean", key = key, value = value.toString()))
    }
}
