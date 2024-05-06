package com.app.yoursafetyfirst.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityMainBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.request.FCMRequest
import com.app.yoursafetyfirst.ui.dashboard.DashboardFragment
import com.app.yoursafetyfirst.ui.notifications.NotificationsFragment
import com.app.yoursafetyfirst.ui.physicalcondition.PhysicalConditionFragment
import com.app.yoursafetyfirst.ui.prevoiusdata.PreviousDataFragment
import com.app.yoursafetyfirst.ui.profile.ProfileFragment
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), EasyPermissions.PermissionCallbacks {

    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()

    private var notificationsFragment: NotificationsFragment = NotificationsFragment()
    private var profileFragment: ProfileFragment = ProfileFragment()
    private var previousFragment: PreviousDataFragment = PreviousDataFragment()

    //private var physicalConditionFragment: PhysicalConditionFragment = PhysicalConditionFragment()
    //private var dashboardFragment: DashboardFragment = DashboardFragment()
    var navView: BottomNavigationView? = null

//    private val stepCountReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//
//            var todayCount = intent.getStringExtra("stepCount").toString()
//            lifecycleScope.launch {
//                LocalData(this@MainActivity).storeTodayStepCount(todayCount.toInt())
//            }
//            var date = intent.getStringExtra("date").toString()
//            Log.e("100", "PulseOnGraphActivity stepCount: $todayCount")
//            Log.e("100", "PulseOnGraphActivity date: $date")
//        }
//    }

    companion object {
        @JvmStatic
        fun start(context: Activity) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
            context.finishAffinity()
        }

        @JvmStatic
        fun startFromNotification(context: Activity) {
            val starter = Intent(context, MainActivity::class.java)
            starter.putExtra("from", "notification")
            context.startActivity(starter)
            context.finishAffinity()
        }


    }


    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    private var firebaseToken: String = ""
    private var language: String = ""
    private var driverId: String = ""
    private val ACTIVITYRECOGNITIONPERMISSION = 456

    override fun observer() {
        mainViewModel.updateTokenResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    Log.e("aaa", it.message.toString())
                }

                is NetworkResult.Loading -> {
                }

                is NetworkResult.Success -> {
                    Log.e("aaa", "firebase token updated")

                }

                is NetworkResult.Validation -> {
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@MainActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@MainActivity,
                            language
                        )
                }
            }
        }
    }

    override fun onCreate() {
        navView = binding.navView
        if (hasCameraPermission()) {
            // service start for step count in background
           /* val intent = Intent(this, StepDetectorService::class.java)
            startService(intent)*/
        } else {
           // checkPermissionTask()
        }

        if (intent.hasExtra("from")) {
            binding.navView.selectedItemId = R.id.navigation_notifications
            loadFragment(notificationsFragment)
        } else {
            loadFragment(DashboardFragment())
        }

        // Register the broadcast receiver
//        val filter = IntentFilter("com.app.driversafety.STEP_COUNT_UPDATE")
//        registerReceiver(stepCountReceiver, filter)

        navView?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_physicalCondition -> {
                    //navController.navigate(R.id.navigation_physicalCondition, null)
                    loadFragment(PhysicalConditionFragment())

                }

                R.id.navigation_dashboard -> {
                    //navController.navigate(R.id.navigation_dashboard, null)
                    loadFragment(DashboardFragment())

                }

                R.id.navigation_notifications -> {
                    //navController.navigate(R.id.navigation_notifications, null)
                    loadFragment(notificationsFragment)
                }

                R.id.navigation_previousData -> {
                    //navController.navigate(R.id.navigation_previousData, null)
                    loadFragment(previousFragment)
                }

                R.id.navigation_profile -> {
                    //navController.navigate(R.id.navigation_profile, null)
                    loadFragment(profileFragment)
                }
            }
            true
        }

        /* navView.setOnItemReselectedListener {
             return@setOnItemReselectedListener
         }*/

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                lifecycleScope.launch {
                    LocalData(this@MainActivity).driverID.first().let {
                        driverId = it
                    }
                }

                lifecycleScope.launch {
                    LocalData(this@MainActivity).firebaseToken.first().let {
                        firebaseToken = it

                    }
                }

                if (checkForInternet(this@MainActivity)) {
                    mainViewModel.updateToken(
                        FCMRequest(
                            driverId,
                            token,
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                        )
                    )
                } else {
                    ShowSnackBar.showBar(
                        binding.container,
                        R.string.no_internt,
                        this@MainActivity,
                        DriverSafetyApp.selectedLanguage
                    )
                }

                lifecycleScope.launch {
                    LocalData(this@MainActivity).storeFireBaseToken(token)
                    cancel()
                }
                //}

                lifecycleScope.launch {
                    LocalData(this@MainActivity).language.first().let {
                        language = it

                    }

                }


            })

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_main, fragment)
            commit()
        }
    }


    private fun checkPermissionTask() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.rationale_activity_recogination),
            ACTIVITYRECOGNITIONPERMISSION,
            Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.POST_NOTIFICATIONS
        )

    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        /*val intent = Intent(this, StepDetectorService::class.java)
        startService(intent)*/
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
          There was an error signing into Fit. Check the troubleshooting section of the README
          for potential issues.
          Request code was: $requestCode
          Result code was: $resultCode
      """.trimIndent()
        Log.e("TAG", message)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
               /* val intent = Intent(this, StepDetectorService::class.java)
                startService(intent)*/
            }

            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }


}
