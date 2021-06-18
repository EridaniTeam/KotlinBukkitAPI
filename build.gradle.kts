plugins {
    kotlin("jvm") version "1.5.10"
    id("me.bristermitten.pdm") version "0.0.33"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
}

group = "club.eridani"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    pdm(kotlin("stdlib"))
    pdm("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0") {
        exclude(module = "kotlin-stdlib")
        exclude(module = "kotlin-stdlib-jdk7")
        exclude(module = "kotlin-stdlib-jdk8")
    }
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
}


bukkit {

    name = "KotlinMinecraftBukkit"

    main = "org.kotlinmc.bukkit.KotlinMinecraftAPI"

    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
}