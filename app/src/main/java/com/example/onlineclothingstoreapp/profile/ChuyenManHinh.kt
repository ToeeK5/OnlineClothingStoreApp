package com.example.onlineclothingstoreapp.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.onlineclothingstoreapp.R

object ChuyenManHinh {

    fun MoFragment(activity: FragmentActivity, fragment: Fragment) {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun QuayLai(activity: FragmentActivity) {
        activity.supportFragmentManager.popBackStack()
    }
}