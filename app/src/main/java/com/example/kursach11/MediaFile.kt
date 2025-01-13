package com.example.kursach11

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_files")
data class MediaFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val mediaType: MediaType,
    val dateCreated: Long
)

enum class MediaType {
    PHOTO, VIDEO
}
