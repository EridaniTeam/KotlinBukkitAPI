import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    kotlin("jvm") version "1.5.10"
    id("me.bristermitten.pdm") version "0.0.33"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"

    `maven-publish`
}

val kotlinMCBukkitVersion = "0.0.1"

group = "club.eridani"
version = kotlinMCBukkitVersion

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

val spaceProperty = loadProperties("${projectDir.absolutePath}/space.properties")


val sources by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {

            from(components["kotlin"])
            artifact(sources.get())

            groupId = "org.kotlinmc"
            artifactId = "bukkit"
            version = kotlinMCBukkitVersion

            pom {
                withXml {
                    val pdmConfig = configurations.pdm.get()
                    asElement().applyConfigurationDependenciesToMavenPom(pdmConfig)
                }
            }


        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.jetbrains.space/eridaniteam/p/kotlinmc/maven-beta")
            credentials {
                username = spaceProperty.getProperty("username")
                password = spaceProperty.getProperty("password")
            }
        }
    }
}


fun org.w3c.dom.Element.applyConfigurationDependenciesToMavenPom(
    configuration: Configuration,
    scope: String = "compile"
) {
    val document = ownerDocument

    val dependenciesElement = getElementsByTagName("dependencies")
        .asList()
        .filterIsInstance<org.w3c.dom.Element>()
        .first()

    val dependenciesNodes = document.createElementsForConfigurationDependencies(
        configuration, scope
    )

    for(dependency in dependenciesNodes)
        dependenciesElement.appendChild(dependency)
}

fun org.w3c.dom.Document.createElementsForConfigurationDependencies(
    configuration: Configuration,
    scope: String
): List<org.w3c.dom.Element> = configuration.allDependencies
    .filter { (it is ProjectDependency) || (it !is SelfResolvingDependency) }
    .map {
        createElement("dependency").apply {
            appendChild(createNode("groupId", it.group))
            appendChild(createNode("artifactId", it.name))
            appendChild(createNode("version", it.version))
            appendChild(createNode("scope", scope))

            if(it is ExternalDependency) {
                if(it.excludeRules.isNotEmpty()) {
                    appendChild(createElementsForExcludeRules(
                        it.excludeRules
                    ))
                }
            }
        }
    }

fun org.w3c.dom.Document.createElementsForExcludeRules(
    excludeRules: MutableSet<ExcludeRule>
): org.w3c.dom.Node {
    return createElement("exclusions").apply {
        for (excludeRule in excludeRules) {
            appendChild(createElement("exclusion").apply {
                appendChild(createNode("artifactId", excludeRule.module?.takeIf { it.isNotBlank() } ?: "*"))
                appendChild(createNode("groupId", excludeRule.group?.takeIf { it.isNotBlank() } ?: "*"))
            })
        }
    }
}


fun org.w3c.dom.NodeList.asList() = (0..length).map { item(it) }

inline fun org.w3c.dom.Document.createNode(name: String, value: Any?): org.w3c.dom.Element {
    return createElement(name).apply {
        setTextContent(value.toString())
    }
}