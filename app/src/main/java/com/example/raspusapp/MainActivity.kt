package com.example.raspusapp

import com.example.raspusapp.data.MyDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.raspusapp.data.DBLine
import kotlinx.coroutines.*
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.raspusapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {
    lateinit var lineGRV: GridView
    lateinit var lineList: List<GridViewModal>
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit private var database: MyDatabase

    lateinit var binding: ActivityMainBinding
    lateinit var dwLayout: DrawerLayout
    lateinit var drawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    private lateinit var lineAdapter :GridRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_filter_alt_24)
        setSupportActionBar(toolbar)

        dwLayout = binding.drawerLayout

        binding.apply {
            drawerToggle = ActionBarDrawerToggle(
                this@MainActivity,
                dwLayout,
                toolbar,
                R.string.open,
                R.string.close
            )
            dwLayout.addDrawerListener(drawerToggle)
            drawerToggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navigationView = findViewById(R.id.nav_view)
            navigationView.setNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.spusa -> {
                        filterCharacter("Raspuša")
                    }
                    R.id.norbit -> {
                        filterCharacter("Norbit")
                    }
                    R.id.wong -> {
                        filterCharacter("pan Wong")
                    }
                    R.id.extras -> {
                        filterCharacter("Extras")
                    }
                    R.id.kate -> {
                        filterCharacter("Kate")
                    }
                    R.id.time -> {
                        sortDatabase("Čas")
                    }
                    R.id.title -> {
                        sortDatabase("Název")
                    }
                    else -> {
                        filterCharacter("All")
                    }
                }
                true
            }
        }


        database = MyDatabase.getDatabase(this) // ziskame instanci databaze

        lateinit var mLineViewModel: LineViewModel
        coroutineScope.launch {
            mLineViewModel = ViewModelProvider(this@MainActivity).get(LineViewModel::class.java)
            mLineViewModel.deleteAll()

            populateDatabase(mLineViewModel)
        }

        var shownLines = database.myDao().getAllLines()

        pupulateGV(shownLines)

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searched: String): Boolean {
                lineAdapter.filter.filter(searched)
                return false
            }
        })

//        val filters: Array<String> =
//            arrayOf("All", "Raspuša", "Norbit", "pan Wong", "Kate", "Extras")
//
//        val filtersACTV = findViewById<AutoCompleteTextView>(R.id.filters_act)
//        val adapterFilters = ArrayAdapter<String>(this, R.layout.filter_item, filters)
//        filtersACTV.setAdapter(adapterFilters)
//        filtersACTV.setOnItemClickListener { parent: AdapterView<*>, _, position, _ ->
//            val filter: String = parent.getItemAtPosition(position).toString()
//            filterCharacter(filter)
//        }


