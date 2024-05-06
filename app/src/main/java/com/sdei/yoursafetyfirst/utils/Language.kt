package com.app.yoursafetyfirst.utils

sealed class Language(val x: String) {
        class English : Language("English")
        class Japanese : Language("Japanese")
    }