package com.shopvite.audioplayerkotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shopvite.audioplayerkotlin.adapter.AudioRecyclerAdapter
import com.shopvite.audioplayerkotlin.model.AudioModel
import com.shopvite.audioplayerkotlin.viewdmodel.AudioViewModel

class MainActivity : AppCompatActivity() {
    private var viewManager = LinearLayoutManager(this)
    private lateinit var viewModel: AudioViewModel
    private lateinit var mainrecycler: RecyclerView


    lateinit var dexter : DexterBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()


    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> dexter.check()
    }
    private fun getPermission() {
        dexter = Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {

                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(this@MainActivity, "Permissions Granted", Toast.LENGTH_SHORT).show()
                            init()
                        } else {
                            AlertDialog.Builder(this@MainActivity, com.karumi.dexter.R.style.Theme_AppCompat_Dialog).apply {
                                setMessage("please allow the required permissions")
                                    .setCancelable(false)
                                    .setPositiveButton("Settings") { _, _ ->
                                        val reqIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .apply {
                                                val uri = Uri.fromParts("package", packageName, null)
                                                data = uri
                                            }
                                        resultLauncher.launch(reqIntent)
                                    }
                                // setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                val alert = this.create()
                                alert.show()
                            }
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).withErrorListener{
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
        dexter.check()
    }

    private fun init(){
        mainrecycler = findViewById(R.id.songView)
        val application = requireNotNull(this).application
        val factory = AudioViewModelFactory()
        viewModel = ViewModelProvider(this).get(AudioViewModel::class.java)
        initialiseAdapter()
        addData()

    }
    private fun initialiseAdapter(){
        mainrecycler.layoutManager = viewManager
        var position= (mainrecycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        observeData()
    }

    fun observeData(){
        viewModel.lst.observe(this, Observer{
            Log.i("data",it.toString())
            mainrecycler.adapter= AudioRecyclerAdapter(viewModel, it, this)
        })
    }


    fun addData(){
        val projection = arrayOf(MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.RELATIVE_PATH,
            MediaStore.Audio.Media.DATA)
//        val selection = sql-where-clause-with-placeholder-variables
//        val selectionArgs = values-of-placeholder-variables
//        val sortOrder = sql-order-by-clause

        applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val audioIndex: Int =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val fullPath: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                Log.d("AudioFile", "" + fullPath)
                viewModel.add(AudioModel(cursor.getString(audioIndex),fullPath))
                // Use an ID column from the projection to get
                // a URI representing the media item itself.
            }
        }
        mainrecycler.adapter?.notifyDataSetChanged()

    }
}