package com.example.kursach11

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.VideoView

class VideoListAdapter(context: Context, private val videoFiles: List<Uri>) :
    ArrayAdapter<Uri>(context, 0, videoFiles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false)
        val videoView: VideoView = view.findViewById(R.id.video_view)
        val textView: TextView = view.findViewById(R.id.video_name)

        val videoUri = getItem(position)
        videoView.setVideoURI(videoUri)
        textView.text = videoUri?.lastPathSegment

        return view
    }
}