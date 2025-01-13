package com.example.kursach11

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.Chronometer
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraPreview: PreviewView
    private lateinit var switchCameraButton: ImageButton
    private lateinit var photoButton: ImageButton
    private lateinit var videoButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var chronometer: Chronometer
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isRecording = false
    private var recording: Recording? = null

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val STORAGE_PERMISSION_REQUEST_CODE = 101
    private val AUDIO_PERMISSION_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Инициализация переменных интерфейса
        cameraPreview = findViewById(R.id.camera_preview)
        switchCameraButton = findViewById(R.id.switch_camera_button)
        photoButton = findViewById(R.id.photo_button)
        videoButton = findViewById(R.id.video_button)
        galleryButton = findViewById(R.id.gallery_button)
        chronometer = findViewById(R.id.chronometer)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Проверка разрешений и запуск камеры
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            startCamera()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
        }

        // Обработчики кнопок
        switchCameraButton.setOnClickListener { switchCamera() }
        photoButton.setOnClickListener { takePhoto() }
        videoButton.setOnClickListener { toggleRecording() }
        galleryButton.setOnClickListener { openGallery() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(cameraPreview.surfaceProvider) // Связь с PreviewView
        }

        // Настройка захвата изображений
        imageCapture = ImageCapture.Builder().build()

        // Настройка захвата видео
        val recorder = Recorder.Builder().build()
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageCapture, videoCapture)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleRecording() {
        if (isRecording) {
            // Остановить запись
            recording?.stop()
            isRecording = false
            videoButton.setImageResource(R.drawable.videocam_24dp) // Обновите на ваш ресурс
            chronometer.stop()  // Остановить хронометр
            chronometer.visibility = View.GONE // Скрыть хронометр
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
        } else {
            // Настройка записи видео
            val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, name)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraApp")
                }
            }

            val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
                contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            ).setContentValues(contentValues).build()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                recording = videoCapture?.output?.prepareRecording(
                    this, mediaStoreOutputOptions
                )?.withAudioEnabled()?.start(ContextCompat.getMainExecutor(this)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            isRecording = true
                            videoButton.setImageResource(R.drawable.squre) // Обновите на ваш ресурс
                            chronometer.visibility = View.VISIBLE // Показать хронометр
                            chronometer.base = SystemClock.elapsedRealtime() // Сбросить хронометр
                            chronometer.start() // Запустить хронометр
                            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (!event.hasError()) {
                                val uri = event.outputResults.outputUri
                                Toast.makeText(this, "Video saved: $uri", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Recording error: ${event.error}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_PERMISSION_REQUEST_CODE)
            }
        }
    }


    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        bindCameraUseCases()
    }

    private fun takePhoto() {
        // Находим View для анимации вспышки
        val flashEffectView = findViewById<FrameLayout>(R.id.flashEffect)

        // Показать слой для вспышки
        flashEffectView.visibility = View.VISIBLE

        ObjectAnimator.ofFloat(flashEffectView, "alpha", 0f, 1f, 0f).apply {
            duration = 100
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Hide flash effect after animation ends
                    flashEffectView.visibility = View.GONE
                }
            })
        }



        // Далее продолжаем процесс фотографирования как обычно
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(this@MainActivity, "Фото сохранено: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@MainActivity, "Ошибка при сохранении фото: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }


    private fun openGallery() {
        val videoDir = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "CameraApp")
        val videoFiles = videoDir.listFiles { file -> file.extension == "mp4" }?.map { it.toURI().toString() }?.toTypedArray()
        val intent = Intent(this, GalleryActivity::class.java).apply {
            putExtra("VIDEO_FILES", videoFiles)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(this, "Необходимы разрешения для камеры", Toast.LENGTH_LONG).show()
                }
            }
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted for storage
                } else {
                    Toast.makeText(this, "Необходимы разрешения для хранения", Toast.LENGTH_LONG).show()
                }
            }
            AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toggleRecording()
                } else {
                    Toast.makeText(this, "Необходимы разрешения для записи аудио", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
    }
}