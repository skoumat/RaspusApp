package com.example.raspusapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.raspusapp.LineListAdapter.LineViewHolder

class LineListAdapter : ListAdapter<DBLine, LineViewHolder>(WORDS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        return LineViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.line)
    }

    class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            wordItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): LineViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return LineViewHolder(view)
            }
        }
    }

    companion object {
        private val WORDS_COMPARATOR = object : DiffUtil.ItemCallback<DBLine>() {
            override fun areItemsTheSame(oldItem: DBLine, newItem: DBLine): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DBLine, newItem: DBLine): Boolean {
                return oldItem.line == newItem.line
            }
        }
    }
}
