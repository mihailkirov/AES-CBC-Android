package com.example.fragmentstodo

import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase

/**
 * Modified database error handler for not deleting
 * an encrypted(corrupted ) sqlite file
 */

class Dberrorhandler: DatabaseErrorHandler {
    var available:Boolean=true
    override fun onCorruption(dbObj: SQLiteDatabase?) {
        available = false

    }

}