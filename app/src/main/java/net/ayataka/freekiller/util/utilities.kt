package net.ayataka.freekiller.util

import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.net.URI
import java.net.URL

fun ByteArray.toBitmap() = BitmapFactory.decodeByteArray(this, 0, this.size)

fun String.toJsonObject(): JsonObject = Gson().fromJson(this, JsonObject::class.java)

fun JsonElement.path(path: String): JsonElement? {
    var result: JsonElement = this

    path.split(".").forEach {
        if (it.endsWith("]")) {
            val field = it.substringBefore("[")
            val index = it.substringAfter("[").substringBefore("]").toInt()
            result = (result.asJsonObject.getOrNull(field) ?: return null).asJsonArray[index]
            return@forEach
        }

        result = result.asJsonObject.getOrNull(it) ?: return null
    }

    return result
}

fun JsonObject.getOrNull(key: String) = if (isJsonNull) null else get(key)