package com.buptsdmda.openbyrtv

import android.content.Context
import android.database.Cursor
import android.database.sqlite.*
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.*

class SQLiteHelper(context: Context) : ManagedSQLiteOpenHelper(context, "userdata") {

    companion object {
        private var instance: SQLiteHelper? = null

        @Synchronized
        fun getInstance(context: Context): SQLiteHelper {
            if (instance == null) {
                instance = SQLiteHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("User", true,
            "_id" to INTEGER + PRIMARY_KEY,
            "username" to TEXT,
            "password" to TEXT,
            "success" to INTEGER)
        db.insert("User","_id" to 0,"username" to "","password" to "","success" to 0)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {


    }

}