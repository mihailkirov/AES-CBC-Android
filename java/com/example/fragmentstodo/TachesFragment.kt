package com.example.fragmentstodo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable
import java.lang.Exception

class TachesFragment: Fragment() {

    private lateinit var adapter: TacheAdapter
    private lateinit var taches:MutableList<Tache>
    lateinit var lesTaches: RecyclerView
    lateinit var tacheinterf:TacheInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (savedInstanceState != null) {
            val d = savedInstanceState.getSerializable("list") as MutableList<*>
            taches = mutableListOf()
            for (e in d)
                    taches.add(e as Tache)
        }
        else
            initTaches()

        val v: View = inflater.inflate(R.layout.fragment_tache_list, container, false)
            lesTaches = v.findViewById(R.id.recyclerViewFragment)
            lesTaches.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TacheAdapter(taches, context, tacheinterf) // has to be attached before
                    addItemDecoration( DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                }

        return v
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("number", taches.size)
        val tmp = ArrayList<Tache>()
        for(e in taches)
            tmp.add(e)
        outState.putSerializable("list", tmp)

    }

   fun getListeTaches(): MutableList<Tache> {
       return taches
   }

    override fun onAttach(context: Context) {
        Log.d("fragment", "attach activity to fragment")
        super.onAttach(context)
        if( context is TacheInterface )
            tacheinterf = context

    }

    /*
    Initialising taches list from the database via the companion object
     */
    private fun initTaches() {
        try {
            taches = mutableListOf()
            val db = DbHelper(activity, null)
            val cursor = db.getAllTaches() ?: return

            cursor.moveToFirst()
            taches.add(
                Tache(
                    cursor.getString(cursor.getColumnIndex(DbHelper.NAME)),
                    cursor.getString(cursor.getColumnIndex(DbHelper.DUREE)).toInt(),
                    cursor.getString(cursor.getColumnIndex(DbHelper.DESCRIPTION)),
                    findCategorie(cursor.getString(cursor.getColumnIndex(DbHelper.CATEGORIE))),
                    cursor.getString(cursor.getColumnIndex(DbHelper.PLACE))
                )
            )

            while (cursor.moveToNext()) {
                taches.add(
                    Tache(
                        cursor.getString(cursor.getColumnIndex(DbHelper.NAME)),
                        cursor.getString(cursor.getColumnIndex(DbHelper.DUREE)).toInt(),
                        cursor.getString(cursor.getColumnIndex(DbHelper.DESCRIPTION)),
                        findCategorie(cursor.getString(cursor.getColumnIndex(DbHelper.CATEGORIE))),
                        cursor.getString(cursor.getColumnIndex(DbHelper.PLACE))
                    )
                )
            }
            cursor.close()
        }catch (e:Exception){
            println(e.stackTrace)
        }
    }

    fun addedTache(tache: Tache) {
        val db = DbHelper(activity, null)
        db.addTacheDb(tache)
        taches.add(tache)
        lesTaches.adapter?.notifyItemInserted(taches.size -1)
        lesTaches.scheduleLayoutAnimation()
    }

    fun findCategorie(catStr:String):Tache.Categorie{
        var cat:Tache.Categorie? = null
        cat = when(catStr){
            "Sport" -> Tache.Categorie.Sport
            "Travail" -> Tache.Categorie.Travail
            "Menage" -> Tache.Categorie.Menage
            "Lecture" -> Tache.Categorie.Lecture
            "Enfants" -> Tache.Categorie.Enfants
            "Courses" -> Tache.Categorie.Courses
            else -> Tache.Categorie.Autre
        }
        return cat
    }

    fun init_taches(){
        taches = mutableListOf(
            Tache("A", 10, "tache", Tache.Categorie.Travail, "F"),
            Tache("A", 10, "tache", Tache.Categorie.Sport, "E"),
            Tache("A", 10, "tache", Tache.Categorie.Enfants, "D"),
            Tache("A", 10, "tache", Tache.Categorie.Lecture, "C"),
            Tache("A", 10, "tache", Tache.Categorie.Menage, "B"),
            Tache("A", 10, "tache", Tache.Categorie.Travail, "A"))

    }
    fun update(){
        init_taches()
        lesTaches.adapter?.notifyDataSetChanged()
    }

    fun removeTache(t:Tache){

    }
}