package com.example.kursach11

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var galleryRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallerery_activity)

        galleryRecyclerView = findViewById(R.id.gallery_recycler_view)

        // Загружаем изображения и видео в галерею
        val mediaFiles = loadMediaFiles()
        val adapter = MediaAdapter(this, mediaFiles)
        galleryRecyclerView.adapter = adapter
        galleryRecyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter.setOnItemClickListener(object : MediaAdapter.OnItemClickListener {
            override fun onItemClick(file: File) {
                openFile(file)
            }

            override fun onItemLongClick(file: File) {
                deleteFile(file)
            }
        })
    }

    private fun loadMediaFiles(): List<File> {
        val mediaFiles = mutableListOf<File>()

        // Загружаем изображения из директории приложения
        val mediaDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        mediaDir?.listFiles()?.let { mediaFiles.addAll(it) }

        // Загружаем видео через MediaStore
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME
        )

        val selection = "${MediaStore.Video.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%/Movies/CameraApp/%") // Указываем путь для поиска видео

        val cursor = contentResolver.query(
            videoUri, projection, selection, selectionArgs, null
        )

        cursor?.use {
            val dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(dataColumnIndex)
                val file = File(filePath)
                if (file.exists()) {
                    mediaFiles.add(file)
                }
            }
        }

        // Добавляем фильтрацию по расширению (если нужно)
        return mediaFiles.filter {
            it.extension in listOf("jpg", "jpeg", "png", "mp4", "mkv", "avi")
        }.sortedByDescending { it.lastModified() }
    }


    private fun openFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(this, VideoPlayerActivity::class.java)
        intent.putExtra("videoUri", uri)  // Передаем URI видео в новый активити
        startActivity(intent)
    }


    private fun deleteFile(file: File) {
        if (file.delete()) {
            Toast.makeText(this, "Файл удален: ${file.name}", Toast.LENGTH_SHORT).show()
            recreate()
        } else {
            Toast.makeText(this, "Не удалось удалить файл: ${file.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension
        return when (extension) {
            "jpg", "jpeg", "png" -> "image/*"
            "mp4", "mkv", "avi" -> "video/*"
            else -> "*/*"
        }
    }
}