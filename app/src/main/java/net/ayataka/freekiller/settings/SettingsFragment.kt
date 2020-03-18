package net.ayataka.freekiller.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.ayataka.freekiller.R
import net.ayataka.freekiller.api.MangaSources
import net.ayataka.freekiller.config

class SettingsFragment : Fragment() {
    private val modesForButtons = mapOf(
        MangaSources.RAWDEVART to R.id.rawdevartButton,
        MangaSources.RAWKUMA to R.id.rawkumaButton,
        MangaSources.LHSCAN to R.id.loveHeavenButton,
        MangaSources.MANGA1000 to R.id.manga1000Button
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        root.findViewById<RadioButton>(modesForButtons[MangaSources.valueOf(config.data.selectedApi)] ?: error("")).isChecked = true

        MangaSources.values().forEach {
            root.findViewById<RadioButton>(modesForButtons[it] ?: error("")).setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    return@setOnCheckedChangeListener
                }

                config.data.selectedApi = it.name
                config.save()
                Toast.makeText(root.context, "Source: ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }
}