package com.example.todolist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fragmentstodo.R

class NewTache : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_tache)

    }

    // Opening the alert dialog for the category choice
    fun openDiaog(view: View) {

        // Alert  Dialog part
        val dial = AlertDialog.Builder(this) // building new dialog
        dial.setTitle("Choisissez a category")
        val items = arrayOf("Sport", "Menage", "Enfants", "Lecture", "Autre",
            "Travail", "Courses")
        dial.setSingleChoiceItems(items, -1 ) {
                dialog, which ->
            // The choice will be found in the value of the button
            findViewById<Button>(R.id.categorie).text = items[which]  // setting the text on the buttons
            dialog.dismiss()
        }

        // Setting the cancel button
        dial.setNegativeButton("Cancel"){
                dialog, which -> dialog.cancel()
        }

        // create the dialog
        val mDial = dial.create()
        mDial.show()

    }


    fun saveNewTache(view: View) {
        // Get the input (not checked for errors)
        val name:String = findViewById<EditText>(R.id.nameTacheEdit).text.toString()
        val dure:String = findViewById<EditText>(R.id.dureeTacheEdit).text.toString()
        val categorie:String = findViewById<Button>(R.id.categorie).text.toString()
        val descript:String = findViewById<EditText>(R.id.descriptionTacheEdit).text.toString()
        // if no contents added
        if(name.isEmpty() && dure.isEmpty() && categorie.isEmpty() && descript.isEmpty()) {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
        try {
            // intent associated with the mainActivity (parent activity)
            val intent = Intent()
            // Sent the new tache to the recycler holder
            intent.putExtra("name", name)
            intent.putExtra("categorie", categorie)
            intent.putExtra("descript", descript)
            dure.toInt()
            intent.putExtra("duree", dure)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }catch (e:NumberFormatException){
            Toast.makeText(this, "Duree doit etre un nombre", Toast.LENGTH_LONG).show()
        }

    }
}