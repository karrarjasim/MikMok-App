package com.example.mikmok.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mikmok.R
import com.example.mikmok.data.models.VideoModel
import com.example.mikmok.databinding.SingleVideoBinding
import com.example.mikmok.util.OnIconsClickListener
import com.example.mikmok.util.OnVideoClickListener
import com.google.android.exoplayer2.ExoPlayer.Builder
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class VideoAdapter(
    private val videoList: List<VideoModel>,
    private val onVideoClickListener : OnVideoClickListener,
    private  val onIconsClickListener: OnIconsClickListener,
    private var isFav: Boolean = false
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
            setupPlayer(videoList[position].url)
            bindVideoView(videoList[position])

        }
    }

    private fun SingleVideoBinding.bindVideoView(currentVideo: VideoModel) {
        title.text = currentVideo.title
        labelDescription.text = currentVideo.description
        labelDirector.text = currentVideo.director
        labelYear.text = currentVideo.year.toString()
        Glide.with(root).load(currentVideo.art).into(image)
        shareIcon.setOnClickListener{onIconsClickListener.onClickShareIcon(currentVideo.url)}
        setFavIcon()

    }
private fun SingleVideoBinding.setFavIcon(){
    heartIcon.setOnClickListener {fav ->
        if (!isFav){
            fav.setBackgroundResource(R.drawable.ic_baseline_favorite_red)
            isFav = true
        }
        else{
            fav.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
            isFav = false
        } }
}

    private fun SingleVideoBinding.setupPlayer(url: String) {
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
                        Player.STATE_ENDED -> {
                            Log.d(TAG, "Video Ended")
                        }
                        Player.STATE_IDLE -> {
                            Log.d(TAG, "Video Idle")
                        }
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(root.context, "Could not play video due to: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            addMediaItem(MediaItem.fromUri(url))
            prepare()
            videoView.videoSurfaceView?.setOnClickListener {
                onVideoClickListener.onClick(this)
            }
        }
    }

    override fun getItemCount() = videoList.size

    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.videoView.player?.play()
    }

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.videoView.player?.pause()
    }



    companion object {
        const val TAG = "VIDEO_ADAPTER_LOG_TAG"
    }
}