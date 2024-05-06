package com.app.yoursafetyfirst.ui.prevoiusdata

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.response.DataX
import com.app.yoursafetyfirst.utils.DateConversion

class PreviousAdapter(
    val activity: Activity,
    val list: ArrayList<DataX>,
    val language: String,
    val onClick: (Int) -> Unit
) :
    RecyclerView.Adapter<PreviousAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousAdapter.ViewHolder {
        val contactView =
            LayoutInflater.from(parent.context).inflate(R.layout.previous_adapter, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: PreviousAdapter.ViewHolder, position: Int) {
        holder.linearlayout.setOnClickListener { onClick(position) }
        val date = DateConversion.utcConversion(list[position].createdAt)
        //val arr = date.split(" ")
        holder.date.text = date



        if (language == "Japanese" || language == "ja") {
            holder.level.text = list[position].level.ja
            holder.title.text = list[position].title.ja
        } else {
            holder.level.text = list[position].level.en
            holder.title.text = list[position].title.en
        }

        if (list[position].colorCode != null) {
            holder.cardView.setCardBackgroundColor(Color.parseColor(list[position].colorCode))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearlayout: LinearLayout = itemView.findViewById(R.id.linear)
        val date: TextView = itemView.findViewById(R.id.date_txt)
        val title: TextView = itemView.findViewById(R.id.title)
        val level: TextView = itemView.findViewById(R.id.level)
        val cardView: CardView = itemView.findViewById(R.id.card_view)

    }
}