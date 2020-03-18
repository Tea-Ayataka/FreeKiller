package net.ayataka.freekiller

import android.content.Context
import com.google.gson.Gson
import net.ayataka.freekiller.api.MangaSources

class Config(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE)
    val data = sharedPreferences.getString("data", null)?.let { Gson().fromJson(it, Settings::class.java) }
        ?: Settings()

    fun save() {
        sharedPreferences.edit().putString("data", Gson().toJson(data)).apply()
    }
}

class Settings {
    var selectedApi = MangaSources.RAWDEVART.name
    val progress = mutableMapOf<String, String>()
    val history = mutableListOf<String>()
}