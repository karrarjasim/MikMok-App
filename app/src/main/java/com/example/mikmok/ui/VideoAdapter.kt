package com.example.mikmok.ui

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mikmok.R
import com.example.mikmok.data.models.VideoModel
import com.example.mikmok.databinding.SingleVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayer.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player


class VideoAdapter (
    private val videoList: List<VideoModel>,
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>(){
    private lateinit var player:Player


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val currentVideo=videoList[position]

        holder.binding.apply {
           setupPlayer(currentVideo.url)
            bindVideoView(currentVideo)
        }
    }

    private fun SingleVideoBinding.bindVideoView(currentVideo: VideoModel) {
        title.text = currentVideo.title
        labelDescription.text = currentVideo.description
        labelDirector.text = currentVideo.director
        labelYear.text = currentVideo.year.toString()
        Glide.with(root).load(currentVideo.art).into(image)
    }

    private fun SingleVideoBinding.setupPlayer(url: String) {
        val player = Builder(root.context).build()
        videoView.player = player
        player.addListener(object :Player.Listener{
            // handle loading
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        progressBar.visibility = View.VISIBLE

                    }
                    Player.STATE_READY -> {
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            }
        })
        player.addMediaItems(listOf(MediaItem.fromUri(Uri.parse(url))))
        player.prepare()
        player.play()
    }










    override fun getItemCount() = videoList.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SingleVideoBinding.bind(itemView)
    }

}