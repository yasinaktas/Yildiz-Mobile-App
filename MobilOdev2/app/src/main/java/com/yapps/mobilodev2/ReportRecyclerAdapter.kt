package com.yapps.mobilodev2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class ReportRecyclerAdapter: RecyclerView.Adapter<ReportRecyclerAdapter.ReportViewHolder>(){

    var data = listOf<Report>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : ReportViewHolder = ReportViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item,holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val scope: TextView = itemView.findViewById(R.id.scope)
        private val recipient: TextView = itemView.findViewById(R.id.recipient)
        private val subject: TextView = itemView.findViewById(R.id.subject)
        private val body: TextView = itemView.findViewById(R.id.body)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val course: TextView = itemView.findViewById(R.id.name)
        companion object{
            fun inflateFrom(parent: ViewGroup): ReportViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_report, parent, false) as View
                return ReportViewHolder(view)
            }
        }
        fun bind(item:Report,context:Context){
            scope.text = "Scope: ${item.scope}"
            recipient.text = "Recipient: ${item.recipient}"
            course.text = "Course: ${item.courseId}"
            subject.text = "Subject: ${item.subject}"
            body.text = "Body: ${item.body}"
            date.text = "Date: ${SimpleDateFormat("dd.MM.yyyy HH.mm").format(Date(item.date))}"
        }

    }

}