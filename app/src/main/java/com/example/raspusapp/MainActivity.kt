package com.example.raspusapp

import com.example.raspusapp.data.MyDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.raspusapp.data.DBLine
import kotlinx.coroutines.*



class MainActivity : AppCompatActivity() {
    lateinit var lineGRV: GridView
    lateinit var lineList: List<GridViewModal>
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = MyDatabase.getDatabase(this)

//        runBlocking {
//            database.myDao().deleteAll()
//        }
        lateinit var mLineViewModel: LineViewModel
        coroutineScope.launch {
//            database.myDao().deleteAll()


            mLineViewModel = ViewModelProvider(this@MainActivity).get(LineViewModel::class.java)
            mLineViewModel.deleteAll()
            var line = DBLine(0, "Wong_kojoti", "Kojoti", "pan Wong")
            mLineViewModel.insert(line)
        }
//        val rowsDeleted =
//        Log.d("Database", "Rows deleted: $rowsDeleted")







//        Log.d("Database", "Line inserted!")
        val allLines = database.myDao().getAllLines()




        lineGRV = findViewById(R.id.idGRV)
        lineList = ArrayList<GridViewModal>()

        allLines.forEach {
            lineList = lineList + GridViewModal(it.line, it.file)
        }

        val lineAdapter = GridRVAdapter(lineList = lineList, this@MainActivity)

        lineGRV.adapter = lineAdapter
        lineGRV.setOnItemClickListener { _, _, position, _ -> play(lineList[position].file) }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
    fun play(file: String) {
        val player = MediaPlayer()
        try {
            val afd = applicationContext.getAssets().openFd("Audio/$file.mp3")
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength())
            afd.close()
            player.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        player.start()
    }
}