package com.example.scribe

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.scribe.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import kotlinx.coroutines.*
import androidx.lifecycle.*

// TODO: search - go through each phrase in the csv, count and show
// TODO: addendum - add notes to 3rd column
// TODO: translation mode - allow you to translate to third column async

// TODO: figure out all cases for having no files buttons that can lead to nullpointexceptions

class MainActivity : AppCompatActivity() {

    // to hold the csv output
    private val sharedViewModel by viewModels<SharedViewModel>()

    private lateinit var speechRecognitionManager: SpeechRecognitionManager
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var coroutineScope: CoroutineScope

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // name stuff
        val transcriptionFolder = findTargetDirectory("Transcriptions")
        transcriptionFolder.mkdir()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                101) // RECORD_AUDIO_PERMISSION_CODE
        }

        coroutineScope = CoroutineScope(Dispatchers.Default)

        // instantiate SRM
        speechRecognitionManager = SpeechRecognitionManager(
            this,
            sharedViewModel
        )

        // add livedata to CSV list
        sharedViewModel.gCSVSet(getAllFilesInFolder(transcriptionFolder.toPath()))

        // check folder contents and set currentCSV
        emptyCheck()

        // livedata setup
        sharedViewModel.gDataSet(readCSV(sharedViewModel.currentCSV))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        loadNavDrawer(binding.navView)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // TODO coroutine setup
        // TODO make cancel button work
        binding.appBarMain.fab.setOnClickListener { // view ->
            // off switch
            if (speechRecognitionManager.isListening) {
                setFabAttributes(this, R.drawable.ic_mic, R.color.teal_200)
                sharedViewModel.stopRequested = true
            }
            // on switch
            else {
                setFabAttributes(this, R.drawable.ic_cancel, R.color.red)
                sharedViewModel.stopRequested = false
                if (sharedViewModel.currentCSV == null) {
                    // make new CSV if none present
                    newCSVFlow(transcriptionFolder)
                }
            }
//            startSpeechToText(applicationContext, sharedViewModel)
            speechRecognitionManager.startSpeechRecognition()
        }

        // Find the ImageView within the header view
        val headerView = navView.getHeaderView(0)
        val imageView: ImageView = headerView.findViewById(R.id.idIVNewCSV)

        // button for new CSV
        imageView.setOnClickListener {
            newCSVFlow(transcriptionFolder)
        }

        sharedViewModel.gCSV.observe(this) { list ->
            loadNavDrawer(navView)
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home //  R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // set nav drawer buttons to do stuff
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // set open file to the CSV variants
                in 0 until 10000 -> { // arbitrary range
                    sharedViewModel.currentCSV = File(
                        transcriptionFolder,
                        sharedViewModel.gCSVIndex(menuItem.itemId) + ".csv"
                    )
                    sharedViewModel.gDataSet(readCSV(sharedViewModel.currentCSV))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    customToast(
                        this,
                        "${fileToString(sharedViewModel.currentCSV)} opened"
                    )
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun updateToolbarMenu(list: List<String>) {
        // Clear existing menu items
        binding.appBarMain.toolbar.menu.clear()

        // Add items from the LiveData list to the toolbar menu
        list.forEachIndexed { index, item ->
            binding.appBarMain.toolbar.menu.add(Menu.NONE, index, Menu.NONE, item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rename -> {
                if (sharedViewModel.currentCSV != null) {
                    // get folder
                    val transcriptionFolder = findTargetDirectory("Transcriptions")
                    renameCSV(this, transcriptionFolder, sharedViewModel)
                }
                true
            }
            R.id.action_delete -> {
                if (sharedViewModel.currentCSV != null) {
                    // get folder
                    val transcriptionFolder = findTargetDirectory("Transcriptions")
                    deleteCSV(sharedViewModel.currentCSV)
                    // update drawer
                    sharedViewModel.gCSVSet(getAllFilesInFolder(transcriptionFolder.toPath()))
                    customToast(
                        this,
                        "${fileToString(sharedViewModel.currentCSV)} deleted"
                    )
                    emptyCheck()
                    // refresh recyclerview
                    sharedViewModel.gDataSet(readCSV(sharedViewModel.currentCSV))
                }
                true
            }
            // Handle other menu item clicks if needed
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                // You can proceed with using the audio feature
            } else {
                // Permission is denied
                // Handle the case where the user denied the permission
                binding.appBarMain.fab.setOnClickListener { view ->
                    Snackbar.make(view, "App requires microphone permission", Snackbar.LENGTH_LONG)
                        .setAction("OK", null).show()
                }
            }
        }
    }

    private fun loadNavDrawer(navView: NavigationView) {
        navView.menu.clear()
        for ((index, itemTitle) in sharedViewModel.gCSV.value?.withIndex()!!) {
            val menuItem = navView.menu.add(Menu.NONE, index, Menu.NONE, itemTitle)
        }
    }

    fun gSharedViewModel(): SharedViewModel {
        return sharedViewModel
    }

    private fun findTargetDirectory(targetFolder: String): File {
        val context = applicationContext
        val path = findDirectory(context)
        return File(path, targetFolder)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun emptyCheck() {
        // find folder
        val transcriptionFolder = findTargetDirectory("Transcriptions")
        if (!sharedViewModel.gCSV.value.isNullOrEmpty()) {
            current2Latest(transcriptionFolder)
        } else {
            // if no suitable targets
            sharedViewModel.currentCSV = null
            customToast(this,"Target folder is empty")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun newCSVFlow(transcriptionFolder: File) {
        newCSV(transcriptionFolder, sharedViewModel)
        // close drawer
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        // update drawer
        sharedViewModel.gCSVSet(getAllFilesInFolder(transcriptionFolder.toPath()))
        // refresh recyclerview
        sharedViewModel.gDataSet(readCSV(sharedViewModel.currentCSV))
        customToast(this, "new CSV generated")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun current2Latest(transcriptionFolder: File) {
        // refresh list
        sharedViewModel.gCSVSet(getAllFilesInFolder(transcriptionFolder.toPath()))
        // set currentCSV to latest file, if there is one
        sharedViewModel.currentCSV = stringToFile(
            sharedViewModel.gCSVIndex(0),
            transcriptionFolder
        )
        //customToast(applicationContext, "${fileToString(sharedViewModel.currentCSV)} current")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop speech recognition when your activity is destroyed (if needed)
        speechRecognitionManager.stopSpeechRecognition()
    }
}