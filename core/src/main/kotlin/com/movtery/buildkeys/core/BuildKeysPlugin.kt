package com.movtery.buildkeys.core

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildKeysPlugin : Plugin<Project> {
    companion object {
        internal const val EXTENSION_NAME = "buildKeys"
        internal const val DEFAULT_OBFUSCATOR = "com.movtery.buildkeys.core.obfuscation.DefaultStringObfuscator"
    }

    //防止 AGP 内部多个插件触发重复配置
    private var configured = false

    override fun apply(project: Project) {
        //等待 Android 插件加载完成后配置（仅执行一次）
        project.plugins.all { plugin ->
            if (!configured && plugin.javaClass.name.startsWith("com.android.build.gradle.")) {
                configured = true
                configurePlugin(project)
            }
        }
    }

    private fun configurePlugin(project: Project) {
        val extension = project.extensions.create(
            EXTENSION_NAME,
            BuildKeysExtension::class.java
        )

        val androidComponents = project.extensions.getByType(
            AndroidComponentsExtension::class.java
        )

        androidComponents.onVariants { variant ->
            val variantNameCap = variant.name.replaceFirstChar { it.uppercaseChar() }

            //注册生成任务，设定输出目录
            val taskProvider = project.tasks.register(
                "generate${variantNameCap}BuildKeys",
                BuildKeysTask::class.java
            ) { task ->
                task.group = "buildKeys"
                task.description = "Generates BuildKeys.kt for ${variant.name} variant"
                task.outputDirectory.set(
                    project.layout.buildDirectory.dir(
                        "generated/source/buildKeys/${variant.name}"
                    )
                )
            }

            //将生成目录注册为 variant 的额外源码目录
            variant.sources.java?.addGeneratedSourceDirectory(
                taskProvider
            ) { it.outputDirectory }

            project.afterEvaluate {
                taskProvider.configure { task ->
                    val pkg = extension.packageName
                        ?: readNamespace(project)
                        ?: error("BuildKeys: unable to determine package name. Please set 'packageName' in buildKeys {} or ensure android.namespace is configured.")

                    task.packageName.set(pkg)
                    task.obfuscatorClass.set(
                        extension.obfuscatorClass.ifEmpty { DEFAULT_OBFUSCATOR }
                    )
                    task.itemList.set(extension.items.toList())
                }
            }
        }
    }

    /**
     * 从 android 扩展读取 namespace
     */
    private fun readNamespace(project: Project): String? {
        return try {
            val androidExt = project.extensions.findByName("android") ?: return null
            val method = androidExt.javaClass.methods.firstOrNull { m ->
                m.name == "getNamespace" && m.parameterTypes.isEmpty()
            }
            method?.invoke(androidExt) as? String
        } catch (_: Exception) {
            null
        }
    }
}
