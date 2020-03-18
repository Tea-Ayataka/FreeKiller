package net.ayataka.freekiller.chapters

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ayataka.freekiller.*
import net.ayataka.freekiller.reader.ReaderActivity

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class ChaptersActivity : AppCompatActivity() {
    lateinit var url: String
    var chapters: List<Chapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)

        val toolbar = findViewById<Toolbar>(R.id.chapters_toolbar)
        toolbar.title = intent.getStringExtra("name")
        setSupportActionBar(toolbar)

        url = intent.getStringExtra("url")!!
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        GlobalScope.launch {
            chapters = try {
                api.getChapters(url)
            } catch (ex: Exception) {
                this@ChaptersActivity.runOnUiThread {
                    Toast.makeText(this@ChaptersActivity, "Error: ${ex.message}", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            runOnUiThread {
                findViewById<ListView>(R.id.chapters_list).apply {
                    adapter = ArrayAdapter(
                        this@ChaptersActivity,
                        android.R.layout.simple_list_item_1,
                        chapters!!.map { it.name })
                    progressBar.isVisible = false
                    setOnItemClickListener { parent, view, position, id ->
                        openReader(url, position, chapters!!)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chapters, menu)
        menu?.findItem(R.id.resume_button)?.let {
            it.isEnabled = config.data.progress[url] != null
            it.setOnMenuItemClickListener {
                if (chapters != null && config.data.progress[url] != null) {
                    val chapter = config.data.progress[url]!!.split(":")[0].toInt()
                    val page = config.data.progress[url]!!.split(":")[1].toInt()
                    Toast.makeText(this, "Resuming from chapter: ${chapter + 1}, page: $page", Toast.LENGTH_SHORT).show()
                    openReader(url, chapter, chapters!!)
                }
                return@setOnMenuItemClickListener true
            }
        }
        return true
    }

    private fun openReader(url: String, index: Int, chapters: List<Chapter>){
        val intent = Intent(this@ChaptersActivity, ReaderActivity::class.java)

        intent.putExtra("url", url)
        intent.putExtra("chapterNum",  index)
        intent.putExtra("chapters", chapters.joinToString(separator = ";") { it.url })
        startActivity(intent)
    }
}
