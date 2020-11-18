package com.github.crazygit.demo.sunnyweather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.crazygit.demo.sunnyweather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}