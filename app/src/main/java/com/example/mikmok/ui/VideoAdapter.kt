package com.example.mikmok.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mikmok.R
import com.example.mikmok.data.models.Feed
import com.example.mikmok.data.models.VideoModel
import com.example.mikmok.databinding.SingleVideoBinding


class VideoAdapter (
    private val videoList: List<VideoModel>,
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.binding.apply {
            val url = Uri.parse(videoList[position].url)
            videoView.setVideoURI(url)
            videoView.start()
        }
    }

    override fun getItemCount() = videoList.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SingleVideoBinding.bind(itemView)
    }

}