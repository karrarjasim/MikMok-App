package com.example.mikmok.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mikmok.R
import com.example.mikmok.data.models.ExoPlayerItem
import com.example.mikmok.data.models.VideoModel
import com.example.mikmok.databinding.SingleVideoBinding
import com.example.mikmok.util.OnVideoClickListener
import com.example.mikmok.util.OnVideoPreparedListener
import com.google.android.exoplayer2.ExoPlayer.Builder
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player


class VideoAdapter(
    private val videoList: List<VideoModel>,
    private val videoPreparedListener: OnVideoPreparedListener,
    private val onVideoClickListener : OnVideoClickListener
    ) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(val binding: SingleVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VideoViewHolder {
        return VideoViewHolder(SingleVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.binding.apply {
            setupPlayer(videoList[position].url, position)
            bindVideoView(videoList[position])
        }
    }

    private fun SingleVideoBinding.bindVideoView(currentVideo: VideoModel) {
        title.text = currentVideo.title
        labelDescription.text = currentVideo.description
        labelDirector.text = currentVideo.director
        labelYear.text = currentVideo.year.toString()
        Glide.with(root).load(currentVideo.art).into(image)
    }

    private fun SingleVideoBinding.setupPlayer(url: String, position: Int) {
        Builder(root.context).build().run {
            videoView.player = this
            addListener(object : Player.Listener {
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
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(root.context, "Could not play video due to: $error", Toast.LENGTH_SHORT).show()
                }
            })
            addMediaItem(MediaItem.fromUri(url))
            prepare()
            if (position == 0) {
                playWhenReady = true
                play()
            }
            videoPreparedListener.onVideoPrepared(ExoPlayerItem(this, position))
            videoView.videoSurfaceView?.setOnClickListener {
                onVideoClickListener.onClick(this)
            }
        }
    }

    override fun getItemCount() = videoList.size
}