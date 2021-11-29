package io.github.dolphin2410.mc

import io.github.dolphin2410.mc.util.ObservableProperty
import io.github.dolphin2410.mc.util.Property
import io.github.teamcheeze.plum.api.core.events.EventRegistry
import org.bukkit.entity.Boat
import org.bukkit.entity.Player
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.spigotmc.event.entity.EntityMountEvent

data class MagicalBoat(val bukkit: Boat, val parent: BoatList) {
    private val onSit = EventRegistry.register<EntityMountEvent> { e ->
        if (parent.mountable && e.mount == bukkit && e.entity is Player) {
            parent.occupiedBoats.data.add(this to e.entity.uniqueId)
            parent.occupiedBoats.trigger()
        } else {
            e.isCancelled = true
        }
    }

    private val onMove = EventRegistry.register<VehicleMoveEvent> { e ->
        if (e.vehicle == bukkit) bukkit.teleport(e.from)
    }

    private val onEject = EventRegistry.register<VehicleExitEvent> { e ->
        if (e.vehicle == bukkit && parent.ejectable) {
            parent.occupiedBoats.data.removeIf { it.first == this }
            parent.occupiedBoats.trigger()
        } else {
            e.isCancelled = true
        }
    }

    fun remove(): MagicalBoat {
        bukkit.remove()
        parent.occupiedBoats.data.removeIf { it.first == this }
        EventRegistry.removeListener(onSit)
        EventRegistry.removeListener(onMove)
        EventRegistry.removeListener(onEject)
        return this
    }

    fun eject() {
        bukkit.eject()
    }
}