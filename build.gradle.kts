import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    kotlin("jvm") version "1.5.21"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
    kotlin("plugin.serialization") version "1.5.21"
    `maven-publish`
}

val kotlinMCBukkitVersion = "0.0.2"

group = "club.eridani"
version = kotlinMCBukkitVersion

// backward compatbility, it will work with 1.8 to newest only if bukkit didn't make any big changes
val bukkitVersion = "1.8.8-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0") {
        exclude(module = "kotlin-stdlib")
        exclude(module = "kotlin-stdlib-jdk7")
        exclude(module = "kotlin-stdlib-jdk8")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.1") {
        exclude(module = "kotlin-stdlib")
        exclude(module = "kotlin-stdlib-jdk7")
        exclude(module = "kotlin-stdlib-jdk8")
    }
    implementation("com.charleskorn.kaml:kaml:0.35.1") {
        exclude(module = "kotlin-stdlib")
        exclude(module = "kotlin-stdlib-jdk7")
        exclude(module = "kotlin-stdlib-jdk8")
    }

    compileOnly("org.spigotmc:spigot-api:$bukkitVersion")
    testCompileOnly("org.spigotmc:spigot-api:$bukkitVersion")
}


bukkit {

    name = "KotlinMinecraftBukkit"

    main = "org.kotlinmc.bukkit.KotlinMinecraftAPI"

    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
}


val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}
