plugins {
    kotlin("jvm") version "2.3.21" apply false
}

group = "com.movtery.buildkeys"
version = "1.0.0"

subprojects {
    group = rootProject.group
    version = rootProject.version
}
