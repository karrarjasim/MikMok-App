package com.example.mikmok.ui

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.mikmok.R
import com.example.mikmok.data.models.MediaFeed
import com.example.mikmok.data.models.VideoState
import com.example.mikmok.databinding.ActivityMainBinding
import com.example.mikmok.util.Constants
import com.example.mikmok.interfaces.OnIconsClickListener
import com.example.mikmok.interfaces.OnVideoClickListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), OnIconsClickListener,Animator.AnimatorListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var videoAdapter: VideoAdapter
    private var videoState: VideoState? = null
    private var _isVisible=false
    private val client = OkHttpClient()

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
                Toast.makeText(
                    this@MainActivity,
                    "Failed to grab the data due to: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    val result = Gson().fromJson(jsonString, MediaFeed::class.java)
                    runOnUiThread {
                        binding.run {
                        videoAdapter = VideoAdapter(
                            result.feed.first().items,
                            object : OnVideoClickListener {
                                override fun onClick(exoPlayer: ExoPlayer) {
                            if (exoPlayer.isPlaying) {
                                    preparePlayer(exoPlayer,VideoState.PAUSED,R.raw.pause_animation,true)
                            } else if (videoState == VideoState.PAUSED) {
                                preparePlayer(exoPlayer,VideoState.RUNNING,R.raw.play_animation,false)
                                        }
                                    }
                            },
                            this@MainActivity
                            )
                            viewpagerHome.run {
                                adapter = videoAdapter
                                registerOnPageChangeCallback(object :
                                    ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        super.onPageSelected(position)
                                        binding.lavPauseResumeVideo.isVisible = false
                                    }
                                })
                            }
                        }
                    }
                }
            }
        })
    }

    fun preparePlayer(exoPlayer: ExoPlayer,videoState: VideoState,lottieAnimation:Int,isRunning:Boolean){
        if(isRunning){
            _isVisible=true
            exoPlayer.pause()
        }else{
            _isVisible=false
            exoPlayer.play()
        }
        this.videoState = videoState
        binding.lavPauseResumeVideo.run {
            removeAllAnimatorListeners()
            setAnimation(lottieAnimation)
            isVisible = true
            speed = 3f
            playAnimation()
            addAnimatorListener(this@MainActivity)
        }
    }


    override fun onClickShareIcon(url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type="text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent,  "Share URL")
        startActivity(shareIntent)

    }

    override fun onAnimationStart(animation: Animator?) {
        Log.d(TAG, "Start Pause Animation")
    }

    override fun onAnimationEnd(animation: Animator?) {
        binding.lavPauseResumeVideo.isVisible=_isVisible
    }

    override fun onAnimationCancel(animation: Animator?) {
        binding.lavPauseResumeVideo.isVisible=false
    }

    override fun onAnimationRepeat(animation: Animator?) {
        binding.lavPauseResumeVideo.isVisible=false

    }

    companion object {
        const val TAG = "MAIN_ACTIVITY_LOG_TAG"
    }
}