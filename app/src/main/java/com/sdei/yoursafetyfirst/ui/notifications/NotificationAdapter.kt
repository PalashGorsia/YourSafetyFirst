package com.app.yoursafetyfirst.ui.notifications

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.response.Notification
import com.app.yoursafetyfirst.utils.DateConversion
import com.app.yoursafetyfirst.utils.NotificationType

class NotificationAdapter(
    val activity: Activity,
    private val notificationList: List<Notification>,
    val language: String,
    val click: (Int) -> Unit
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.ViewHolder {
        val contactView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_adapter, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        holder.linearLayout.setOnClickListener {
            click(position)
        }

        when (language) {
            "en" -> {
                //holder.description.text = notificationList[position].message.en
                 holder.title.text = notificationList[position].title?.en
            }

            "ja" -> {
                //holder.description.text = notificationList[position].message.ja
                holder.title.text = notificationList[position].title?.ja
            }

            else -> {
                //holder.description.text = notificationList[position].message.en
                holder.title.text = notificationList[position].title?.en
            }
        }

        holder.time.text = DateConversion.utcConversion(notificationList[position].createdAt!!)


        when (notificationList[position].type) {
            NotificationType.NoticeSDNC.getValue() -> {
                holder.image_type.load(R.drawable.information)
            }

            NotificationType.NoticeApplication.getValue() -> {
                holder.image_type.load(R.drawable.tool_icon)
            }

            NotificationType.NoticeTransportation.getValue() -> {
                holder.image_type.load(R.drawable.baseline_warning_24)

            }
            else ->{
                holder.image_type.load(R.drawable.baseline_warning_24)
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linear)

        val title: TextView = itemView.findViewById(R.id.title)
        val time: TextView = itemView.findViewById(R.id.time)
        //val description: TextView = itemView.findViewById(R.id.description)
        val image_type: ImageView = itemView.findViewById(R.id.image_type)

    }
}