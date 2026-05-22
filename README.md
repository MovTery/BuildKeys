# BuildKeys
一个编译期动态生成 Kotlin 配置类的 Gradle 插件，支持字符串自动“模糊化”处理  
类似 BuildConfig，但更方便使用

## 接入

**根 build.gradle.kts**

```kotlin
buildscript {
    repositories {
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.github.MovTery.BuildKeys:core:1.0.0")
    }
}
```

**module/build.gradle.kts** 应用插件配置：

```kotlin
plugins {
    id("com.movtery.buildkeys")
}

//配置该插件
buildKeys {
    string("API_KEY", "xxxxx")
    string("SECRET", "xxxxx", hidden = true) //启用字符串模糊化处理
    int("MAX_RETRY", 3)
    boolean("DEBUG", false)
}
```

该插件会生成一个 BuildKeys 单例类，像 BuildConfig 一样可以直接在项目内使用

```kotlin
val key = BuildKeys.API_KEY       // const val
val secret = BuildKeys.SECRET     // lazy 运行时解码
```

## 基础用法

支持 6 种类型，`hidden` 参数仅对 `string` 有效：

```kotlin
buildKeys {
    // 明文字符串 → 编译为 const val
    string("KEY", "value")
    // 模糊化字符串 → 编译为 lazy 运行时解码
    string("HIDDEN", "secret", hidden = true)

    int("COUNT", 100)
    float("RATIO", 0.5f)
    double("SCORE", 99.9)
    long("TIMEOUT", 5000L)
    boolean("ENABLED", true)

    // 自定义包名，可选，不配置则默认使用 android.namespace
    packageName = "com.example.config"
}
```

## 自定义模糊处理（混淆）

默认方案为 Base64 → IntArray  
如需自定义，请实现 `StringObfuscator` 接口：

```kotlin
class MyObfuscator : StringObfuscator {
    override fun obfuscate(original: String): ObfuscatedString {
        val encoded = org.example.encode(original)
        return ObfuscatedString(
            literal = "\"$encoded\"",                       // 嵌入源码的字面量
            runtimeExpression = "org.example.decode({LIT})" // {LIT} 替换为 literal
        )
    }
}
```

配置使用：

```kotlin
buildKeys {
    obfuscatorClass = "org.example.MyObfuscator"
    string("TOKEN", "xxx", hidden = true)
}
```
