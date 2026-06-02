package com.example.onlineclothingstoreapp.profile

import com.example.onlineclothingstoreapp.R

object QuanLyAvatar {

    fun LayHinhAvatar(tenAvatar: String): Int {
        return when (tenAvatar) {
            "avatar_1" -> R.drawable.avatar_1
            "avatar_2" -> R.drawable.avatar_2
            "avatar_3" -> R.drawable.avatar_3
            "avatar_4" -> R.drawable.avatar_4
            "avatar_5" -> R.drawable.avatar_5
            "avatar_6" -> R.drawable.avatar_6
            else -> R.drawable.avatar_1
        }
    }
}