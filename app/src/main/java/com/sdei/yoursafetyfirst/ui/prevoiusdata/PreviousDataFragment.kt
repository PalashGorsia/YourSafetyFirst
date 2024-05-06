package com.app.yoursafetyfirst.ui.prevoiusdata

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.yoursafetyfirst.BaseFragment
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.FragmentPreviousDataBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.DataX
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class PreviousDataFragment : BaseFragment<FragmentPreviousDataBinding>(),
    DatePickerDialog.OnDateSetListener {


    private var driverId: String = ""
    private var search: String = ""
    private var pageNumber: Int = 1
    var recordCount: Int = 0
    private var previousAdapter: PreviousAdapter? = null
    val previousDataList = ArrayList<DataX>()
    private var myYear = "0"
    private var myday = "0"
    private var myMonth = "0"

    private val previousDataViewModel by viewModels<PreviousDataViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        lifecycleScope.launch {
            LocalData(requireContext()).driverID.first().let {
                driverId = it
            }
        }


        if (previousDataList.isEmpty()) {
            pageNumber = 1
            getPreviousDataList(pageNumber)
        } else {
            setOnlyAdapter()
        }


        binding.topBar.heading.text = getString(R.string.diagnosis_history)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == previousDataList.size - 1 && previousDataList.size < recordCount) {
                    pageNumber += 1
                    getPreviousDataList(pageNumber)
                }
            }
        })

        binding.search.setOnClickListener {
            Locale.setDefault(Locale.getDefault())
            val config: Configuration = requireActivity().resources.configuration
            config.setLocale(Locale.getDefault())
            requireActivity().createConfigurationContext(config)

            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.clear.setOnClickListener {
            search = ""
            binding.search.text = ""
            pageNumber = 1
            previousDataList.clear()
            getPreviousDataList(pageNumber)
        }

    }

    override fun getViewBinding(): FragmentPreviousDataBinding =
        FragmentPreviousDataBinding.inflate(layoutInflater)

    override fun observer() {
        previousDataViewModel.previousListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        LanguageActivity.start(requireActivity())
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

                    if (it.data?.data?.isNotEmpty()!!) {
                        if (!previousDataList.contains(it.data?.data!![0])) {
                            previousDataList.addAll(it.data.data)
                        }
                    }

                    if (previousDataList.isNotEmpty()) {
                        recordCount = it.data.total
                        setAdapter(previousDataList)
                    } else {
                        recordCount = 0
                        setAdapter(previousDataList)
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
    }

    private fun getPreviousDataList(pageNumber: Int) {
        if (checkForInternet(requireContext())) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (driverId.isNotEmpty()) {
                    LocalData(requireContext()).token.first().let {
                        previousDataViewModel.getPreviousList(
                            it,
                            driverId,
                            pageNumber.toString(),
                            search
                        )
                    }
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

    private fun setAdapter(previousDataList: ArrayList<DataX>) {
        if (previousDataList.isEmpty() && pageNumber == 1) {
            binding.showList = false
        } else if (previousDataList.isNotEmpty() && pageNumber == 1) {
            //pageNumber++
            setOnlyAdapter()
        } else if (previousDataList.isNotEmpty() && pageNumber > 1) {
            binding.recyclerView.adapter?.notifyItemInserted(previousDataList.size)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myYear = year.toString()
        myday = dayOfMonth.toString()
        myMonth = (month + 1).toString()

        if (myday.length == 1) {
            myday = "0$myday"
        }
        if (myMonth.length == 1) {
            myMonth = "0$myMonth"
        }


        binding.search.text = (myYear + "/" + myMonth + "/" + myday)
        search = binding.search.text.toString()
        pageNumber = 1
        previousDataList.clear()
        getPreviousDataList(pageNumber)
    }


    private fun setOnlyAdapter() {
        binding.showList = true
        lifecycleScope.launch {
            LocalData(requireContext()).language.first().let { _it ->
                previousAdapter =
                    PreviousAdapter(requireActivity(), previousDataList, _it) {
                        PreviousDataDetailActivity.start(
                            requireContext(),
                            previousDataList[it].declarationId,
                            previousDataList[it]._id
                        )
                    }
            }
        }
        binding.recyclerView.adapter = previousAdapter
    }

}