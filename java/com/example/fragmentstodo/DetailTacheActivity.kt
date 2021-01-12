package com.example.fragmentstodo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DetailTacheActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailactivity)
        getIncomingIntent()

    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("name") && intent.hasExtra("dure") &&
            intent.hasExtra("desc") && intent.hasExtra("categorie")
        )
        {
            val fragm = supportFragmentManager.findFragmentById(R.id.fragmentDetail)
                    as DetailFragment
            fragm.showContent(Tache(intent.getStringExtra("name"),
                intent.getStringExtra("dure")?.toInt(),
                intent.getStringExtra("desc"),
                intent.getSerializableExtra("categorie") as Tache.Categorie))
        }
    }
}