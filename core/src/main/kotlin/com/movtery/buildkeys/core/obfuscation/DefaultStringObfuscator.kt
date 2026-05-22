package com.movtery.buildkeys.core.obfuscation

/**
 * 默认实现：Base64 编码后转为 IntArray 字面量，解码逻辑内联在 runtimeExpression 中
 */
class DefaultStringObfuscator : StringObfuscator {
    override fun obfuscate(original: String): ObfuscatedString {
        val base64 = java.util.Base64.getEncoder()
            .encodeToString(original.toByteArray(Charsets.UTF_8))
        val arrayLiteral = base64.map { it.code }.joinToString(", ")
        return ObfuscatedString(
            literal = "intArrayOf($arrayLiteral)",
            runtimeExpression = "String(java.util.Base64.getDecoder().decode(" +
                "buildString { {LIT}.forEach { append(it.toChar()) } }), " +
                "Charsets.UTF_8)"
        )
    }
}
