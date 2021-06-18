package org.kotlinmc.bukkit.serialization.serializers.exceptions

class WorldNotFoundException(val world: String) : Exception("Could not deserialize the World=$world because was not found on the Server.")