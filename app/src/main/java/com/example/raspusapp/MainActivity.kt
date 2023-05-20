package com.example.raspusapp

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class MainActivity : AppCompatActivity() {
    lateinit var lineGRV: GridView
    lateinit var lineList: List<GridViewModal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database = MyDatabase.getDatabase(this, CoroutineScope(SupervisorJob()))
        val allLines = database.myDao().getAllLines()

        lineGRV = findViewById(R.id.idGRV)
        lineList = ArrayList<GridViewModal>()

//        allLines.forEach{
//            lineList = lineList + GridViewModal(it.line, it.file)
//        }

        val lineAdapter = GridRVAdapter(lineList = lineList, this@MainActivity)

        lineGRV.adapter = lineAdapter
        lineGRV.setOnItemClickListener { _, _, position, _ -> play(lineList[position].file) }
    }

    fun play(file : String){
        val player = MediaPlayer()
        try {
            val afd = applicationContext.getAssets().openFd("Audio/$file.mp3")
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength())
            afd.close()
            player.prepare()
        }

        catch(e: Exception){
            e.printStackTrace()
        }
        player.start()
    }
}