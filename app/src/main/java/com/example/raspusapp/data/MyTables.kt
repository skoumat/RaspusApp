package com.example.raspusapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lines")
data class DBLine(@PrimaryKey(autoGenerate = true) val id : Long,
                  @ColumnInfo(name = "file") val file : String,
                  @ColumnInfo(name = "line") val line : String,
                  @ColumnInfo(name = "character") val character : String
//                  @ColumnInfo(name = "character2") val character2 : String
)