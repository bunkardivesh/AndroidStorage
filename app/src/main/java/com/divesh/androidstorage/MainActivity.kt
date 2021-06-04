package com.divesh.androidstorage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.divesh.androidstorage.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var photoAdapter: InternalPhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoAdapter = InternalPhotoAdapter(onLongClick = {
               val isDeleted = deletePhotoFromStorage(it.fileName)

            if (isDeleted){
                loadPhotosInRecyclerView()
                Toast.makeText(this,"Photo Deleted.",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Failed to delete photo!.",Toast.LENGTH_SHORT).show()
            }
        })

        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            val isPrivate = binding.switch1.isChecked
            if (isPrivate){
               val isSaved = savePhotoToInternalStorage(UUID.randomUUID().toString(),it)
                if (isSaved){
                    loadPhotosInRecyclerView()
                    Toast.makeText(this,"Photo Saved.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Failed to save photo!.",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.cameraButton.setOnClickListener {
            takePhoto.launch()
        }

        setUpRecyclerView()
        loadPhotosInRecyclerView()

    }

    private fun deletePhotoFromStorage(filename: String): Boolean{

        return try {
                    deleteFile(filename)
        }catch (e: IOException){
            e.printStackTrace()
            false
        }
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.apply {
            adapter = photoAdapter
            layoutManager = GridLayoutManager(this@MainActivity,2)
            //layoutManager = StaggeredGridLayoutManager(3,RecyclerView.VERTICAL)
        }
    }

    private fun loadPhotosInRecyclerView(){
        lifecycleScope.launch {
            val photos = loadPhotoFromStorage()
            photoAdapter.submitList(photos)
        }
    }

    private suspend fun loadPhotoFromStorage(): List<PhotoModel>{
        return withContext(Dispatchers.IO){
            val files = filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                PhotoModel(it.name,bmp)
            } ?: listOf()
        }
    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {

        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}