package com.app.yoursafetyfirst.utils

import android.text.TextUtils
import java.util.regex.Pattern




object Constants {



    // production
    const val BASE_URL = "https://google.com"

    const val NETWORK_TIMEOUT = 60L
    const val CHECKBOX = "checkboxes"
    const val OPTION = "option"
    const val DROPDOWN = "dropdown"
    const val TRAFFIC_SAFETY_INFORMATION = "Traffic Safety Information"
    const val ARTICLES_BLOGS = "Articles & Blogs"
    const val LANGUAGE = "language"
    const val PHYSICAL_ID = "ID"
    const val FROM = "from"
    const val VIDEO = "video"
    const val IMAGE = "image"
    const val ID = "id"


    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z\\-]{0,25}" +
                ")+"
    )

    fun isValidString(str: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    fun isValidPassword(s: String?): Boolean{
        val PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*_=+-]).{8,50}\$")
        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches()
    }


    private var footstepList= mutableListOf<Int>()
    fun setFootSteps(step:Int)
    {
        if (step==0)
        {
            footstepList.add(0,0)
        }else{
            footstepList.add(0,step)
        }
    }

    fun getFootSteps()= footstepList.firstOrNull()


}