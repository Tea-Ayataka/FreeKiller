package net.ayataka.freekiller.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import net.ayataka.freekiller.R
import net.ayataka.freekiller.api
import net.ayataka.freekiller.config
import net.ayataka.freekiller.httpClient
import net.ayataka.freekiller.util.toBitmap

val readerScope = CoroutineScope(newSingleThreadContext("Reader Thread Context"))

class ReaderActivity : FragmentActivity() {
    lateinit var pager: ViewPager2
    lateinit var mangaUrl: String
    val pages: MutableList<String> = mutableListOf()
    var startPosOfChapter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        mangaUrl = intent.getStringExtra("url")!!
        val chapters = intent.getStringExtra("chapters")!!.split(";")
        var chapterNumber = intent.getIntExtra("chapterNum", 0)

        pager = findViewById(R.id.manga_pager)
        pager.layoutDirection = View.LAYOUT_DIRECTION_RTL
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                println("onPageScrolled: $position")
                GlobalScope.launch {
                    val actualPosition = position - startPosOfChapter
                    config.data.progress[mangaUrl] = "$chapterNumber:$actualPosition"
                    config.save()
                }

                if (position >= pages.size - 1) {
                    if (chapterNumber + 1 >= chapters.size) {
                        println("Last chapter")
                        return
                    }

                    chapterNumber++
                    println("Loading chapter: $chapterNumber")
                    runOnUiThread {
                        Toast.makeText(this@ReaderActivity, "Loading next chapter", Toast.LENGTH_SHORT).show()
                    }
                    GlobalScope.launch {
                        pages.addAll(api.getPages(chapters[chapterNumber], 0..99999))
                        runOnUiThread {
                            startPosOfChapter = position
                            pager.adapter?.notifyItemRangeChanged(position + 1, pages.size - position)
                            Toast.makeText(this@ReaderActivity, "Ready", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })

        GlobalScope.launch {
            pages.addAll(api.getPages(chapters[chapterNumber], 0..99999))

            runOnUiThread {
                pager.adapter = CustomAdapter(this@ReaderActivity, pages) {
                    // Back/Forward when click screen
                    val nextPage = pager.currentItem + (if (it) 1 else -1)
                    if (nextPage in 0 until pager.adapter!!.itemCount) {
                        pager.setCurrentItem(nextPage, true)
                    }
                }

                findViewById<ProgressBar>(R.id.progress_bar).isVisible = false

                // Resume
                config.data.progress[mangaUrl]?.let {
                    if (it.split(":")[0].toInt() == chapterNumber) {
                        pager.setCurrentItem(it.split(":")[1].toInt(), false)
                    }
                }
            }
        }
    }

    class CustomAdapter(
        private val activity: ReaderActivity,
        private val pages: List<String>,
        private val onClickListener: (direction: Boolean) -> Unit
    ) :
        RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.fragment_manga_page,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = pages.size

        private var touchedX = 0F

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.root.setOnTouchListener { _, event ->
                touchedX = event.x
                false
            }

            holder.root.setOnClickListener {
                onClickListener(holder.root.width / 2 > touchedX)
            }
            holder.imageView.setImageResource(0)
            readerScope.launch {
                try {
                    println("Downloading : ${pages[position]}")
                    val image = httpClient.get<ByteArray>(pages[position]) {
                        header("Referer", activity.mangaUrl)
                    }.toBitmap()
                    activity.runOnUiThread {
                        holder.imageView.setImageBitmap(image)
                    }
                } catch (ex: Exception) {
                    activity.runOnUiThread {
                        Toast.makeText(activity, "Error: ${ex.message}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("", "Failed to download image: ${ex.message}")
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: LinearLayout = view.findViewById(R.id.root_view)
        val imageView: ImageView = view.findViewById(R.id.page_image)
    }
}
