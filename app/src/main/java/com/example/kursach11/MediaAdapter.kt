package com.example.kursach11

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File

class MediaAdapter(private val context: Context, private val mediaFiles: List<File>) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(file: File)
        fun onItemLongClick(file: File)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaFile = mediaFiles[position]
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", mediaFile)

        // Для изображений используем Glide
        if (mediaFile.extension in listOf("jpg", "jpeg", "png")) {
            Glide.with(context)
                .load(uri)
                .apply(RequestOptions().centerCrop())
                .into(holder.mediaPreview)
        } else if (mediaFile.extension in listOf("mp4", "mkv", "avi")) {
            // Для видео отображаем иконку или миниатюру
            holder.mediaPreview.setImageResource(android.R.drawable.ic_media_play)
        }

        // Устанавливаем тип медиафайла
        holder.mediaType.text = if (mediaFile.extension in listOf("jpg", "jpeg", "png")) "Фото" else "Видео"
        // Устанавливаем дату последнего изменения файла
        holder.mediaDate.text = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", mediaFile.lastModified())

        // Обработка клика по элементу
        holder.itemView.setOnClickListener {
            when {
                mediaFile.extension in listOf("jpg", "jpeg", "png") -> {
                    // Если это изображение, открываем его в ImageView
                    val intent = Intent(context, ImageViewerActivity::class.java).apply {
                        putExtra("imageUri", uri)  // Передаем URI изображения
                    }
                    context.startActivity(intent)
                }
                mediaFile.extension in listOf("mp4", "mkv", "avi") -> {
                    // Если это видео, открываем его в VideoPlayerActivity
                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                        putExtra("videoUri", uri)  // Передаем URI видео
                    }
                    context.startActivity(intent)
                }
                else -> onItemClickListener?.onItemClick(mediaFile)  // Для других файлов вызываем стандартное действие
            }
        }

        // Обработка долгого нажатия на элемент
        holder.itemView.setOnLongClickListener {
            onItemClickListener?.onItemLongClick(mediaFile)
            true
        }
    }

    override fun getItemCount(): Int = mediaFiles.size

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mediaPreview: ImageView = itemView.findViewById(R.id.media_preview)
        val mediaType: TextView = itemView.findViewById(R.id.media_type)
        val mediaDate: TextView = itemView.findViewById(R.id.media_date)
    }
}
