# KotlinBukkitAPI

Original (https://github.com/DevSrSouza/KotlinBukkitAPI)

Use in your project

1. Add maven repository to your project

```kotlin
repositories {
    maven("https://maven.eridani.club/")
}
```

2. Add dependencies, you don't need to add Kotlin stdlib and other kotlin related libraries that used in this project, but you need to include bukkit api on your
   own, every version should be supported

```kotlin
dependencies {
    implementation("club.eridani.bukkit:kotlin-api:0.0.3")
}
```