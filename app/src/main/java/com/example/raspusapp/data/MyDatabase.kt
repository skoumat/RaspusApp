package com.example.raspusapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.ContentValues

@Database(entities = [DBLine::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    abstract fun myDao(): MyDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_database")
                //.fallbackToDestructiveMigration()
//                        .addCallback(MyDatabaseCallback)
                        .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }


        private var MyDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.insert("lines", 0, fillInfo(0, "Wong_kojoti", "Kojoti", "pan Wong"))

//                val wong = "pan Wong"
//                val spusa = "Raspuša"
//                val extras = "Extras"
//                val norbit = "Norbit"
//                val kate = "Kate"
//                val noone = ""

//                db.insert("lines", 0, fillInfo(0, "Wong_kojoti", "Kojoti", "pan Wong"))
//                db.insert("lines", 0, fillInfo(0, "Wong_dalsi_negr", "Další negr", wong, noone))
//                db.insert("lines", 0, fillInfo(0, "Wong_glutamat", "Glutamát", wong, noone))
//                db.insert("lines", 0, fillInfo(0, "Wong_to_byl_veleryba", "To být velryba", wong, noone))

//                val lineList = listOf(
//                    Line("Wong_kojoti", "Kojoti", wong, noone),
//                    Line("Wong_dalsi_negr", "Další negr", wong, noone),
//                    Line("Wong_seredna_cernoch", "Šeredná černoch", wong, noone),
//                    Line("Wong_glutamat", "Glutamát", wong, noone),
//                    Line("Wong_to_byl_veleryba", "To být velryba", wong, noone)
//                )
//
//                var i : Long = 0
//                lineList.forEach {
//                    db.insert("lines", 0, fillInfo(i, it.file, it.line, it.character, it.character2))
//                    i++
//                }
            }


            private fun fillInfo(
                id: Long,
                file: String,
                line: String,
                character: String
            ): ContentValues {
                val values = ContentValues()
                values.put("id", id)
                values.put("file", file)
                values.put("line", line)
                values.put("character", character)
//                if(character2 != "")
//                    values.put("character2", character2)

                return values
            }
            //data class Line(val file:String, val line:String, val character:String, val character2: String)

        }

        suspend fun populateDatabase(myDao: MyDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
//            myDao.deleteAll()

            var line = DBLine(0, "Wong_kojoti", "Kojoti", "pan Wong")
            myDao.insert(line)
        }
    }
}


