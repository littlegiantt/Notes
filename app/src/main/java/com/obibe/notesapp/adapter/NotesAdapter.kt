package com.obibe.notesapp.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.obibe.notesapp.R
import com.obibe.notesapp.entities.Notes
import kotlinx.android.synthetic.main.item_note.view.*

class NotesAdapter() : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    var listener: OnItemClickListener? = null
    var arrList = ArrayList<Notes>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotesAdapter.NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Notes>) {
        arrList = arrNotesList as ArrayList<Notes>
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    override fun onBindViewHolder(holder: NotesAdapter.NotesViewHolder, position: Int) {

        if (arrList[position].title != "") {
            holder.itemView.title_tv.text = arrList[position].title
        } else {
            holder.itemView.title_tv.visibility = View.GONE
        }

        holder.itemView.decs_tv.text = arrList[position].noteText
        holder.itemView.dateTime_tv.text = arrList[position].dateTime

        if (arrList[position].color != null) {
            holder.itemView.card_view.setCardBackgroundColor(Color.parseColor(arrList[position].color))
        }

        if (arrList[position].imgPath != null) {
            holder.itemView.img_note.setImageBitmap(BitmapFactory.decodeFile(arrList[position].imgPath))
            holder.itemView.img_note.visibility = View.VISIBLE
        } else  {
            holder.itemView.img_note.visibility = View.GONE
        }

        if (arrList[position].webLink != "") {
            holder.itemView.webLink_tv.text = arrList[position].webLink
            holder.itemView.webLink_tv.visibility = View.VISIBLE
        } else {
            holder.itemView.webLink_tv.visibility = View.GONE
        }

        holder.itemView.card_view.setOnClickListener {
            listener!!.onClicked(arrList[position].id!!)
        }
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    interface OnItemClickListener {
        fun onClicked(noteId:Int)
    }
}