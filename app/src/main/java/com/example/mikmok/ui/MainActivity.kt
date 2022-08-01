package com.example.mikmok.ui

import android.animation.Animator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.mikmok.R
import com.example.mikmok.data.models.ExoPlayerItem
import com.example.mikmok.data.models.MediaFeed
import com.example.mikmok.data.models.VideoState
import com.example.mikmok.databinding.ActivityMainBinding
import com.example.mikmok.util.Constants
import com.example.mikmok.util.OnVideoClickListener
import com.example.mikmok.util.OnVideoPreparedListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    lateinit var videoAdapter: VideoAdapter
    private val client = OkHttpClient()

    private val exoPlayerItems = ArrayList<ExoPlayerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getVideos()
    }

    private fun getVideos() {
        val request = Request.Builder()
            .url(Constants.API_URL)
            .get()
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v(TAG, e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    val result = Gson().fromJson(jsonString, MediaFeed::class.java)
                    Log.v(TAG, result.toString())
                    runOnUiThread {
                        binding.run {
                            videoAdapter = VideoAdapter(result.feed.first().items, object : OnVideoPreparedListener {
                                override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                                    exoPlayerItems.add(exoPlayerItem)
                                }
                            }, object : OnVideoClickListener {
                                override fun onClick(exoPlayer: ExoPlayer) {
                                    if (exoPlayer.isPlaying) {
                                        exoPlayer.pause()
                                        videoState = VideoState.PAUSED
                                        binding.lavPauseResumeVideo.run {
                                            setAnimation(R.raw.pause_animation)
                                            isVisible = true
                                            speed = 3f
                                            playAnimation()
                                            addAnimatorListener(object : Animator.AnimatorListener {
                                                override fun onAnimationStart(animation: Animator?) {
                                                    Log.d(TAG, "Start Pause Animation")
                                                }

                                                override fun onAnimationEnd(animation: Animator?) {
                                                    Log.d(TAG, "End Pause Animation")
                                                }

                                                override fun onAnimationCancel(animation: Animator?) {
                                                    isVisible = false
                                                }

                                                override fun onAnimationRepeat(animation: Animator?) {
                                                    isVisible = false
                                                }

                                            })
                                        }
                                        Toast.makeText(this@MainActivity, "Video Paused", Toast.LENGTH_SHORT).show()
                                    } else if (videoState == VideoState.PAUSED) {
                                        exoPlayer.play()
                                        videoState = VideoState.RUNNING
                                        binding.lavPauseResumeVideo.run {
                                            setAnimation(R.raw.play_animation)
                                            isVisible = true
                                            speed = 3f
                                            playAnimation()
                                            addAnimatorListener(object : Animator.AnimatorListener {
                                                override fun onAnimationStart(animation: Animator?) {
                                                    Log.d(TAG, "Start Resume Animation")
                                                }

                                                override fun onAnimationEnd(animation: Animator?) {
                                                    isVisible = false
                                                }

                                                override fun onAnimationCancel(animation: Animator?) {
                                                    isVisible = false
                                                }

                                                override fun onAnimationRepeat(animation: Animator?) {
                                                    isVisible = false
                                                }

                                            })
                                        }
                                        Toast.makeText(this@MainActivity, "Video Resumed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                            recyclerHome.run {
                                adapter = videoAdapter
                                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        videoState = null
                                        binding.lavPauseResumeVideo.isVisible = false
                                        val previousPlayerIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                                        if (previousPlayerIndex != -1) exoPlayerItems[previousPlayerIndex].exoPlayer.playWhenReady = false
                                        val newPlayerIndex = exoPlayerItems.indexOfFirst { it.position == position }
                                        if (newPlayerIndex != -1) exoPlayerItems[newPlayerIndex].exoPlayer.play()
                                    }
                                })
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.recyclerHome.currentItem }
        if (index != -1) {
            exoPlayerItems[index].exoPlayer.run {
                pause()
                playWhenReady = false
                videoState = VideoState.PAUSED
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.recyclerHome.currentItem }
        if (index != -1) {
            exoPlayerItems[index].exoPlayer.run {
                playWhenReady = true
                play()
                videoState = null
                binding.lavPauseResumeVideo.isVisible = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayerItems.isNotEmpty()) {
            exoPlayerItems.forEach {
                it.exoPlayer.run {
                    stop()
                    clearMediaItems()
                }
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        private var videoState: VideoState? = null
    }
}