package com.example.raspusapp

import com.example.raspusapp.data.MyDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.raspusapp.data.DBLine
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.raspusapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Collections
import android.util.Log

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

    private lateinit var lineAdapter: GridRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("")

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

            toolbar.setNavigationIcon(R.drawable.baseline_filter_alt_24)

            navigationView = findViewById(R.id.nav_view)
            navigationView.setNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.spusa -> {
                        filterCharacter(getString(R.string.spusa))
                    }
                    R.id.norbit -> {
                        filterCharacter(getString(R.string.norbit))
                    }
                    R.id.wong -> {
                        filterCharacter(getString(R.string.wong))
                    }
                    R.id.extras -> {
                        filterCharacter(getString(R.string.extras))
                    }
                    R.id.kate -> {
                        filterCharacter(getString(R.string.kate))
                    }
                    R.id.time -> {
                        sortDatabase(getString(R.string.time))
                    }
                    R.id.title -> {
                        sortDatabase(getString(R.string.title))
                    }
                    else -> {
                        filterCharacter(getString(R.string.all))
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

        sortDatabase(getString(R.string.time))
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

        val wong = getString(R.string.wong)
        val spusa = getString(R.string.spusa)
        val extras = getString(R.string.extras)
        val norbit = getString(R.string.norbit)
        val kate = getString(R.string.kate)
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
            DBLine(0, "Raspusa_mas_uz_nejakou_holku","Už máš nějakou holku, Nezbite", spusa, noone),
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
            DBLine(0, "Raspusa_proc_hejbes s moji sedackou", "Proč hejbeš s mojí sedačkou?", spusa, noone),
            DBLine(0, "Raspusa_prsa_zmacknou_klakson", "Psa zmáčknou klakson", spusa, noone),
            DBLine(0, "Wong_ja_nebyt_jako_ti_z_mesta", "Já nebýt jako ti z města", wong, noone),
            DBLine(0, "Wong_vyyyyy", "Vyyyy", wong, noone),
            DBLine(0, "Wong_ling_ling_moje_pistole", "Ling Ling! Moje pistole!", wong, noone),
            DBLine(0, "Buster_lekce_tance", "Lekce tance", extras, noone),
            DBLine(0, "Raspusa_mmmm_to_je_dobrej_napad", "Mmmm, to je dobrej nápad", spusa, noone),
            DBLine(0, "Pasaci_jednou_pasak_vzdycky_pasak", "Jednou pasák vždycky pasák", extras, noone),
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
            DBLine(0, "Raspusa_takhle_to_s_tebou_dopadne_vzdycky", "Takhle to s tebou dopadne vždycky", spusa, noone),
            DBLine(0, "Raspusha_zebirka", "Žebírka", spusa, noone),
            DBLine(0, "Raspusa_fujtajbl", "Fujtajbl", spusa, noone),
            DBLine(0, "Norbit_pohadka", "Pohádka", norbit, noone),
            DBLine(0, "Norbit_pohadka_2", "Pohádka 2", norbit, noone),
            DBLine(0, "Wong_to_byt_cinsky_porno", "Čínský porno", wong, noone),
            DBLine(0, "Kate_ale_notaaak", "Ale notaaak", kate, noone),
            DBLine(0, "Norbit_utery", "Úterý", norbit, noone),
            DBLine(0, "Raspusa_jeste_jednou_reknes_utery", "Ještě jednou řekneš úterý...", spusa, noone),
            DBLine(0, "Raspusa_tanecni_horecka", "Taneční horečka", spusa, noone),
            DBLine(0, "Norbit_Mojzisiii!", "Mojžíši", norbit, noone),
            DBLine(0, "Raspusa_zejtra_na_pikniku_chcipaci", "Zejtra na pikniku chcípáci", spusa, noone),
            DBLine(0, "Norbit_,Raspusa_pocasi", "Počasí", norbit, noone),
            DBLine(0, "Raspusa_hnusny_pocasi", "Hnusný počasí", spusa, noone),
            DBLine(0, "Raspusa_posledni_dobou_furt_prsi", "Poslední dobou furt prší", spusa, noone),
            DBLine(0, "Raspusa_nehraj_si_s_moji_sedackou!", "Nehraj si s mojí sedačkou", spusa, noone),
            DBLine(0, "Norbit_nikdo_na_tvoji_sedacku_nesahl", "Nikdo na tvojí sedačnu nesáhl", norbit, noone),
            DBLine(0, "Raspusa_doobre_tak_proc", "Tak proč je najednou takhle vepředu", spusa, noone),
            DBLine(0, "Raspusa_to_mas_mozna_pravdu", "To máš možná pravdu", spusa, noone),
            DBLine(0, "Raspusa_desny_pocasi", "Děsný počasí", spusa, noone),
            DBLine(0, "Raspusa_podivej_na_toho_cokla_jak_cumi", "Podívej na toho čokla jak čumí", spusa, noone),
            DBLine(0, "Raspusa_uz_te_mam_uz_te_mam", "Už tě mám, už tě mám", spusa, noone),
            DBLine(0, "Raspusa_zmlkniii", "Zmlkniii", spusa, noone),
            DBLine(0, "Raspusa_co_ten_zvuk_znamena", "Co ten zvuk znamená", spusa, noone),
            DBLine(0, "Raspusa_kam_si_ty_pitomce_myslis_ze_jako_des", "Kam si ty pitomče myslíš, že jako jdeš", spusa, noone),
            DBLine(0, "Raspusa,_Norbit_ja_te_opoustim", "Já tě opouštím", norbit, noone),
            DBLine(0, "Raspusa_norbite_cekam_dite", "Čekám dítě", spusa, noone),
            DBLine(0, "Raspusa_nejses_nic", "Nejseš nic", spusa, noone),
            DBLine(0, "Raspusa_uz_zacinam_mit_brisko", "Už začínám mít bříško", spusa, noone),
            DBLine(0, "Raspusa_prsa_me_svedej_a_tlacej", "Prsa mě svěděj a tlačej", spusa, noone),
            DBLine(0, "Raspusa_Aaaah_Norbiteeeeee...", "Norbiteeee...", spusa, noone),
            DBLine(0, "Norbit,_Kate_ta_v_tech_barevnech_satech", "Ta v těch barevnejch šatech", norbit, kate),
            DBLine(0, "Raspusa_tresne_nebo_jahody", "Třešně nebo jahody?", spusa, noone),
            DBLine(0, "Raspusa_kdo_sakra_ste", "Kdo sakra ste?", spusa, noone),
            DBLine(0, "Raspusa_hmm_ahoj", "Hmm, ahoj", spusa, noone),
            DBLine(0, "Raspusa_joo_spravne_to_je_pravda", "Joo, spravně, to je pravda", spusa, noone),
            DBLine(0, "Raspusa_tak_sup_sup", "Tak šup, šup", spusa, noone),
            DBLine(0, "Raspusha_doobree", "Doobřee", spusa, noone),
            DBLine(0, "Raspusa_pocem!", "Pocem!", spusa, noone),
            DBLine(0, "Raspusa_dones_mi_dalsi_vinnej_strik", "Dones mi další vinnej střik", spusa, noone),
            DBLine(0, "Raspusa_a_proc_sakra_ne", "A proč sakra ne?", spusa, noone),
            DBLine(0, "Raspusa&Norbit_bratricek", "Bratříček", spusa, norbit),
            DBLine(0, "Raspusa_stat_vratte_to", "Stát, vraťte to!", spusa, noone),
            DBLine(0, "Raspusa_vrat_mi_to_okamzite", "Vrať mi to okamžitě", spusa, noone),
            DBLine(0, "Raspusa_myslite_ze_tam_nevlezu", "Myslíte, že tam nevlezu_", spusa, noone),
            DBLine(0, "Raspusa_nemysli_si_ze_nezabiju_dite", "Nemysli si, že nazabiju dítě", spusa, noone),
            DBLine(0, "Raspusa_a_ted_to_bude_bolet", "A teď to bude bolet", spusa, noone),
            DBLine(0, "Raspusa_mali_parchanti", "Malí parchanti", spusa, noone),
            DBLine(0, "Pasaci_moje_modlitby_byly_vyslyseny", "Moje modlitby byly vyslyšeny", extras, noone),
            DBLine(0, "Raspusa_mam_fakt_zizen", "Mám fakt žízeň", spusa, noone),
            DBLine(0, "Raspusa_tak_tohle_ne", "Tak tohle ne", spusa, noone),
            DBLine(0, "Raspusa_on_se_snad_zblaznil", "On se snad zbláznil", spusa, noone),
            DBLine(0, "Norbit_ja_jsem_tak_stastnej", "Já jsem tak šťastnej", norbit, noone),
            DBLine(0, "Raspusa_pekne_si_ho_navnadila", "Pěkně si ho navnadila", spusa, noone),
            DBLine(0, "Raspusa_tak_ty_ses_stastnej", "Tak ty seš šťastnej?", spusa, noone),
            DBLine(0, "Blue_my_Latimorove", "My, Latimorové", extras, noone),
            DBLine(0, "Kate_jedeem", "Jedeem", kate, noone),
            DBLine(0, "Kate&Norbit_no_tak_ano", "No, tak ano", kate, norbit),
            DBLine(0, "Rapusa_kam_to_zase_jdes", "Kam to zase jdedš?", spusa, noone),
            DBLine(0, "Raspusa_NORBITEEE!", "NORBITEEE!", spusa, noone),
            DBLine(0, "Raspusa_slecna_bang_bang", "Slečna bang bang", spusa, noone),
            DBLine(0, "Raspusa_Kate_ne", "Kate, ne?", spusa, noone),
            DBLine(0, "Norbit_Kadabushi", "Kadabuši", norbit, noone),
            DBLine(0, "Norbit_Neeeee", "Neeeee", norbit, noone),
            DBLine(0, "Raspusa_ja_tam_du_taky", "Já tam jdu taky", spusa, noone),
            DBLine(0, "Raspusa_pout", "Pouť", spusa, noone),
            DBLine(0, "Norbit_bEzVa", "bEzVa", norbit, noone),
            DBLine(0, "Raspusa_to_tady_budeme_jen_tak_stat", "To tady budeme jen tak stát", spusa, noone),
            DBLine(0, "Raspusa_co_je", "Co je_", spusa, noone),
            DBLine(0, "Extras_mate_i_spodni_dil", "Máte i spodní díl", extras, noone),
            DBLine(0, "Raspusa_no_jasne_ze_ho_mam", "No jasně, že ho mám", spusa, noone),
            DBLine(0, "Raspusa_dik", "Dík", spusa, noone),
            DBLine(0, "Raspusa_Co_to_sakra_je", "Co to sakra je?", spusa, noone),
            DBLine(0, "Raspusa_co_to_sakra_je_extended", "Co to sakra je? extended", spusa, noone),
            DBLine(0, "Raspusa_dete_tam", "Dete tam?", spusa, noone),
            DBLine(0, "Raspusa_tady_nemaj_nic!", "Tady nemají nic!", spusa, noone),
            DBLine(0, "Kate_ne_uz_jsem_jedla", "Ne, už jsem jedla", kate, noone),
            DBLine(0, "Raspusa_no_toho_bych_si_nevsimla", "No, toho bych si nevšimla", spusa, noone),
            DBLine(0, "Raspusa_oo_je_mi_vas_lito", "Oo, je mi vás líto", spusa, noone),
            DBLine(0, "Raspusa_sem_krestanka", "Jsem křesťanka", spusa, noone),
            DBLine(0, "Raspusa_je_to_ten_nejvetsi_samec", "Je to ten největší samec", spusa, noone),
            DBLine(0, "Raspusa_nezlobim_se_na_nej", "Nezlobím se na něj", spusa, noone),
            DBLine(0, "Raspusa_urcite_zacnu_s_tou_dietou", "Určitě začnu s dietou", spusa, noone),
            DBLine(0, "Raspusa_na_co_cumis", "Na co čumíš", spusa, noone),
            DBLine(0, "Kate_ahooooj!", "Ahoooj!", kate, noone),
            DBLine(0, "Raspusa_zenska_co_jede_dolu", "Žesnaká, co jede dolů", spusa, noone),
            DBLine(0, "Norbit&Kate_co_to_je", "Co to je?", norbit, kate),
            DBLine(0, "Raspusa_uz_jedu_dolu_nany", "Už jedu dolů nány!", spusa, noone),
            DBLine(0, "Raspusa_165", "165", spusa, noone),
            DBLine(0, "Raspusa_to_cumis_zlato", "To čumíš, zlato", spusa, noone),
            DBLine(0, "Narbit_omlouvam_se", "Omlouvám se", norbit, noone),
            DBLine(0, "Raspusa_testy", "Testy", spusa, noone),
            DBLine(0, "Raspusa_to_se_vi_zlato", "To se ví, zlato", spusa, noone),
            DBLine(0, "Raspusa_na_radnici", "Na radnici", spusa, noone),
            DBLine(0, "Norbit_tak_jo", "Tak jo", norbit, noone),
            DBLine(0, "Raspusa_zvratky_tu_uklizis_ty", "Zvratky tu uklízíš ty", spusa, noone),
            DBLine(0, "Raspusa_bez_a_uklid_to!!!", "Běž a ukliď to!", spusa, noone),
            DBLine(0, "Raspusa_extra", "Proč chcete dnes vypadat tak extra?", spusa, noone),
            DBLine(0, "Raspusa_kvetinka", "Květinka", spusa, noone),
            DBLine(0, "Raspusa_reputace", "Reputace", spusa, noone),
            DBLine(0, "Raspusa_travnik", "Trávník", spusa, noone),
            DBLine(0, "Raspusa_helgo_uz_jdu", "Helgo už jdu", spusa, noone),
            DBLine(0, "Raspusa_pistalka", "Norbitova píšťalka", spusa, noone),
            DBLine(0, "Extras_tak_jdeme_na_to", "Tak jdeme na to, drahá", extras, noone),
            DBLine(0, "Kate_kamaradi_byli", "Kamarádi byli", kate, noone),
            DBLine(0, "Kate_no_ofsem", "No ofšem", kate, noone),
            DBLine(0, "Kate_co", "Co?", kate, noone),
            DBLine(0, "Raspusa_hlady_scvrkla", "Hlady scvrkla", spusa, noone),
            DBLine(0, "Raspusa&Blue_bazina", "Bažina", spusa, extras),
            DBLine(0, "Blue_proc_se_na_me_tak_divas", "Proč se na mě tak díváš?", extras, noone),
            DBLine(0, "Raspusa_kde_je_Norbit", "Kde je Norbit?", spusa, noone),
            DBLine(0, "Blue_ja_fakt_nic_nevim", "Já fakt nic nevim", extras, noone),
            DBLine(0, "Raspusa_kvuli_tobe_sem_to_rozbila", "Kvůli tobě jsem to rozbila", spusa, noone)
        )

        lineList.forEach {
            mLineViewModel.insert(it)
        }
    }

    private fun filterCharacter(character: String): Boolean {
        var shownLines: List<DBLine>
        if (character == getString(R.string.all)) {
            shownLines = database.myDao().getAllLines()
        } else {
            shownLines = database.myDao().getCharacterLines(character)
        }

        pupulateGV(shownLines)

        return false
    }

    private fun sortDatabase(by: String): Boolean {
        var shownLines: List<DBLine>
        if (by == getString(R.string.title)) {
            //Collections.sort(lineAdapter.filteredData)
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

        lineAdapter = GridRVAdapter(lineList = lineList, this)

        lineGRV.adapter = lineAdapter
        lineGRV.setOnItemClickListener { _, _, position, _ -> play(lineList[position].file) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            Log.d("RIGHT", "onOptionsItemSelected")
//            if (dwLayout.isDrawerOpen(GravityCompat.END)) {
//                Log.d("RIGHT", "if")
//                dwLayout.closeDrawer(GravityCompat.END)
//            } else {
//                Log.d("RIGHT", "else")
//                dwLayout.openDrawer(GravityCompat.END)
//            }
//            return true
//        }
//        return false
//    }

//    override fun onBackPressed() {
//        Log.d("RIGHT", "onBackPressed")
//        if (dwLayout.isDrawerOpen(GravityCompat.END)) {
//            Log.d("RIGHT", "if")
//            dwLayout.closeDrawer(GravityCompat.END)
//        } else {
//            super.onBackPressed()
//        }
//    }
}