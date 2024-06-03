package com.yapps.mobilodev2.questionAnswerModule.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yapps.mobilodev2.R

class QuestionRecyclerAdapter(private val clickListener: OnItemClickListener): RecyclerView.Adapter<QuestionRecyclerAdapter.QuestionViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(item: Question)
    }

    var data = listOf<Question>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    var dataCheckBox = mutableListOf<Boolean>()

    private val listener:Listener = object : Listener{
        override fun onItemClicked(position: Int, isChecked: Boolean) {
            dataCheckBox[position] = isChecked
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : QuestionViewHolder = QuestionViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item,position,listener)

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val question: TextView = itemView.findViewById(R.id.text)
        private val checkbox: CheckBox = itemView.findViewById(R.id.check_box)
        companion object{
            fun inflateFrom(parent: ViewGroup): QuestionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_checkbox, parent, false) as View
                return QuestionViewHolder(view)
            }
        }
        fun bind(item:Question,position:Int,listener: Listener){
            question.text = item.question
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                listener.onItemClicked(position,isChecked)
            }
        }



    }

}

interface Listener{
    fun onItemClicked(position: Int,isChecked:Boolean)
}

