package com.example.raspusapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {
    // je tam treba to order by?  ORDER BY id ASC
    @Query("SELECT * FROM lines ORDER BY id ASC")
    fun getAllLines () : List<DBLine>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(line: DBLine)

    @Query("DELETE FROM lines")
    suspend fun deleteAll(): Int

//    @Query("SELECT * FROM lines ORDER BY line ASC")
//    fun getAllLinesOrderedByLine () : List<DBLine>
//
//    // bude to porad zorganizovane podle casu?  NULLS FIRST na konci nefunguje :/
//    @Query("SELECT * FROM lines i WHERE i.character = :character OR i.character2 = :character ORDER BY character2")
//    fun getCharacterLines(character : String) : List<DBLine>
//
//    @Query("SELECT * FROM lines i WHERE i.line LIKE :line COLLATE NOCASE")
//    fun getLinesByName(line : String) : List<DBLine>
}