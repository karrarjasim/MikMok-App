package com.example.mikmok.data.models

data class Feed(
    val description: String,
    val id: String,
    val image: String,
    val items: List<VideoModel>,
    val title: String
)