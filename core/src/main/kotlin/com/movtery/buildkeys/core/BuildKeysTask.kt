package com.movtery.buildkeys.core

import com.movtery.buildkeys.core.obfuscation.DefaultStringObfuscator
import com.movtery.buildkeys.core.obfuscation.StringObfuscator
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class BuildKeysTask : DefaultTask() {
    @get:Input
    abstract val packageName: Property<String>
    @get:Input
    abstract val obfuscatorClass: Property<String>
    @get:Input
    abstract val itemList: ListProperty<ItemConfig>
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val items = itemList.get()
        val obfuscator = createObfuscator(obfuscatorClass.get())
        val packageValue = packageName.get()

        logger.lifecycle(
            "BuildKeys: generating for package '$packageValue' " +
            "with ${items.size} entries (${items.count { it.hidden }} hidden strings)"
        )

        val generator = BuildKeysGenerator(
            packageName = packageValue,
            items = items,
            obfuscator = obfuscator
        )

        val code = generator.generate()
        val outputFile = outputDirectory.get().asFile.resolve("BuildKeys.kt")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(code)

        logger.lifecycle("BuildKeys: generated -> ${outputFile.absolutePath}")
    }

    private fun createObfuscator(className: String): StringObfuscator {
        return try {
            val clazz = Class.forName(className)
            clazz.getDeclaredConstructor().newInstance() as StringObfuscator
        } catch (e: ClassNotFoundException) {
            logger.warn("BuildKeys: obfuscator class not found: '$className', using default. Cause: ${e.message}")
            DefaultStringObfuscator()
        } catch (e: NoSuchMethodException) {
            logger.warn("BuildKeys: obfuscator '$className' lacks no-arg constructor, using default. Cause: ${e.message}")
            DefaultStringObfuscator()
        } catch (e: ClassCastException) {
            logger.warn("BuildKeys: '$className' does not implement StringObfuscator, using default. Cause: ${e.message}")
            DefaultStringObfuscator()
        } catch (e: Exception) {
            logger.warn("BuildKeys: unexpected error creating obfuscator '$className', using default. Cause: ${e.message}")
            DefaultStringObfuscator()
        }
    }
}
