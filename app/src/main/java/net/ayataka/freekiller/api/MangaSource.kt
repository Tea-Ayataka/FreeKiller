package net.ayataka.freekiller.api

import net.ayataka.freekiller.Chapter
import net.ayataka.freekiller.Manga

interface MangaSource {
    val name: String
    val supportRatings: Boolean
    val mangaNumbersInAPage: Int

    suspend fun query(query: String, page: Int, sortByRating: Boolean = false): List<Manga>
    suspend fun getChapters(url: String): List<Chapter>
    suspend fun getPages(url: String, range: IntRange): List<String>
}