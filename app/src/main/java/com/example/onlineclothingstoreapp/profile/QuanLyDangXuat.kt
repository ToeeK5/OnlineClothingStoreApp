package com.example.onlineclothingstoreapp.profile

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.example.onlineclothingstoreapp.activities.auth.LoginActivity

object QuanLyDangXuat {

    fun DangXuat(activity: FragmentActivity) {

        val intent =
            Intent(activity, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        activity.startActivity(intent)

        activity.finish()
    }
}