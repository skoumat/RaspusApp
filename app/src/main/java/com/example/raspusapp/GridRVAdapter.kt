package com.example.raspusapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Filter
import java.util.Locale
import android.util.Log

internal class GridRVAdapter(
    private val lineList: List<GridViewModal>,
    private val mainActivity: MainActivity
) : BaseAdapter(), Filterable {

    var filteredData: List<GridViewModal> = lineList

    override fun getCount(): Int {
        return filteredData.size
    }

    override fun getItem(position: Int): Any? {
        return filteredData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.gridview_item, parent, false)

        var itemText = view.findViewById<TextView>(R.id.idTVLine)
        itemText.text = filteredData[position].line

        val soundFileName = filteredData[position].file

        view.setOnClickListener {
            mainActivity.play(soundFileName)
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.getDefault())

                val results = FilterResults()
                val filteredList = if (query.isNullOrBlank()) {
                    lineList
                } else {
                    lineList.filter { it.line.lowercase(Locale.getDefault()).contains(query) }
                }

                results.values = filteredList
                results.count = filteredList.size

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredData = results?.values as? List<GridViewModal> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}