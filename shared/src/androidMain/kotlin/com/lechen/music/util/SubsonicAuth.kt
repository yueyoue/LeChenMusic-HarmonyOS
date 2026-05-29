package com.lechen.music.util

import java.security.MessageDigest

actual object SubsonicAuth {
    actual fun generateToken(password: String, salt: String): String {
        val md = MessageDigest.getInstance("MD5")
        val combined = password + salt
        val digest = md.digest(combined.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    actual fun generateSalt(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..12).map { chars.random() }.joinToString("")
    }
}
