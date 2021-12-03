package io.github.dolphin2410.mc.item

import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object Items {
    val specialBoat = ItemStack(Material.OAK_BOAT).apply {
        itemMeta = itemMeta.apply {
            displayName(text("Magical Boat"))
            addEnchant(Enchantment.LUCK, 1, false)
        }
    }
}