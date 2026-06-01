package com.example.onlineclothingstoreapp.applications

import android.app.Application
import androidx.emoji2.text.EmojiCompat
import androidx.emoji2.text.DefaultEmojiCompatConfig // Thêm import này

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Tự động lấy cấu hình hệ thống (Nó luôn mặc định chạy Asynchronous ngầm)
        val config = DefaultEmojiCompatConfig.create(this)

        if (config != null) {
            config.setReplaceAll(true)
            EmojiCompat.init(config)
        }
    }
}