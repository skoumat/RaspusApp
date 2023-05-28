package com.example.raspusapp.data

class LineRepository(private val myDao: MyDao)  {
    val getAllLines: List<DBLine> = myDao.getAllLines()

    suspend fun insert(line: DBLine) {
        myDao.insert(line)
    }

    suspend fun deleteAll(){
        myDao.deleteAll()
    }
}