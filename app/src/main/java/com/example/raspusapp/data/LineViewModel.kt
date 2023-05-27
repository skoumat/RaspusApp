package com.example.raspusapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.raspusapp.data.DBLine
import com.example.raspusapp.data.LineRepository
import com.example.raspusapp.data.MyDao
import com.example.raspusapp.data.MyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LineViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var getAllLines: List<DBLine>

    //    private val getAllLines: LiveData<List<DBLine>>
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

//class LineViewModelFactory(private val repository: LineRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(LineViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return LineViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}