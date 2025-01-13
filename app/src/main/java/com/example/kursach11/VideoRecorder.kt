package com.example.kursach11
/*
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService

class VideoRecorder(
    private val context: Context,
    private val cameraPreview: PreviewView,
    private val videoButton: Button,
    private val chronometer: Chronometer,
    private val cameraExecutor: ExecutorService
) {

    private var _isRecording = false
    val isRecording: Boolean
        get() = _isRecording

    private var videoFile: File? = null
    private var mediaRecorder: MediaRecorder? = null
    private var videoUri: Uri? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    fun startRecording() {
        // Создаем ContentValues для добавления видео в MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "video_${System.currentTimeMillis()}")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)  // Для Android 10+
        }

        // Вставляем новый файл в MediaStore и получаем его URI
        videoUri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        // Настроим MediaRecorder с этим URI
        mediaRecorder = MediaRecorder().apply {
            // Вначале настраиваем источник аудио
            setAudioSource(MediaRecorder.AudioSource.MIC) // Устанавливаем источник аудио

            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            // Путь к файлу, где будет сохранено видео
            setOutputFile(context.contentResolver.openFileDescriptor(videoUri!!, "w")?.fileDescriptor)

            prepare()
            start()
            _isRecording = true
        }
    }



    fun stopRecording() {
        mediaRecorder?.apply {
            try {
                if (_isRecording) {
                    // Останавливаем запись
                    stop()
                    release()
                    _isRecording = false

                    // Обновляем медиабазу, чтобы файл был доступен в галерее
                    videoUri?.let {
                        context.contentResolver.update(it, ContentValues().apply {
                            put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                        }, null, null)
                    }
                }
            } catch (e: RuntimeException) {
                Log.e("VideoRecorder", "stop failed: ${e.message}")
                // Если ошибка при остановке, удаляем файл
                videoUri?.let { context.contentResolver.delete(it, null, null) }
            }
        }
    }


    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }
            val imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(context as MainActivity, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("VideoRecorder", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

 */