package org.kotlinmc.bukkit.serialization.serializers.exceptions

import java.lang.Exception

class MaterialNotFoundException(val material: String): Exception("Could not deserialize the Material=$material because it do not exist.")