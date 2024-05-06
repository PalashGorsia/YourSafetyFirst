package com.app.yoursafetyfirst.ui.finaljudgement

import android.content.Context
import android.content.Intent
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityRegistrationCompleteBinding
import com.app.yoursafetyfirst.ui.MainActivity

class RegistrationCompleteActivity : BaseActivity<ActivityRegistrationCompleteBinding>() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, RegistrationCompleteActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate() {

        binding.topBar.heading.text = getString(R.string.sdnc_diagnosis)

        binding.topBar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnBackTop.setOnClickListener {
            MainActivity.start(this)
        }
    }

    override fun getViewBinding(): ActivityRegistrationCompleteBinding =
        ActivityRegistrationCompleteBinding.inflate(layoutInflater)

    override fun observer() {


    }
}