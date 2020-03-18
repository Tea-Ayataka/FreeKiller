package net.ayataka.freekiller

data class Manga(val url: String, val title: String, val rating: Double, val thumbnailUrl: String)

data class Chapter(val url: String, val name: String)