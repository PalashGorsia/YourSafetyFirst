package com.app.yoursafetyfirst.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.yoursafetyfirst.BaseFragment
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.FragmentNotificationsBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.Notification
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.utils.Language
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private val notificationsViewModel by viewModels<NotificationsViewModel>()
    private var notificationAdapter: NotificationAdapter? = null
    var notificationList = ArrayList<Notification>()
    var pageNumber: Int = 1
    var recordCount: Int = 0
    lateinit var language: String


    override fun getViewBinding(): FragmentNotificationsBinding =
        FragmentNotificationsBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()


        lifecycleScope.launch {
            LocalData(requireActivity()).language.first().let {
                language = if (it == "en") {
                    Language.English().x
                } else {
                    Language.Japanese().x

                }
            }
        }

        if (notificationList.isEmpty()) {
            pageNumber = 1
            getNotificationList(pageNumber)
        } else {
            setOnlyAdapter()
        }



        binding.appBar.heading.text = getString(R.string.title_notifications)

        binding.swiperefresh.setOnRefreshListener {
            binding.swiperefresh.isRefreshing = false
            notificationList.clear()
            pageNumber = 1
            getNotificationList(pageNumber)
        }


        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == notificationList.size - 1 && notificationList.size < recordCount) {
                    pageNumber += 1
                    getNotificationList(pageNumber)
                }
            }
        })
    }


    private fun setOnlyAdapter() {
        binding.showList = true
        lifecycleScope.launch {
            LocalData(requireContext()).language.first().let { _it ->
                notificationAdapter =
                    NotificationAdapter(requireActivity(), notificationList, _it) {
                        NotificationDetailsActivity.start(
                            requireContext(), notificationList[it]
                        )
                    }
            }
        }
        binding.recyclerView.adapter = notificationAdapter
    }

    override fun observer() {
        notificationsViewModel.notificationListResponse.observe(viewLifecycleOwner) { it ->
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        lifecycleScope.launch {
                            LocalData(requireContext()).clearDataStore()
                            LanguageActivity.start(requireActivity())
                        }
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout, it.message!!, requireActivity(), language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false

                    if (it.data?.notifications!!.isNotEmpty()) {
                        if (!notificationList.contains(it.data.notifications[0])) {
                            notificationList.addAll(it.data.notifications)
                        }
                    }


                    if (notificationList.isNotEmpty()) {
                        recordCount = it.data.total
                        setAdapter(notificationList)
                    } else {
                        recordCount = 0
                        setAdapter(notificationList)
                    }
                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (DriverSafetyApp.selectedLanguage == "ja" || DriverSafetyApp.selectedLanguage == "Japanese") ShowSnackBar.showBarString(
                        binding.frameLayout, it.validationMessage?.ja!!, requireActivity(), language
                    ) else ShowSnackBar.showBarString(
                        binding.frameLayout, it.validationMessage?.en!!, requireActivity(), language
                    )

                }

            }
        }

    }

    private fun getNotificationList(pageNumber: Int) {
        if (checkForInternet(requireContext())) {
            lifecycleScope.launch(Dispatchers.IO) {
                LocalData(requireContext()).token.first().let {
                    notificationsViewModel.getNotificationList(it, pageNumber.toString())
                }
            }
        } else {
            ShowSnackBar.showBar(
                binding.frameLayout,
                R.string.no_internt,
                requireActivity(),
                DriverSafetyApp.selectedLanguage
            )
        }
    }

    private fun setAdapter(notificationList: List<Notification>) {
        if (notificationList.isEmpty() && pageNumber == 1) {
            binding.showList = false
        } else if (notificationList.isNotEmpty() && pageNumber == 1) {
            //pageNumber++
            setOnlyAdapter()
        } else if (notificationList.isNotEmpty() && pageNumber > 1) {
            binding.recyclerView.adapter?.notifyItemInserted(notificationList.size)
        }
    }


}