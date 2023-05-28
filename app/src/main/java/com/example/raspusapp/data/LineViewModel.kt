package com.example.raspusapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.raspusapp.data.DBLine
import com.example.raspusapp.data.LineRepository
import com.example.raspusapp.data.MyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LineViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var getAllLines: List<DBLine>
    private val lineRepository: LineRepository

    init {
        val myDao = MyDatabase.getDatabase(application).myDao()
        lineRepository = LineRepository(myDao)
        getAllLines = lineRepository.getAllLines
    }

    suspend fun insert(line: DBLine) {
        viewModelScope.launch(Dispatchers.IO) {
            lineRepository.insert(line)
        }
    }

    suspend fun deleteAll(){
        lineRepository.deleteAll()
    }
}