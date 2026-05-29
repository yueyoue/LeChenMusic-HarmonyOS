package com.lechen.music.util

expect object SubsonicAuth {
    fun generateToken(password: String, salt: String): String
    fun generateSalt(): String
}
