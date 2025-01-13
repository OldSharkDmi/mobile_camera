//package com.example.kursach11
//
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.media.MediaScannerConnection
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.Toast
//import android.widget.VideoView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import com.bumptech.glide.Glide
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//
//class MediaViewerActivity : AppCompatActivity() {
//
//    private lateinit var mediaVideoView: VideoView
//    private lateinit var deleteButton: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_media_viewer)
//
//        mediaVideoView = findViewById(R.id.media_video_view)
//        deleteButton = findViewById(R.id.delete_button)
//
//        val fileUriString = intent.getStringExtra(FILE_URI)
//        val fileUri = Uri.parse(fileUriString)
//        val mimeType = intent.getStringExtra(MIME_TYPE)
//
//        if (mimeType?.startsWith("video") == true && fileUri != null) {
//            // Сохраняем видео в директоорию приложения
//            val savedVideoUri = saveVideoToAppDirectory(fileUri)
//            playVideo(savedVideoUri)
//        }
//
//        deleteButton.setOnClickListener {
//            val file = File(fileUri.path!!)
//            if (file.delete()) {
//                Toast.makeText(this, "Файл удален", Toast.LENGTH_SHORT).show()
//                finish()
//            } else {
//                Toast.makeText(this, "Не удалось удалить файл", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun saveVideoToAppDirectory(uri: Uri): Uri {
//        val savedUri = saveVideoToInternalDirectory(uri)
//        return savedUri
//    }
//
//    private fun saveVideoToInternalDirectory(uri: Uri): Uri {
//        val resolver = contentResolver
//        val inputStream = resolver.openInputStream(uri)
//        val outputDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
//        val savedFile = File(outputDir, "video_${System.currentTimeMillis()}.mp4")
//        val outputStream = FileOutputStream(savedFile)
//
//        inputStream?.use { input ->
//            outputStream.use { output ->
//                val buffer = ByteArray(1024)
//                var length: Int
//                while (input.read(buffer).also { length = it } > 0) {
//                    output.write(buffer, 0, length)
//                }
//            }
//        }
//
//        return Uri.fromFile(savedFile) // Возвращаем URI для внутреннего хранилища
//    }
//
//    private fun playVideo(uri: Uri) {
//        mediaVideoView.setVideoURI(uri)
//        mediaVideoView.start()
//    }
//
//    companion object {
//        const val FILE_URI = "file_uri"
//        const val MIME_TYPE = "mime_type"
//    }
//}