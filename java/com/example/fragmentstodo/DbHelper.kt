package com.example.fragmentstodo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.*

class DbHelper(context:Context?, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION, handler){

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "TacheDatabase"
        val TABLE_NAME = "Taches"
        val KEY_ID = "id"
        val NAME = "nom"
        val DUREE = "duree"
        val DESCRIPTION = "description"
        val CATEGORIE = "categorie"
        val PLACE = "place"
        val handler:Dberrorhandler = Dberrorhandler()
        var path = "/data/user/0/com.example.fragmentstodo/databases/${DATABASE_NAME}"
        fun getName():String{
            return DATABASE_NAME
        }

    }


    override fun onCreate(db: SQLiteDatabase?) {
    try {
        val CREATE_TACHE_TABLE = "CREATE TABLE  " +
                TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                NAME + " TEXT NOT_NULL," +
                DUREE + " INTEGER" + " NOT_NULL," +
                DESCRIPTION + " TEXT," +
                CATEGORIE + " TEXT," +
                PLACE + " TEXT NOT_NULL" + ")"
        db?.execSQL(CREATE_TACHE_TABLE)

    }catch (e:java.lang.Exception){
        Log.d("Here it is", "cad")
    }

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTacheDb(tache: Tache) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DUREE, tache.duree)
        values.put(CATEGORIE, tache.categorie.toString())
        values.put(PLACE, tache.place)
        values.put(DESCRIPTION, tache.description)
        values.put(NAME, tache.nom)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    /*
    GET all database contents
     */
    fun getAllTaches(): Cursor? {


        val integ = this.readableDatabase.isDatabaseIntegrityOk()
        return if (integ) {
            this.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
        } else {
            null
        }
    }

    fun getCount(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(db, "table_name").toInt()
    }

    fun clearData() {
        try {
            val db = this.writableDatabase

            db.delete(TABLE_NAME, null, null)

        }catch (e:Exception){
            Log.d("Open database problem", e.stackTrace.toString())
        }
    }

    fun removeTache(t:Tache){

        val db = this.writableDatabase.also {
            it.execSQL("DELETE FROM ${TABLE_NAME} WHERE ${NAME} IN (?) AND " +
                    "${CATEGORIE} IN (?) AND ${DUREE} IN (?)" +
                    "AND ${DESCRIPTION} IN (?)", arrayOf(t.nom.toString(), t.categorie.toString(),
                t.duree.toString(), t.description.toString())
            )
        }
    }
    fun encryptDatabase(password:CharArray, map:HashMap<String, ByteArray>) {
        try {
            val fileData = getDataDb()
            val encrypt = Encrypt()
            val encrypted: ByteArray = encrypt.ecnrypt(fileData, password, map)
            saveDataDb(encrypted)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    fun decryptDatabase(password:CharArray, iv:ByteArray, salt:ByteArray) {
        try {

            val fileData = getDataDb()
            val decrypted = Encrypt().decrypt(fileData, password, salt, iv)
            if(decrypted!=null) {
                saveDataDb(decrypted)
            }
        }catch (e:Exception){
            println("error - " + e.message)
            e.stackTrace
        }
    }

    @Throws(Exception::class)
    private fun getDataDb(): ByteArray{
        val file = File(path)
        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(
            FileInputStream(file)
        )
        inputBuffer.read(fileContents)
        inputBuffer.close()
        return fileContents
    }

    @Throws(Exception::class)
    private fun saveDataDb(fileData:ByteArray){
        val file = File(path)
        val bos = BufferedOutputStream(FileOutputStream(file, false))
        bos.write(fileData)
        bos.flush()
        bos.close()
    }
}