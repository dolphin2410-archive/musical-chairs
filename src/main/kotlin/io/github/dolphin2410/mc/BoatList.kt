package io.github.dolphin2410.mc

import io.github.dolphin2410.mc.util.ObservableProperty
import io.github.dolphin2410.mc.util.PropertyObserver
import java.util.*
import kotlin.collections.ArrayList

class BoatList: ArrayDeque<MagicalBoat>() {
    val occupiedBoats = ObservableProperty(ArrayList<Pair<MagicalBoat, UUID>>())

    var ejectable: Boolean = false
        private set

    var mountable: Boolean = false
        private set

    var movable: Boolean = false
        private set

    fun movable(): Boolean {
        return movable
    }

    fun popRemove() {
        pop().remove()
    }

    fun disableEject() {
        ejectable = false
    }

    fun enableEject() {
        ejectable = true
    }

    fun enableMount() {
        mountable = true
    }

    fun disableMount() {
        mountable = false
    }

    fun enableMove() {
        movable = true
    }

    fun disableMove() {
        movable = false
    }

    fun onAllSit(observer: PropertyObserver<ArrayList<Pair<MagicalBoat, UUID>>>) {
        occupiedBoats.addObserver(object: PropertyObserver<ArrayList<Pair<MagicalBoat, UUID>>> {
            override fun accept(t: ArrayList<Pair<MagicalBoat, UUID>>) {
                if (this@BoatList.size == t.size) {
                    observer.accept(t)
                }
            }
        }, false)
    }

    fun ejectAll() {
        forEach {
            it.eject()
        }
        occupiedBoats.data.clear()
    }
}