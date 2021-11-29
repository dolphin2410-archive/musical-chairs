package io.github.dolphin2410.mc.plugin

import io.github.dolphin2410.mc.MusicalChair
import io.github.dolphin2410.mc.item.Items
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.teamcheeze.plum.api.core.events.EventRegistry
import net.kyori.adventure.text.Component.text
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class MusicalChairPlugin: JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var plugin: JavaPlugin
    }

    private val map = HashMap<UUID, MusicalChair>()

    override fun onEnable() {
        plugin = this
        EventRegistry.servicePlugin = this
        kommand {
            register("music") {
                requires { isOp }
                then("give") {
                    executes {
                        player.inventory.addItem(Items.specialBoat)
                    }
                }

                then("radius") {
                    then("radius" to int()) {
                        executes {
                            val radius: Int by it

                            val data = map[player.uniqueId]
                            if (data == null) {
                                player.sendMessage("No Game")
                            } else {
                                data.radius = radius
                            }
                        }
                    }
                }

                then("addPlayer") {
                    then("player" to player()) {
                        executes {
                            val player: Player by it

                            val data = map[player.uniqueId]
                            if (data == null) {
                                player.sendMessage("No Game")
                            } else {
                                data.addPlayer(player.uniqueId)
                            }
                        }
                    }
                }

                then("start") {
                    executes {
                        val data = map[player.uniqueId]
                        if (data == null) {
                            player.sendMessage("No Game")
                        } else {
                            data.start()
                        }
                    }
                }
            }
        }

        EventRegistry.register<PlayerInteractEvent> { e ->
            if (e.player.inventory.itemInMainHand == Items.specialBoat && e.action == Action.RIGHT_CLICK_BLOCK) {
                e.isCancelled = true
                e.player.sendMessage(text("${ChatColor.DARK_PURPLE}The Location is set to center"))
                map[e.player.uniqueId] = MusicalChair().apply {
                    center = e.clickedBlock!!.location.add(0.0, 1.0, 0.0)
                }
            }
        }
    }

    override fun onDisable() {

    }
}