package com.example.raspusapp.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.LiveData

class LineRepository(private val myDao: MyDao)  {
    val getAllLines: List<DBLine> = myDao.getAllLines()
//    val getAllLines: LiveData<List<DBLine>> = myDao.getAllLines()


//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
    suspend fun insert(line: DBLine) {
        myDao.insert(line)
    }
}