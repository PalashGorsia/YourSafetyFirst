package com.app.yoursafetyfirst.ui.notifications

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityNotificationDetailsBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.MultiImageModel
import com.app.yoursafetyfirst.response.Notification
import com.app.yoursafetyfirst.ui.MainActivity
import com.app.yoursafetyfirst.ui.dashboard.SafetyPagerAdapter
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.DateConversion
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.NotificationType
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationDetailsActivity : BaseActivity<ActivityNotificationDetailsBinding>() {

    private val notificationsViewModel by viewModels<NotificationsViewModel>()

    companion object {
        private const val NOTIFICATION_DATA = "notification_data"

        @JvmStatic
        fun start(context: Context, notification: Notification) {
            val starter = Intent(context, NotificationDetailsActivity::class.java)
            starter.putExtra(NOTIFICATION_DATA, notification)
            context.startActivity(starter)
        }

    }

    var notification: Notification? = null
    private lateinit var notificatinId: String

    override fun onCreate() {
        if (intent.hasExtra("notificationId")) {
            notificatinId = intent.getStringExtra("notificationId").toString()
            getNotification(notificatinId)
        } else {
            notification = intent.getParcelableExtra(NOTIFICATION_DATA) as Notification?
            setData(notification)
        }


        binding.topBar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.topBar.heading.text = getString(R.string.title_notifications)


    }

    private fun setData(notification: Notification?) {
        notification?.let { notification ->
            binding.time.text = DateConversion.utcConversion(notification.createdAt.toString())

            when (notification.type) {
                NotificationType.NoticeSDNC.getValue() -> {
                    binding.imageType.load(R.drawable.information)
                }

                NotificationType.NoticeApplication.getValue() -> {
                    binding.imageType.load(R.drawable.tool_icon)
                }

                NotificationType.NoticeTransportation.getValue() -> {
                    binding.imageType.load(R.drawable.baseline_warning_24)

                }
            }
            lifecycleScope.launch {
                LocalData(this@NotificationDetailsActivity).language.first().let {
                    if (it == "en") {
                        binding.title.text = notification.title?.en
                        binding.message.text = notification.message?.en
                    } else {
                        binding.title.text = notification.title?.ja
                        binding.message.text = notification.message?.ja

                    }
                }
            }

            if (notification.image.size > 0) {
                binding.cardView.visibility = View.VISIBLE
                val arrayList = ArrayList<MultiImageModel>()

                for (i in 0..<notification.image.size) {
                    val multiImageModel = MultiImageModel(
                        Constants.IMAGE, "",
                        Constants.IMAGE_URL + notification.type + "/" + notification.image[i],
                    )
                    arrayList.add(multiImageModel)
                }

                binding.viewPager.adapter =
                    SafetyPagerAdapter(this@NotificationDetailsActivity, arrayList) {}
                binding.indicator.attachToPager(binding.viewPager)

            } else {
                binding.cardView.visibility = View.GONE
            }
        }
    }

    override fun getViewBinding(): ActivityNotificationDetailsBinding =
        ActivityNotificationDetailsBinding.inflate(layoutInflater)

    override fun observer() {
        notificationsViewModel.notificationDetailResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        LanguageActivity.start(this)
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@NotificationDetailsActivity,
                            DriverSafetyApp.selectedLanguage
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    setData(it.data?.get(0))
                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (DriverSafetyApp.selectedLanguage == "Japanese" || DriverSafetyApp.selectedLanguage == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@NotificationDetailsActivity,
                            DriverSafetyApp.selectedLanguage
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@NotificationDetailsActivity,
                            DriverSafetyApp.selectedLanguage
                        )

                }
            }

        }

    }

    private fun getNotification(id: String) {
        if (checkForInternet(this@NotificationDetailsActivity)) {
            lifecycleScope.launch {
                LocalData(this@NotificationDetailsActivity).token.first().let {
                    notificationsViewModel.getNotification(it, id)
                }
            }
        } else {
            ShowSnackBar.showBar(
                binding.frameLayout,
                R.string.no_internt,
                this@NotificationDetailsActivity,
                DriverSafetyApp.selectedLanguage
            )
        }
    }


    override fun onBackPressed() {
        if (intent.hasExtra("notificationId")) {
            MainActivity.startFromNotification(this@NotificationDetailsActivity)
        } else {
            super.onBackPressed()
        }
    }
}