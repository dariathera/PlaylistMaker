package com.practicum.playlistmaker.data

interface Saver<T> {
    fun getFromMemory(): T?
    fun writeIntoMemory(t: T?)
}