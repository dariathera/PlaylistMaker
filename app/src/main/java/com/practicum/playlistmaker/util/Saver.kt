package com.practicum.playlistmaker.util

interface Saver<T> {
    fun getFromMemory(): T?
    fun writeIntoMemory(t: T?)
}