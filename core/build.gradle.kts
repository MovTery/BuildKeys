plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.android.tools.build:gradle:9.1.0")
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    plugins {
        create("buildKeys") {
            id = "com.movtery.buildkeys"
            implementationClass = "com.movtery.buildkeys.core.BuildKeysPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

tasks.processResources {
    filesMatching("version.properties") {
        expand("version" to project.version)
    }
}
