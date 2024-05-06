package com.app.yoursafetyfirst.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.yoursafetyfirst.BaseFragment
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.FragmentDashboardBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.DashboardData
import com.app.yoursafetyfirst.response.Data
import com.app.yoursafetyfirst.response.MultiImageModel
import com.app.yoursafetyfirst.response.TrafficResponse
import com.app.yoursafetyfirst.ui.MainActivity
import com.app.yoursafetyfirst.ui.dashboard.extension.autoScroll
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.ui.login.LoginActivity
import com.app.yoursafetyfirst.ui.physicalcondition.PhysicalConditionFragment
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.LocaleHelper
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.collections.set


@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private val viewModel: DashboardViewModel by viewModels()
    private var dashboardAdapter: DashboardAdapter? = null
    private val dashboardList = ArrayList<Data>()
    private var pageNumber: Int = 1
    var recordCount: Int = 0
    private var hashmapBeanList = HashMap<Int, ArrayList<MultiImageModel>>()
    private var safetyPagerAdapter: SafetyPagerAdapter? = null
    private var authorization: String? = null
    private var previousIdlePosition = 0

    private val trafficList = ArrayList<TrafficResponse>()
    private val arrayList = ArrayList<MultiImageModel>()
    private val arrayListData = ArrayList<DashboardData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   checkHealthConnectInstalledOrNot()
        observer()

        lifecycleScope.launch {
            LocalData(requireContext()).token.first().let {
                authorization = it
            }
        }

        binding.appBar.heading.text = getString(R.string.title_dashboard)

        if (checkForInternet(requireContext())) {
            if (dashboardList.isEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    authorization?.let {
                        viewModel.trafficInfoList(it)
                    }
                    getDashBoardList(pageNumber)
                }
            }
            lifecycleScope.launch {
                LocalData(requireContext()).language.first().let {
                    if (it.isEmpty() || it != DriverSafetyApp.selectedLanguage) {
                        authorization?.let { token ->
                            viewModel.getLanguage(token)
                        }

                    }
                }
            }
        } else {
            ShowSnackBar.showBar(
                binding.container,
                R.string.no_internt,
                requireActivity(),
                DriverSafetyApp.selectedLanguage
            )
        }

        binding.sdnc.setOnClickListener {
            (requireActivity() as MainActivity).navView?.selectedItemId =
                R.id.navigation_physicalCondition
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.nav_host_fragment_activity_main, PhysicalConditionFragment())
                commit()
            }
        }


        binding.recyclerView.itemAnimator?.changeDuration = 0
        binding.recyclerView.setItemViewCacheSize(4)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == hashmapBeanList.size - 1 && hashmapBeanList.size < recordCount) {
                    pageNumber += 1
                    getDashBoardList(pageNumber)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?

                when (newState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                        if (previousIdlePosition != linearLayoutManager?.findFirstCompletelyVisibleItemPosition()!!) {
                            dashboardAdapter?.idlePositionObserver?.postValue(linearLayoutManager.findFirstCompletelyVisibleItemPosition())
                        }


                    }

                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {
                        previousIdlePosition =
                            linearLayoutManager?.findFirstCompletelyVisibleItemPosition()!!

                    }
                }
            }

        })

    }


    private fun getDashBoardList(pageNumber: Int) {
        if (checkForInternet(requireContext())) {
            lifecycleScope.launch(Dispatchers.IO) {
                authorization?.let {
                    viewModel.getAllContent(it, pageNumber)
                }
            }
        }
    }

    private fun setAdapter(dashboardList: ArrayList<Data>) {
        if (dashboardList.isEmpty() && pageNumber == 1) {
            // binding.showList = false
        } else if (dashboardList.isNotEmpty() && pageNumber == 1) {
            lifecycleScope.launch {
                LocalData(requireContext()).language.first().let { _it ->
                    for (position in 0 until dashboardList.size) {
                        var type = ""
                        type = when (dashboardList[position].type) {
                            Constants.ARTICLES_BLOGS -> {
                                "Articles%20&%20Blogs"
                            }

                            Constants.TRAFFIC_SAFETY_INFORMATION -> {
                                "Traffic%20Safety%20Information"
                            }

                            else -> {
                                dashboardList[position].type
                            }
                        }

                        val beanList = ArrayList<MultiImageModel>()
                        if (dashboardList[position].images.isNotEmpty()) {
                            for (i in 0 until dashboardList[position].images.size) {
                                val beanImage = MultiImageModel(
                                    "Image",
                                    "",
                                    Constants.IMAGE_URL + type + "/" + dashboardList[position].images[i]
                                )
                                beanList.add(beanImage)
                            }
                        }

                        if (dashboardList[position].videos.isNotEmpty()) {
                            for (i in 0 until dashboardList[position].videos.size) {
                                val beanImage = MultiImageModel(
                                    "Video",
                                    Constants.VIDEO_URL + dashboardList[position].type + "/" + dashboardList[position].videos[i].videoURL,
                                    Constants.THUMBNAIL_URL + dashboardList[position].videos[i].thumbnailURL
                                )
                                beanList.add(beanImage)


                            }
                        }

                        hashmapBeanList[position] = beanList
                    }

                    dashboardAdapter = DashboardAdapter(
                        requireActivity(), dashboardList, _it, hashmapBeanList, viewLifecycleOwner
                    ) {
                        DialogActivity.start(
                            requireContext(),
                            hashmapBeanList[it]!!,
                            dashboardList[it].title,
                            dashboardList[it].description,
                            _it, ""
                        )
                    }
                }
            }
            binding.recyclerView.adapter = dashboardAdapter
            dashboardAdapter?.idlePositionObserver?.postValue(0)

        } else if (dashboardList.isNotEmpty() && pageNumber > 1) {
            for (position in 0 until dashboardList.size) {
                var type = ""
                type = when (dashboardList[position].type) {
                    Constants.ARTICLES_BLOGS -> {
                        "Articles%20&%20Blogs"
                    }

                    Constants.TRAFFIC_SAFETY_INFORMATION -> {
                        "Traffic%20Safety%20Information"
                    }

                    else -> {
                        dashboardList[position].type
                    }
                }

                val beanList = ArrayList<MultiImageModel>()
                if (dashboardList[position].images.isNotEmpty()) {
                    for (i in 0 until dashboardList[position].images.size) {
                        val beanImage = MultiImageModel(
                            Constants.IMAGE,
                            "",
                            Constants.IMAGE_URL + type + "/" + dashboardList[position].images[i]
                        )
                        beanList.add(beanImage)
                    }
                }

                if (dashboardList[position].videos.isNotEmpty()) {
                    for (i in 0 until dashboardList[position].videos.size) {
                        val beanImage = MultiImageModel(
                            Constants.VIDEO,
                            Constants.VIDEO_URL + dashboardList[position].type + "/" + dashboardList[position].videos[i].videoURL,
                            Constants.THUMBNAIL_URL + dashboardList[position].videos[i].thumbnailURL
                        )
                        beanList.add(beanImage)
                    }
                }

                hashmapBeanList[position] = beanList
            }

            binding.recyclerView.adapter?.notifyItemInserted(dashboardList.size)
        }
    }

    override fun getViewBinding(): FragmentDashboardBinding =
        FragmentDashboardBinding.inflate(layoutInflater)

    override fun observer() {
        viewModel.trafficListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    ShowSnackBar.showBarString(
                        binding.frameLayout,
                        "Session expire",
                        requireActivity(),
                        DriverSafetyApp.selectedLanguage
                    )
                    lifecycleScope.launch {
                        LocalData(requireContext()).clearDataStore()
                        LoginActivity.startWithFinish(requireActivity())
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    if (it.data?.data?.size!! > 0) {
                        if (!trafficList.contains(it.data.data[0])) {
                            trafficList.addAll(it.data.data)
                            for (i in it.data.data) {
                                if (i.images.isNotEmpty()) {
                                    for (img in 0 until i.images.size) {
                                        val multiImageModel = MultiImageModel(
                                            Constants.IMAGE,
                                            "",
                                            Constants.IMAGE_URL + i.type + "/" + i.images[img]
                                        )
                                        arrayList.add(multiImageModel)

                                        val dashboardData =
                                            DashboardData(i.url, i.title, i.description)
                                        arrayListData.add(dashboardData)
                                    }

                                }

                                if (i.videos.isNotEmpty()) {
                                    for (img in 0 until i.videos.size) {
                                        val multiImageModel = MultiImageModel(
                                            Constants.VIDEO,
                                            Constants.VIDEO_URL + i.type + "/" + i.videos[img].videoURL,
                                            ""
                                        )
                                        arrayList.add(multiImageModel)

                                        val dashboardData =
                                            DashboardData("", i.title, i.description)
                                        arrayListData.add(dashboardData)
                                    }
                                }

                            }
                        }

                        if (arrayList.isNotEmpty()) {
                            safetyPagerAdapter =
                                SafetyPagerAdapter(requireContext(), arrayList) { position ->
                                    lifecycleScope.launch {
                                        LocalData(requireContext()).language.first().let { _it ->
                                            val singleImage = ArrayList<MultiImageModel>()
                                            singleImage.add(arrayList[position])
                                            DialogActivity.start(
                                                requireContext(),
                                                singleImage,
                                                arrayListData[position].title,
                                                arrayListData[position].description,
                                                _it, arrayListData[position].url
                                            )
                                        }
                                    }
                                }
                            binding.viewPager.adapter = safetyPagerAdapter
                            binding.indicator.attachToPager(binding.viewPager)
                            binding.viewPager.autoScroll(9000)
                        }

                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            getString(R.string.oop_something_went_wrong),
                            requireActivity(),
                            DriverSafetyApp.selectedLanguage
                        )
                    }

                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (DriverSafetyApp.selectedLanguage == "ja" || DriverSafetyApp.selectedLanguage == "Japanese")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            requireActivity(),
                            DriverSafetyApp.selectedLanguage
                        )
                    else ShowSnackBar.showBarString(
                        binding.frameLayout,
                        it.validationMessage?.en!!,
                        requireActivity(),
                        DriverSafetyApp.selectedLanguage
                    )

                }
            }
        }

        viewModel.allContentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        lifecycleScope.launch {
                            LocalData(requireActivity()).clearDataStore()
                            LanguageActivity.start(requireActivity())
                        }
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            requireActivity(),
                            DriverSafetyApp.selectedLanguage
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true

                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false

                    if (!dashboardList.contains(it.data!!.data[0])) {
                        dashboardList.addAll(it.data.data)
                    }

                    if (dashboardList.isNotEmpty()) {
                        recordCount = it.data.total
                        setAdapter(dashboardList)
                    } else {
                        recordCount = 0
                        setAdapter(dashboardList)
                    }


                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (DriverSafetyApp.selectedLanguage == "ja" || DriverSafetyApp.selectedLanguage == "Japanese")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            requireActivity(),
                            DriverSafetyApp.selectedLanguage
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            requireActivity(),
                            DriverSafetyApp.selectedLanguage
                        )

                }
            }
        }


        viewModel.languageResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {
                }

                is NetworkResult.Success -> {
                    if (it.data?.language == "English") {
                        DriverSafetyApp.selectedLanguage = "en"
                    } else {
                        DriverSafetyApp.selectedLanguage = "ja"

                    }

                    if (it.data?.language == "English") {
                        LocaleHelper.setLocale(requireContext(), "en")

                    } else {
                        LocaleHelper.setLocale(requireContext(), "ja")
                    }
                }

                is NetworkResult.Validation -> {
                    if (DriverSafetyApp.selectedLanguage == "Japanese" || DriverSafetyApp.selectedLanguage == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            requireActivity(),
                            DriverSafetyApp.selectedLanguage
                        )
                    else ShowSnackBar.showBarString(
                        binding.frameLayout,
                        it.validationMessage?.en!!,
                        requireActivity(),
                        DriverSafetyApp.selectedLanguage
                    )


                }
            }
        }
    }


    /*private fun checkHealthConnectInstalledOrNot() {
        val availabilityStatus =
            HealthConnectClient.getSdkStatus(requireContext(), "com.app.driversafety")
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            Log.e("HC", "Health Connect installed already: ")
            return // early return as there is no viable integration
        }
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            Log.e("HC", "Health Connect not installed : ")

            // Optionally redirect to package installer to find a provider, for example:
            val uriString =
                "market://details?id=com.app.driversafety&url=healthconnect%3A%2F%2Fonboarding"
            this.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", "com.app.driversafety")
                }
            )
            return
        }
        val healthConnectClient = HealthConnectClient.getOrCreate(requireContext())
// Issue operations with healthConnectClient
    }*/


}