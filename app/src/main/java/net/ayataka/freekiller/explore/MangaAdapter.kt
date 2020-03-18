package net.ayataka.freekiller.explore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import net.ayataka.freekiller.*
import net.ayataka.freekiller.util.toBitmap

private val loaderContext = newSingleThreadContext("Thumbnail Loader")

class MangaAdapter : RecyclerView.Adapter<MangaAdapter.ViewHolder>() {
    var listener: ((Manga) -> Unit)? = null
    val dataSet: MutableList<Manga> = mutableListOf()

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val rootLayout: FrameLayout = v.findViewById(R.id.frame_layout)
        val imageView: ImageView = v.findViewById(R.id.imageView)
        val titleView: TextView = v.findViewById(R.id.manga_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.manga_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val manga = dataSet[position]

        holder.titleView.text = manga.title

        holder.rootLayout.setOnClickListener {
            listener?.invoke(manga)
        }

        holder.imageView.setImageResource(R.drawable.loading)

        GlobalScope.launch(loaderContext) {
            try {
                val data = httpClient.get<ByteArray>(manga.thumbnailUrl).toBitmap()
                mainActivity.runOnUiThread {
                    holder.imageView.setImageBitmap(data)
                }
            } catch (ex: Exception) {
                Log.e("", "failed to download thumbnail: ${manga.thumbnailUrl}\n${ex.message}")
            }
        }
    }

    override fun getItemCount() = dataSet.size
}