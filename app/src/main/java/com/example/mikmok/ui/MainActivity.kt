package com.example.mikmok.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mikmok.R
import com.example.mikmok.data.models.MediaFeed
import com.example.mikmok.databinding.ActivityMainBinding
import com.example.mikmok.util.Constants
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
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
                Log.v(TAG, e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    val result = Gson().fromJson(jsonString, MediaFeed::class.java)
                    Log.v(TAG, result.toString())
                    runOnUiThread {

                    }
                }
            }

        })
    }

    companion object {
        const val TAG = "MainActivity"
    }
}