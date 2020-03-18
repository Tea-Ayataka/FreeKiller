package net.ayataka.freekiller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.header
import net.ayataka.freekiller.api.MangaSource
import net.ayataka.freekiller.api.MangaSources


val httpClient = HttpClient {
    defaultRequest {
        header("Accept", "*/*")
        header("Accept-Language", "ja,en-US")
        header(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"
        )
    }
}

val api: MangaSource
    get() = MangaSources.valueOf(config.data.selectedApi)

lateinit var config: Config
lateinit var mainActivity: MainActivity

class MainActivity : AppCompatActivity() {
    init {
        mainActivity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        setContentView(R.layout.activity_main)

        config = Config(this)

        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)
    }
}
