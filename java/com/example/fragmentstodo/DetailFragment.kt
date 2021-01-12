package com.example.fragmentstodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class DetailFragment:Fragment() {

    var nom:TextView? = null
    var desc:TextView? = null
    var categorie:ImageView? = null
    var dure:TextView? = null
    var place:TextView? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v: View = inflater.inflate(R.layout.details_tache, container, false)
        nom = v.findViewById(R.id.nomTache)
        desc = v.findViewById(R.id.description)
        dure = v.findViewById(R.id.duree)
        categorie = v.findViewById(R.id.detailImage)
        place = v.findViewById(R.id.place)
        return v
    }

    fun showContent(t:Tache){
        nom?.text = t.nom
        desc?.text = t.description
        dure?.text = t.duree.toString()
        place?.text = t.place

        when(t.categorie){
            Tache.Categorie.Courses -> categorie?.setImageResource(R.drawable.courses)
            Tache.Categorie.Sport -> categorie?.setImageResource(R.drawable.sport)
            Tache.Categorie.Travail ->  categorie?.setImageResource(R.drawable.travail)
            Tache.Categorie.Menage -> categorie?.setImageResource(R.drawable.menage)
            Tache.Categorie.Enfants -> categorie?.setImageResource(R.drawable.enfant)
            Tache.Categorie.Lecture ->categorie?.setImageResource(R.drawable.lecture)
            else -> categorie?.setImageResource(R.drawable.point_interro_)
        }
    }
}