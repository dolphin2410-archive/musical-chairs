package io.github.dolphin2410.mc.util

class Property<T>() {
    constructor(initial: T): this() {
        data = initial
    }

    private var data: T? = null

    fun set(value: T) {
        this.data = value
    }

    fun get(): T {
        return data!!
    }

    fun getSafe(): T? {
        return data
    }

    internal fun forceSet(value: Any) {
        @Suppress("unchecked_cast")
        this.data = value as T
    }
}