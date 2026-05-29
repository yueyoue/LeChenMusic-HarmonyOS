package com.lechen.music

import android.app.Application
import com.lechen.music.api.SubsonicClient
import com.lechen.music.repository.MusicRepository

class LeChenApp : Application() {
    val subsonicClient = SubsonicClient()
    val repository = MusicRepository(subsonicClient)

    companion object {
        lateinit var instance: LeChenApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
