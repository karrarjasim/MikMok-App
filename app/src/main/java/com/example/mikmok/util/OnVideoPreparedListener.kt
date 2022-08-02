package com.example.mikmok.util

import com.example.mikmok.data.models.ExoPlayerItem

interface OnVideoPreparedListener {
    fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
}