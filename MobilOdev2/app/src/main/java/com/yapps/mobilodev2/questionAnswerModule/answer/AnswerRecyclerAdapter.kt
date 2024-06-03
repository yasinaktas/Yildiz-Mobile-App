package com.yapps.mobilodev2.questionAnswerModule.answer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yapps.mobilodev2.R
import com.yapps.mobilodev2.questionAnswerModule.question.Listener
import com.yapps.mobilodev2.questionAnswerModule.question.Question

class AnswerRecyclerAdapter(private val clickListener: OnItemClickListener): RecyclerView.Adapter<AnswerRecyclerAdapter.AnswerViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(item: Answer, isChecked: Boolean)
    }

    var data = listOf<Answer>()
        set(value){
            field = value
            notifyDataSetChanged()
        }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : AnswerViewHolder = AnswerViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item,clickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val question: TextView = itemView.findViewById(R.id.text)
        private val checkbox: CheckBox = itemView.findViewById(R.id.check_box)
        companion object{
            fun inflateFrom(parent: ViewGroup): AnswerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_checkbox, parent, false) as View
                return AnswerViewHolder(view)
            }
        }
        fun bind(item:Answer,listener: OnItemClickListener){
            question.text = item.answer
            checkbox.isChecked = item.checked
            checkbox.isClickable = false
            /*checkbox.setOnCheckedChangeListener { _, isChecked ->
                item.checked = isChecked
                listener.onItemClick(item)
            }*/
            itemView.setOnClickListener{
                checkbox.isChecked = !checkbox.isChecked
                listener.onItemClick(item,checkbox.isChecked)
            }
        }
    }

}