//        val sort_by: Array<String> =
//            arrayOf("Čas", "Název")
//
//        val sortByACTV = findViewById<AutoCompleteTextView>(R.id.sort_by_act)
//        val adapterSortBy = ArrayAdapter<String>(this, R.layout.filter_item, sort_by)
//        sortByACTV.setAdapter(adapterSortBy)
//        sortByACTV.setOnItemClickListener { parent: AdapterView<*>, _, position, _ ->
//            val by: String = parent.getItemAtPosition(position).toString()
//            sortDatabase(by)
//        }

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
        val noone = "noone"

        val lineList = listOf(
            DBLine(0, "Wong_kojoti", "Kojoti", wong, noone),
            DBLine(0, "Wong_dalsi_negr", "Další negr", wong, noone),
            DBLine(0, "Wong_seredna_cernoch", "Šeredná černoch", wong, noone),
            DBLine(0, "Wong_glutamat", "Glutamát", wong, noone),
            DBLine(0, "Wong_to_byl_veleryba", "To být velryba", wong, noone),
            DBLine(0, "Wong_ty_parchant", "Ty parchant", wong, noone),
            DBLine(0, "Wong_bingo", "Bingo", wong, noone),
            DBLine(0, "Kate_to_nevadi_Norbite", "To nevadí Norite", kate, noone),
            DBLine(0, "Kate_jabko", "Jablko", kate, noone),
            DBLine(0, "Kate&Norbit_pusa", "Pusa", kate, norbit),
            DBLine(0, "Kate_ahoj_norbiteee", "Ahoj, Norbite", kate, noone),
            DBLine(0, "Norbit_proc_ste_to_udelali", "Proč jste to udělali?", norbit, noone),
            DBLine(0, "Raspusa_sem_raspusa", "Jsem Raspuša", spusa, noone),
            DBLine(
                0,
                "Raspusa_mas_uz_nejakou_holku",
                "Už máš nějakou holku, Nezbite",
                spusa,
                noone
            ),
            DBLine(0, "BBJ&Norbit_prdel", "Krocaní prdel", extras, norbit),
            DBLine(0, "Wong_fuuuuj", "Fuuuuuj", wong, noone),
            DBLine(0, "Raspusa_otevri_ji", "Otevři jí!", spusa, noone),
            DBLine(0, "Pasaci_mliko_zadarmo", "Mlíko zdarma", extras, noone),
            DBLine(0, "Raspusa_nekrakej", "Nekrákej holka", spusa, noone),
            DBLine(0, "Raspusa&Wong_horor", "Horror", spusa, wong),
            DBLine(0, "BBJ&Norbit_priserne_bolet", "Bude to příšerně bolet, kámo", extras, norbit),
            DBLine(0, "Ital_dort", "Někdo mi ukousnout obrovskej kus dortu!", extras, noone),
            DBLine(0, "Raspusa_ja_nic_nejedla", "Co čumíte, já nic nejedla", spusa, noone),
            DBLine(0, "Wong_to_vtip", "To vtip, to vtip, to vtip", wong, noone),
            DBLine(0, "Wong_do_prdele_ne", "Ne, do prdele ne!", wong, noone),
            DBLine(0, "Raspusa_mala_vila", "Malá víla", spusa, noone),
            DBLine(0, "Raspusa_opri_se_do_toho", "Opři se do toho!", spusa, noone),
            DBLine(0, "Raspusa_bud_chlap", "Buď chlap!", spusa, noone),
            DBLine(0, "Raspusa_si_fakt_baba", "Jsi fakt baba", spusa, noone),
            DBLine(0, "Raspusa_hnusss", "Hnusss", spusa, noone),
            DBLine(0, "Raspusa_dobre_jitro_Raspuso", "Dobré jitro, Raspušo", spusa, extras),
            DBLine(0, "Raspusa_pridu_pozde", "Přijdu pozdě na hodiny tance", spusa, noone),
            DBLine(
                0,
                "Raspusa_proc_hejbes s moji sedackou",
                "Proč hejbeš s mojí sedačkou?",
                spusa,
                noone
            ),
            DBLine(0, "Raspusa_prsa_zmacknou_klakson", "Psa zmáčknou klakson", spusa, noone),
            DBLine(0, "Wong_ja_nebyt_jako_ti_z_mesta", "Já nebýt jako ti z města", wong, noone),
            DBLine(0, "Wong_vyyyyy", "Vyyyy", wong, noone),
            DBLine(0, "Wong_ling_ling_moje_pistole", "Ling Ling! Moje pistole!", wong, noone),
            DBLine(0, "Buster_lekce_tance", "Lekce tance", extras, noone),
            DBLine(0, "Raspusa_mmmm_to_je_dobrej_napad", "Mmmm, to je dobrej nápad", spusa, noone),
            DBLine(
                0,
                "Pasaci_jednou_pasak_vzdycky_pasak",
                "Jednou pasák vždycky pasák",
                extras,
                noone
            ),
            DBLine(0, "Pasaci_bidety_baby_a_abordel", "Bidety, baby a bordel", extras, noone),
            DBLine(0, "Norbit_Ste_prima_kluci", "Jste prima, kluci", norbit, noone),
            DBLine(0, "Buster_slovo_tanec", "Tanec", extras, noone),
            DBLine(0, "Raspusha_buster_je_nas_host", "Buster je náš host", spusa, noone),
            DBLine(0, "Buster_druha_tvar", "Druhá tvář", extras, noone),
            DBLine(0, "Raspusa_spokojenej", "Spokojenej?", spusa, noone),
            DBLine(0, "Raspusha_nic_se_nestalo", "NIC. SE. NESTALO.", spusa, noone),
            DBLine(0, "Raspusa_Nic_se_tu_nestalo!", "Nic se tu nesalo!", spusa, noone),
            DBLine(0, "Norbit_ale_jo,_stalo", "Ale jo, stalo!", norbit, noone),
            DBLine(0, "Raspusa_AAAAaaaa!", "AAAAaaaa!", spusa, noone),
            DBLine(0, "Raspusa_pockej_na_me", "Počkej na mě!", norbit, noone),
            DBLine(0, "Raspusa_podelanej_srab", "Podělanej srab", spusa, noone),
            DBLine(0, "Norbit_ty_behno", "Ty běhno!", norbit, noone),
            DBLine(0, "Raspusa_pocem_tyyyyy", "Pocem tyyyy", spusa, noone),
            DBLine(0, "Raspusha_doufam_ze_ses_zabil", "Doufám, že ses zabil!", spusa, noone),
            DBLine(
                0,
                "Raspusa_takhle_to_s_tebou_dopadne_vzdycky",
                "Takhle to s tebou dopadne vždycky",
                spusa,
                noone
            ),
            DBLine(0, "Raspusha_zebirka", "Žebírka", spusa, noone),
            DBLine(0, "Raspusa_fujtajbl", "Fujtajbl", spusa, noone)
        )

        lineList.forEach {
            mLineViewModel.insert(it)
        }
    }

    private fun filterCharacter(character: String): Boolean {
        Log.d("FILTER", "$character")
        var shownLines: List<DBLine>
        if (character == "All") {
            shownLines = database.myDao().getAllLines()
        } else {
            shownLines = database.myDao().getCharacterLines(character)
        }

        pupulateGV(shownLines)

        return false
    }

    private fun sortDatabase(by: String): Boolean {
        Log.d("SORT", "$by")
        var shownLines: List<DBLine>
        if (by == "Název") {
            shownLines = database.myDao().getAllLinesOrderedByLine()
        } else {
            shownLines = database.myDao().getAllLines()
        }

        pupulateGV(shownLines)

        return false
    }

    private fun pupulateGV(shownLines: List<DBLine>) {
        lineGRV = findViewById(R.id.GRV)
        lineList = ArrayList<GridViewModal>()

        shownLines.forEach {
            lineList = lineList + GridViewModal(it.line, it.file)
        }

        lineAdapter = GridRVAdapter(lineList = lineList, this@MainActivity)

        lineGRV.adapter = lineAdapter
        lineGRV.setOnItemClickListener { _, _, position, _ -> play(lineList[position].file) }
    }

//        override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if(drawerToggle.onOptionsItemSelected(item)){
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            Log.d("RIGHT", "onOptionsItemSelected")
            if (dwLayout.isDrawerOpen(GravityCompat.END)) {
                Log.d("RIGHT", "if")
                dwLayout.closeDrawer(GravityCompat.END)
            } else {
                Log.d("RIGHT", "else")
                dwLayout.openDrawer(GravityCompat.END)
            }
            return true
        }
        return false
    }

    override fun onBackPressed() {
        Log.d("RIGHT", "onBackPressed")
        if (dwLayout.isDrawerOpen(GravityCompat.END)) {
            Log.d("RIGHT", "if")
            dwLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}