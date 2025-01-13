package com.example.kursach11

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File

class ImageViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        val imageView: ImageView = findViewById(R.id.image_view)

        val imageUri = intent.getParcelableExtra<Uri>("imageUri")  // Получаем URI изображения

        // Загружаем изображение в ImageView с помощью Glide
        imageUri?.let {
            Glide.with(this).load(it).into(imageView)
        }
    }
}
