package com.example.kursach11

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.video_view)

        // Получаем Uri видео файла из Intent
        val videoUri: Uri = intent.getParcelableExtra("videoUri")!!

        // Настроим контроллер воспроизведения
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Устанавливаем источник видео
        videoView.setVideoURI(videoUri)

        // Запускаем видео
        videoView.start()
    }
}
