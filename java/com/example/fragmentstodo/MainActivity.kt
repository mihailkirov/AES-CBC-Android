package com.example.fragmentstodo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.NewTache
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.password_dialog.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), TacheInterface {

    private lateinit var frag: TachesFragment
    private var requestcode = 1
    private var databaseTache:DbHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseTache = DbHelper(this, null)
        //init_database()

        frag = this.supportFragmentManager.findFragmentById(R.id.ListeTachesFragment)
                as TachesFragment

        if(DbHelper.handler.available) {
            fab.setOnClickListener { view ->
                val intent = Intent(view.context, NewTache::class.java)
                this.startActivityForResult(intent, requestcode)
            }
        }
    }

    private fun init_database() {
        // Test version
        databaseTache!!.clearData()
       val taches = mutableListOf(
            Tache("A", 10, "tache0", Tache.Categorie.Travail, "F"),
            Tache("B", 11, "tache1", Tache.Categorie.Sport, "E"),
            Tache("C", 12, "tache2", Tache.Categorie.Enfants, "D"),
            Tache("D", 13, "tache3", Tache.Categorie.Lecture, "C"),
            Tache("E", 14, "tache4", Tache.Categorie.Menage, "B"),
            Tache("F", 15, "tache5", Tache.Categorie.Travail, "A"))

        for(t in taches)
            databaseTache?.addTacheDb(t)

        Toast.makeText(this, "Database ready", Toast.LENGTH_LONG).show()
    }


    override fun selectedTache(t: Tache) {
        if (!resources.getBoolean(R.bool.DeuxFenetres)) {
            val intent = Intent(this, DetailTacheActivity::class.java)
            intent.putExtra("name", t.nom)
            intent.putExtra("dure", t.duree.toString())
            intent.putExtra("desc", t.description)
            intent.putExtra("categorie", t.categorie)
            this.startActivity(intent)
        }
        // landscape
        else {
            val frag2 =
                supportFragmentManager.findFragmentById(R.id.fragmentDetail) as DetailFragment
            frag2.showContent(t)
        }
    }

    override fun deleteTache(t: Tache) {
        databaseTache!!.removeTache(t)
    }


    /**
     * Launching a new intent to create 'tache'
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && this.requestcode == requestCode) {
            val name = intent?.getStringExtra("name")
            val dure = intent?.getStringExtra("duree")
            val desc = intent?.getStringExtra("descript")
            val categorie = intent?.getSerializableExtra("categorie")
            lateinit var realCat: Tache.Categorie
            realCat = when(categorie) {
                "Sport" -> Tache.Categorie.Sport
                "Courses" -> Tache.Categorie.Courses
                "Travail" -> Tache.Categorie.Travail
                "Lecture" -> Tache.Categorie.Lecture
                "Menage" -> Tache.Categorie.Menage
                "Enfants" -> Tache.Categorie.Enfants
                else -> Tache.Categorie.Autre

            }
            frag.addedTache(Tache(name, dure?.toInt(), desc, realCat))
            // notifying the adapter for the new change an on which position
            Toast.makeText(this, "New activity added", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Sending database file to the local server
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun sendFile(){
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        // building client for request
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val pathf = this.getDatabasePath(DbHelper.getName()).toString()
        val request = OkHttpRequest(client)
        request.POSTFile(pathf ,request.url_server, object: Callback {
            override fun onResponse(call: Call?, response: Response) {
                val responseData = response.body()?.string()
                runOnUiThread{
                    try {
                        val json = JSONArray(responseData!!)
                        if (json.get(1) == 200) {
                            println("Request Successful!!")
                        }
                        else
                            println("Something went wrong - ${json.get(0)}")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call?, e: IOException?) {
                println("Request Failure.")
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.send_menu, menu)
        return true
    }



    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalStdlibApi
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.Send -> {
                Log.d("msg", "Sending database")
                sendFile()
                true
            }
            R.id.DecryptD -> {
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.password_dialog, null)
                val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
                    .setTitle("Enter password for decryption")
                //show dialog
                val  mAlertDialog = mBuilder.show()
                //login button click of custom layout
                mDialogView.dialogOkBtn.setOnClickListener {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                    //get text from EditTexts of custom layout
                    val password = mDialogView.dialogPasswEt.text.toString()
                    decryptData(password)
                    //set the input text in TextView
                }
                //cancel button click of custom layout
                mDialogView.dialogCancelBtn.setOnClickListener {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                }
                true
            }
            R.id.EncryptD ->{
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.password_dialog, null)
                val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
                    .setTitle("Enter password for decryption")
                //show dialog
                val  mAlertDialog = mBuilder.show()
                //login button click of custom layout
                mDialogView.dialogOkBtn.setOnClickListener {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                    //get text from EditTexts of custom layout
                    val password = mDialogView.dialogPasswEt.text.toString()
                    encryptData(password)
                    //set the input text in TextView
                }
                //cancel button click of custom layout
                mDialogView.dialogCancelBtn.setOnClickListener {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun encryptData(password:String){
        val map = hashMapOf<String, ByteArray>()
        databaseTache?.encryptDatabase( password.toCharArray(), map)
        val ivBase64String = Base64.encodeToString(map["iv"], Base64.NO_WRAP)
        val saltBase64String = Base64.encodeToString(map["salt"], Base64.NO_WRAP)
        val editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
        editor.putString("lsalt", saltBase64String)
        editor.putString("liv", ivBase64String)
        editor.apply()
        frag.update()
        Toast.makeText(this, "Database encrypted", Toast.LENGTH_LONG).show()
    }

    fun decryptData(password: String){
        val preferences = getSharedPreferences("MyPrefs",
            Context.MODE_PRIVATE)
        val base64Salt = preferences.getString("lsalt", "")
        val base64Iv = preferences.getString("liv", "")
        //Base64 decode
        val iv = Base64.decode(base64Iv, Base64.NO_WRAP)
        val salt = Base64.decode(base64Salt, Base64.NO_WRAP)
        databaseTache?.decryptDatabase(password.toCharArray(), iv, salt)
        frag.update()
        Toast.makeText(this, "Database decrypted", Toast.LENGTH_LONG).show()
    }

}