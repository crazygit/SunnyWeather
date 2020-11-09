package com.github.crazygit.demo.sunnyweather

import android.app.Application
import android.content.Context


class SunnyWeatherApplication : Application() {

    companion object {
        const val TOKEN = BuildConfig.CAIYUN_TOKEN
        lateinit var context: Context
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}