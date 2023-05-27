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

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
//    val allLines: LiveData<List<DBLine>> = repository.allWords.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    private val getAllLines: List<DBLine>
//    private val getAllLines: LiveData<List<DBLine>>
    private val repository: LineRepository
    init {
        val myDao = MyDatabase.getDatabase(application).myDao()
        repository = LineRepository(myDao)
        getAllLines = repository.getAllLines
    }

    fun insert(line: DBLine) {
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(line)
        }
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