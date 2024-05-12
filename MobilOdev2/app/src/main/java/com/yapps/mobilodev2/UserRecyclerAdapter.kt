package com.yapps.mobilodev2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserRecyclerAdapter(var editor:Boolean = false, var users: List<String> = listOf(), var listener: (User, Boolean) -> Unit) : RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder>(){

    var data = listOf<User>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : UserViewHolder = UserViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = data[position]
        holder.bind(listener,editor,item,isUserSelected(data[position]))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textView: TextView = itemView.findViewById(R.id.text)
        private val checkBox: CheckBox = itemView.findViewById(R.id.check_box)
        companion object{
            fun inflateFrom(parent: ViewGroup): UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.user_list_item, parent, false) as View
                return UserViewHolder(view)
            }
        }
        fun bind(listener: (User, Boolean) -> Unit, editor: Boolean, item: User, isSelected:Boolean){
            textView.text = item.email
            if(!editor){
                checkBox.visibility = View.GONE
            }else{
                checkBox.visibility = View.VISIBLE
            }

            checkBox.isChecked = isSelected

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                listener(item,isChecked)
            }

        }

    }

    private fun isUserSelected(user:User):Boolean{
        for(userInList in users){
            if(user.id.toString() == userInList){
                return true
            }
        }
        return false
    }

}