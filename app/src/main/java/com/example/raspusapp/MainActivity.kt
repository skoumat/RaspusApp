package com.example.raspusapp

import com.example.raspusapp.data.MyDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.raspusapp.data.DBLine
import kotlinx.coroutines.*
import android.widget.SearchView
import com.example.raspusapp.databinding.ActivityMainBinding

import android.util.Log


class MainActivity : AppCompatActivity() {
    lateinit var lineGRV: GridView
    lateinit var lineList: List<GridViewModal>
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        val database = MyDatabase.getDatabase(this) // ziskame instanci databaze


        lateinit var mLineViewModel: LineViewModel
        coroutineScope.launch {
            mLineViewModel = ViewModelProvider(this@MainActivity).get(LineViewModel::class.java)
            mLineViewModel.deleteAll()

            populateDatabase(mLineViewModel)
        }



        var shownLines = database.myDao().getAllLines()


        lineGRV = findViewById(R.id.GRV)
        lineList = ArrayList<GridViewModal>()

        shownLines.forEach {
            lineList = lineList + GridViewModal(it.line, it.file)
        }

        var lineAdapter = GridRVAdapter(lineList = lineList, this@MainActivity)

        lineGRV.adapter = lineAdapter
        lineGRV.setOnItemClickListener { _, _, position, _ -> play(lineList[position].file) }




//        binding = ActivityMainBinding.inflate(layoutInflater)
        // k cemu to je? potrebuju to?
//        setContentView(binding.root)
//        binding.GRV.adapter = lineAdapter

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searched: String): Boolean {
                Log.d("SEARCH", "String $searched")
                lineAdapter.filter.filter(searched)

                return false
            }
        })

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

    suspend fun populateDatabase(mLineViewModel: LineViewModel) {

        val wong = "pan Wong"
        val spusa = "Raspuša"
        val extras = "Extras"
        val norbit = "Norbit"
        val kate = "Kate"
        val noone = ""

        val lineList = listOf(
            DBLine(0, "Wong_kojoti", "Kojoti", wong, noone),
            DBLine(0, "Wong_dalsi_negr", "Další negr", wong, noone),
            DBLine(0, "Wong_seredna_cernoch", "Šeredná černoch", wong, noone),
            DBLine(0, "Wong_glutamat", "Glutamát", wong, noone),
            DBLine(0, "Wong_to_byl_veleryba", "To být velryba", wong, noone)
        )

        lineList.forEach {
            mLineViewModel.insert(it)
        }
    }
}