package com.example.fragmentstodo

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TacheAdapter(var listeTaches:MutableList<Tache>, var context: Context?, var communiqueInter:TacheInterface) :
    RecyclerView.Adapter<TacheAdapter.TacheViewHolder>(){

    // Providing data for all views to the view holder
    inner class TacheViewHolder(private val tacheview:View) : RecyclerView.ViewHolder(tacheview){
        // A holder contains an image and a text
        var text:TextView = tacheview.findViewById(R.id.tache)
        var image:ImageView = tacheview.findViewById(R.id.Image)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TacheAdapter.TacheViewHolder {
        val tacheview = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)

        return TacheViewHolder(tacheview)
    }


    // Return the size of the data structure
    override fun getItemCount(): Int {
        return listeTaches.size
    }

    // choose layout(int) to return in function of the tache at position positon
    override fun getItemViewType(position: Int): Int {
       return R.layout.tache_lay

    }

    // binding resources
    override fun onBindViewHolder(holder: TacheAdapter.TacheViewHolder, position: Int) {

        holder.text.text = "${listeTaches[position].categorie}"
        when (listeTaches[position].categorie) {
            Tache.Categorie.Travail -> holder.image.setImageResource(R.drawable.travail)
            Tache.Categorie.Sport -> holder.image.setImageResource(R.drawable.sport)
            Tache.Categorie.Menage -> holder.image.setImageResource(R.drawable.menage)
            Tache.Categorie.Lecture -> holder.image.setImageResource(R.drawable.lecture)
            Tache.Categorie.Enfants -> holder.image.setImageResource(R.drawable.enfant)
            Tache.Categorie.Courses -> holder.image.setImageResource(R.drawable.courses)
            else -> holder.image.setImageResource(R.drawable.point_interro_)
        }

        // adding a click listener

        holder.itemView.setOnClickListener {
          communiqueInter.selectedTache(listeTaches[position]) // binding the tache to a method
        }
        // on hold click listener
        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.delete_message)
            builder.setPositiveButton(R.string.OK) { dialog, which ->
                communiqueInter.deleteTache(listeTaches[position])
                listeTaches.removeAt(position)

                this.notifyDataSetChanged()
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.CANCEL) { dialog, which ->
                dialog.dismiss()
            }
            builder.create().show()
            true
        }


    }
}