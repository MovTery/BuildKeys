package com.movtery.buildkeys.core.obfuscation

/**
 * 混淆结果
 * [literal] 直接嵌入生成代码，[runtimeExpression] 中的 `{LIT}` 占位符会在生成时被替换为 [literal]
 */
data class ObfuscatedString(
    val literal: String,
    val runtimeExpression: String = "{LIT}"
)
