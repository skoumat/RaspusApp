package com.example.raspusapp

import android.app.Application
import com.example.raspusapp.data.LineRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

//class LinesApplication : Application() {
//    // No need to cancel this scope as it'll be torn down with the process
//    val applicationScope = CoroutineScope(SupervisorJob())
//
//    // Using by lazy so the database and the repository are only created when they're needed
//    // rather than when the application starts
//    val database = MyDatabase.getDatabase(this)
//    val repository = LineRepository(database.myDao())
//    }

