package io.github.dolphin2410.mc

import io.github.dolphin2410.mc.plugin.MusicalChairPlugin
import io.github.dolphin2410.mc.util.PropertyObserver
import io.github.teamcheeze.plum.api.core.events.EventRegistry
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.title.Title.title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Boat
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.properties.Delegates

class MusicalChair {
    lateinit var center: Location

    var radius by Delegates.notNull<Int>()

    private val players = ArrayList<UUID>()

    var valid = false

    private val boats = BoatList()

    private var rounds by Delegates.notNull<Int>()

    var soundName: String = "AMBIENT_CAVE"

    var min = 2
    var max = 15

    private val onQuit = EventRegistry.register<PlayerQuitEvent> { e ->
        if (players.contains(e.player.uniqueId)) {
            players.remove(e.player.uniqueId)
            endGameReason(Reason.PLAYER_QUIT)
        }
    }

    init {
        boats.onAllSit(object: PropertyObserver<ArrayList<Pair<MagicalBoat, UUID>>> {
            override fun accept(t: ArrayList<Pair<MagicalBoat, UUID>>) {
                val iter = players.iterator()
                while (iter.hasNext()) {
                    val player = iter.next()
                    if (!t.any { it.second == player }) {
                        iter.remove()
                        Bukkit.getPlayer(player)!!.apply {
                            showTitle(title(text("You Lose!"), text("")))
                            gameMode = GameMode.SPECTATOR
                        }
                    }
                }
                countdown(5)
            }
        })
    }

    fun start() {
        valid = true
        removeChair()
        rounds = players.size - 1
    }

    fun countdown(num: Int) {
        var index = 0
        object: BukkitRunnable() {
            override fun run() {
                players.forEach { uuid ->
                    Bukkit.getPlayer(uuid)!!.showTitle(title(text(num - index++), text("")))
                }

                if (index == num) {
                    startMusic()
                }
            }
        }.runTaskTimer(MusicalChairPlugin.plugin, 0, 20)
    }

    fun startMusic() {
        // START MUSIC
        boats.disableMount()
        boats.enableEject()
        boats.ejectAll()

        players.forEach {
            Bukkit.getPlayer(it)!!.apply {
                playSound(this.location, soundName, 1.0F, 1.0F)
            }
        }

        val time = round(((Math.random() * (max - min)) + min))

        // random from 2 ~ 15
        object: BukkitRunnable() {
            override fun run() {
                players.forEach {
                    Bukkit.getPlayer(it)!!.showTitle(title(text("STOP!"), text("")))
                }
                stopMusic()
            }
        }.runTaskLater(MusicalChairPlugin.plugin, time.toLong() * 20L)
    }

    fun stopMusic() {
        boats.enableMount()
        boats.disableEject()
        // STOP MUSIC
        rounds--
        players.forEach {
            Bukkit.getPlayer(it)!!.stopSound(soundName)
        }
        removeChair()
        if (rounds == 0) {
            if (players.size != 1) {
                throw RuntimeException("Error! Final Player Is Not 1")
            }
        }
    }

    fun relocateChairs() {
        if (::center.isInitialized) {
            boats.forEachIndexed { index, it ->
                val angle = index * 360 / boats.size

                val x = sin(angle * Math.PI / 180) * radius
                val z = cos(angle * Math.PI / 180) * radius

                it.bukkit.teleport(center.clone().add(x, 0.0, z))
            }
        }
    }

    fun removeChair() {
        boats.popRemove()
        relocateChairs()
    }

    fun endGame() {
        EventRegistry.removeListener(onQuit)
        valid = false
        if (valid) {
            for (i in 0 until boats.size) {
                boats.popRemove()
            }
        }
    }

    fun endGameReason(reason: Reason) {
        players.forEach {
            Bukkit.getPlayer(it)?.sendMessage(text("Game Ended due to $reason"))
        }
        endGame()
    }

    fun addPlayer(uuid: UUID) {
        players.add(uuid)
        boats.add(MagicalBoat(center.world!!.spawn(center.add(0.0, boats.size * 0.5, 0.0), Boat::class.java), boats))
        if (valid) endGameReason(Reason.PLAYER_ADD)
    }

    fun removePlayer(uuid: UUID) {
        players.remove(uuid)
        if (valid) endGameReason(Reason.PLAYER_QUIT)
    }
}