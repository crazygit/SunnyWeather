package com.github.crazygit.demo.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.crazygit.demo.sunnyweather.R
import com.github.crazygit.demo.sunnyweather.databinding.ActivityWeatherBinding
import com.github.crazygit.demo.sunnyweather.helper.themeColor
import com.github.crazygit.demo.sunnyweather.logic.model.Weather
import com.github.crazygit.demo.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityWeatherBinding
    val viewModel: WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置全屏显示
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT


        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherData.observe(this) {
            val weather = it.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false
        }
        binding.swipeRefresh.setColorSchemeColors(themeColor(R.attr.colorPrimary))
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        refreshWeather()
        binding.nowLayoutInclude.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                // 关闭抽屉时隐藏输入法
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        binding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        binding.nowLayoutInclude.placeName.text = viewModel.placeName

        val realtime = weather.realtime
        binding.nowLayoutInclude.currentTemp.text = "${realtime.temperature.toInt()} °C"
        binding.nowLayoutInclude.currentSky.text = getSky(realtime.skycon).info
        binding.nowLayoutInclude.currentAQI.text = "空气指数: ${realtime.airQuality.aqi.chn.toInt()}"
        binding.nowLayoutInclude.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        val daily = weather.daily
        binding.forecastLayoutInclude.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item, binding.forecastLayoutInclude.forecastLayout, false
            )
            val skycon = daily.skycon[i]
            val sky = getSky(skycon.value)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val temperature = daily.temperature[i]
            view.findViewById<TextView>(R.id.dateInfo).text =
                simpleDateFormat.format(skycon.date)
            view.findViewById<ImageView>(R.id.skyIcon).setImageResource(sky.icon)
            view.findViewById<TextView>(R.id.skyInfo).text = sky.info
            view.findViewById<TextView>(R.id.temperatureInfo).text =
                "${temperature.min} ~ ${temperature.max} °C"
            binding.forecastLayoutInclude.forecastLayout.addView(view)
        }

        val lifeIndex = daily.lifeIndex
        binding.lifeIndexLayoutInclude.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.lifeIndexLayoutInclude.dressText.text = lifeIndex.dressing[0].desc
        binding.lifeIndexLayoutInclude.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.lifeIndexLayoutInclude.carWashingText.text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility = View.VISIBLE
    }
}