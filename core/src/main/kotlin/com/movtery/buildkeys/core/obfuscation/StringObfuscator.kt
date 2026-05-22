package com.movtery.buildkeys.core.obfuscation

/**
 * 字符串混淆接口，实现类需提供无参构造器
 * 通过 [ObfuscatedString.{LIT}][ObfuscatedString] 占位符完全自定义数据格式与运行时解码表达式
 */
interface StringObfuscator {
    fun obfuscate(original: String): ObfuscatedString
}
