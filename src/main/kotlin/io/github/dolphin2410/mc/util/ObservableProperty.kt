package io.github.dolphin2410.mc.util

class ObservableProperty<T>(val data: T) {

    private val _observers = ArrayList<PropertyObserver<T>>()

    fun trigger() {
        _observers.forEach {
            it.accept(data)
        }
    }

    fun addObserver(observer: PropertyObserver<T>, init: Boolean = false) {
        _observers.add(observer)
        if (init) observer.accept(data)
    }

    fun removeObserver(observer: PropertyObserver<T>) {
        _observers.remove(observer)
    }

    fun clearObservers() {
        _observers.clear()
    }
}