package org.kotlinmc.bukkit.dsl.menu

import org.kotlinmc.bukkit.dsl.menu.slot.SlotDSL
import org.kotlinmc.bukkit.menu.Menu
import org.kotlinmc.bukkit.menu.calculateSlot
import org.bukkit.inventory.ItemStack

inline fun MenuDSL.slot(
        line: Int,
        slot: Int,
        item: ItemStack?,
        block: SlotDSL.() -> Unit = {}
): SlotDSL = slot(calculateSlot(line, slot), item, block)

inline fun MenuDSL.slot(
        slot: Int,
        item: ItemStack?,
        block: SlotDSL.() -> Unit = {}
): SlotDSL = baseSlot.clone(item).apply(block).also {
    setSlot(slot, it)
}

interface MenuDSL : Menu<SlotDSL> {

    override val eventHandler: MenuEventHandlerDSL

    fun onUpdate(update: MenuPlayerUpdateEvent) {
        eventHandler.updateCallbacks.add(update)
    }

    fun onClose(close: MenuPlayerCloseEvent) {
        eventHandler.closeCallbacks.add(close)
    }

    fun onMoveToMenu(moveToMenu: MenuPlayerMoveToEvent) {
        eventHandler.moveToMenuCallbacks.add(moveToMenu)
    }

    fun preOpen(preOpen: MenuPlayerPreOpenEvent) {
        eventHandler.preOpenCallbacks.add(preOpen)
    }

    fun onOpen(open: MenuPlayerOpenEvent) {
        eventHandler.openCallbacks.add(open)
    }

}