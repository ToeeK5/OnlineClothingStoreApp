package com.example.onlineclothingstoreapp.models

data class User(
    var id: String = "",        // uid từ Firebase
    var username: String = "",  // tên hiển thị (Tên đăng nhập)
    var email: String = ""      // email login
)