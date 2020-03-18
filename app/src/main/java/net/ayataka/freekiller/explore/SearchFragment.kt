package net.ayataka.freekiller.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ayataka.freekiller.R
import net.ayataka.freekiller.api
import net.ayataka.freekiller.chapters.ChaptersActivity
import net.ayataka.freekiller.mainActivity


class SearchFragment : Fragment() {
    private var query = ""
    private var loading = false
    private var page = 1
    private val adapter = MangaAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        progressBar = root.findViewById(R.id.progressBar2)

        val layoutManager = GridLayoutManager(root.context, 2)
        recyclerView = root.findViewById<RecyclerView>(R.id.manga_list_view).apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
            this.layoutManager = layoutManager
            this.adapter = this@SearchFragment.adapter
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (loading || dy < 0) {
                    return
                }
                if (layoutManager.childCount + layoutManager.findFirstVisibleItemPosition() >= layoutManager.itemCount) {
                    loading = true
                    page++
                    println("Loading page: $page")
                    GlobalScope.launch {
                        val index = adapter.dataSet.size
                        val retrievedItems = try {
                            api.query(query, page).toMutableList()
                        } catch (ex: Exception) {
                            loading = false
                            mainActivity.runOnUiThread {
                                Toast.makeText(mainActivity, "Error: ${ex.message}", Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }
                        retrievedItems.removeIf { adapter.dataSet.contains(it) }
                        adapter.dataSet.addAll(retrievedItems)
                        mainActivity.runOnUiThread {
                            adapter.notifyItemRangeInserted(index + 1, retrievedItems.size)
                            println("Added ${retrievedItems.size} items")

                            if (retrievedItems.size >= api.mangaNumbersInAPage) {
                                loading = false
                            } // no more left
                        }
                    }
                }
            }
        })

        adapter.listener = {
            val intent = Intent(context, ChaptersActivity::class.java)
            intent.putExtra("url", it.url)
            intent.putExtra("name", it.title)
            startActivity(intent)
        }

        val searchBar = root.findViewById<MaterialSearchBar>(R.id.search_bar)
        searchBar.isSuggestionsEnabled = false
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {

            }

            override fun onSearchStateChanged(enabled: Boolean) {

            }

            override fun onSearchConfirmed(text: CharSequence?) {
                doSearch(text.toString())
                searchBar.clearFocus()
            }
        })

        // First load
        doSearch("")

        return root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun doSearch(query: String) {
        if (loading) {
            println("Still Loading!")
            return
        }
        adapter.dataSet.clear()
        adapter.notifyDataSetChanged()
        page = 1
        loading = true
        this.query = query

        GlobalScope.launch {
            println("Searching: $query, page: $page")
            try {
                adapter.dataSet.addAll(api.query(this@SearchFragment.query, page))
                mainActivity.runOnUiThread {
                    progressBar.isVisible = false
                    adapter.notifyDataSetChanged()
                    loading = false
                }
            } catch (ex: Exception) {
                mainActivity.runOnUiThread {
                    Toast.makeText(mainActivity, "Error: ${ex.message}", Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}