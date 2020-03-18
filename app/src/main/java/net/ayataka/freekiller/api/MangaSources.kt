package net.ayataka.freekiller.api

import net.ayataka.freekiller.Chapter
import net.ayataka.freekiller.Manga

enum class MangaSources : MangaSource {
    RAWKUMA {
        override val supportRatings = true
        override val mangaNumbersInAPage = 30

        override suspend fun query(query: String, page: Int, sortByRating: Boolean): List<Manga> {
            TODO("Not yet implemented")
        }

        override suspend fun getChapters(url: String): List<Chapter> {
            TODO("Not yet implemented")
        }

        override suspend fun getPages(url: String, range: IntRange): List<String> {
            TODO("Not yet implemented")
        }
    },
    RAWDEVART {
        override val supportRatings = false
        override val mangaNumbersInAPage = 12

        override suspend fun query(query: String, page: Int, sortByRating: Boolean): List<Manga> {
            TODO("Not yet implemented")
        }

        override suspend fun getChapters(url: String): List<Chapter> {
            TODO("Not yet implemented")
        }

        override suspend fun getPages(url: String, range: IntRange): List<String> {
            TODO("Not yet implemented")
        }
    },
    LHSCAN {
        override val supportRatings = false
        override val mangaNumbersInAPage = 20

        override suspend fun query(query: String, page: Int, sortByRating: Boolean): List<Manga> {
            TODO("Not yet implemented")
        }

        override suspend fun getChapters(url: String): List<Chapter> {
            TODO("Not yet implemented")
        }

        override suspend fun getPages(url: String, range: IntRange): List<String> {
            TODO("Not yet implemented")
        }
    },
    MANGA1000 {
        override val supportRatings = false
        override val mangaNumbersInAPage = 30

        override suspend fun query(query: String, page: Int, sortByRating: Boolean): List<Manga> {
            TODO("Not yet implemented")
        }

        override suspend fun getChapters(url: String): List<Chapter> {
            TODO("Not yet implemented")
        }

        override suspend fun getPages(url: String, range: IntRange): List<String> {
            TODO("Not yet implemented")
        }
    }
}