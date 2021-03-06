package org.kotlinmc.bukkit.extensions

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*
import com.google.common.collect.Multimap
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.material.MaterialData

inline fun item(
        material: Material,
        amount: Int = 1,
        data: Short = 0,
        meta: ItemMeta.() -> Unit = {}
): ItemStack = ItemStack(material, amount, data).meta(meta)

inline fun <reified T : ItemMeta> metadataItem(
        material: Material,
        amount: Int = 1,
        data: Short = 0,
        meta: T.() -> Unit
): ItemStack = ItemStack(material, amount, data).meta(meta)

inline fun <reified T : ItemMeta> ItemStack.meta(
        block: T.() -> Unit
): ItemStack = apply {
    itemMeta = (itemMeta as? T)?.apply(block) ?: itemMeta
}

fun ItemStack.displayName(displayName: String): ItemStack = meta<ItemMeta> {
    this.displayName = displayName
}

fun ItemStack.lore(lore: List<String>): ItemStack = meta<ItemMeta> {
    this.lore = lore
}

inline fun Material.asItemStack(
        amount: Int = 1,
        data: Short = 0,
        meta: ItemMeta.() -> Unit = {}
): ItemStack = item(this, amount, data, meta)

fun Material.asMaterialData(
        data: Byte = 0
) = MaterialData(this, data)

fun MaterialData.toItemStack(
        amount: Int = 1,
        meta: ItemMeta.() -> Unit = {}
) = toItemStack(amount).meta(meta)

/**
 * get head from base64
 */
val gameProfileClass by lazy { Class.forName("com.mojang.authlib.GameProfile") }
val propertyClass by lazy { Class.forName("com.mojang.authlib.properties.Property") }
val getPropertiesMethod by lazy { gameProfileClass.getMethod("getProperties") }
val gameProfileConstructor by lazy { gameProfileClass.getConstructor(UUID::class.java, String::class.java) }
val propertyConstructor by lazy { propertyClass.getConstructor(String::class.java, String::class.java) }

fun headFromBase64(base64: String): ItemStack {
    val item = ItemStack(Material.SKULL_ITEM, 1, 3)
    val meta = item.itemMeta as SkullMeta

    val profile = gameProfileConstructor.newInstance(UUID.randomUUID(), null as String?)
    val properties = getPropertiesMethod.invoke(profile) as Multimap<Any, Any>
    properties.put("textures", propertyConstructor.newInstance("textures", base64))

    val profileField = meta.javaClass.getDeclaredField("profile").apply { isAccessible = true }
    profileField.set(meta, profile)

    return item.apply { itemMeta = meta }
}

inline val Material.isPickaxe: Boolean get() = name.endsWith("PICKAXE")
inline val Material.isSword: Boolean get() = name.endsWith("SWORD")
inline val Material.isAxe: Boolean get() = name.endsWith("_AXE")
inline val Material.isSpade: Boolean get() = name.endsWith("SPADE")
inline val Material.isHoe: Boolean get() = name.endsWith("HOE")
inline val Material.isOre: Boolean get() = name.endsWith("ORE")
inline val Material.isIngot: Boolean get() = name.endsWith("INGOT")
inline val Material.isDoor: Boolean get() = name.endsWith("DOOR")
inline val Material.isMinecart: Boolean get() = name.endsWith("MINECART")
inline val Material.isWater: Boolean get() = this == Material.WATER || this == Material.STATIONARY_WATER
inline val Material.isLava: Boolean get() = this == Material.LAVA || this == Material.STATIONARY_LAVA
