package com.example.bitfit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bitfit.databinding.ItemEntryBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class EntryAdapter : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {
    private var entries: List<WaterEntry> = emptyList()
    private val formatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())

    fun submitList(newEntries: List<WaterEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) = holder.bind(entries[position])
    override fun getItemCount(): Int = entries.size

    inner class EntryViewHolder(private val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: WaterEntry) {
            val date = runCatching { LocalDate.parse(entry.date).format(formatter) }.getOrDefault(entry.date)
            binding.entryDate.text = if (entry.isDemo) "$date · demo" else date
            binding.entryOunces.text = "${entry.ounces} oz"
        }
    }
}
