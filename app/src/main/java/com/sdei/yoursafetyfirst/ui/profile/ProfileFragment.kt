package com.app.yoursafetyfirst.ui.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.app.yoursafetyfirst.BaseFragment
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.FragmentProfileBinding
import com.app.yoursafetyfirst.databinding.ProgressBarFalseBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.MainActivity
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.ui.login.LoginActivity
import com.app.yoursafetyfirst.utils.CustomSpinnerAdapter
import com.app.yoursafetyfirst.utils.Language
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.LocaleHelper
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.YearPickerDialog
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    lateinit var language: String
    private var driverId: String = ""
    var token: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = profileViewModel
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

        if (checkForInternet(requireContext())) {
            lifecycleScope.launch(Dispatchers.IO) {
                LocalData(requireContext()).driverID.first().let {
                    if (it.isNotEmpty()) {
                        LocalData(requireContext()).token.first().let { token ->
                            profileViewModel.getProfile(token, it)
                        }

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




        binding.topBar.heading.text = getString(R.string.title_profile)

        binding.genderSpinner.adapter =
            CustomSpinnerAdapter(requireActivity(), resources.getStringArray(R.array.Gender))

        binding.languageSpinner.adapter =
            CustomSpinnerAdapter(requireActivity(), resources.getStringArray(R.array.Language))

        binding.changePassword.setOnClickListener {
            ChangePasswordActivity.start(requireContext())
        }

        binding.logout.setOnClickListener {
            val mBuilder =
                MaterialAlertDialogBuilder(requireContext(), R.style.RoundShapeTheme).create()
            val view = layoutInflater.inflate(R.layout.logout_dialog, null)
            val title = view.findViewById<TextView>(R.id.title)
            val no = view.findViewById<AppCompatButton>(R.id.no)
            val yes = view.findViewById<AppCompatButton>(R.id.yes)
            mBuilder.setView(view)
            mBuilder.show()

            title.text = getString(R.string.logout_title)

            yes.setOnClickListener {
                mBuilder.dismiss()
                if (checkForInternet(requireContext())) {
                    lifecycleScope.launch {
                        LocalData(requireContext()).token.first().let {
                            profileViewModel.logout(it)
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

            no.setOnClickListener {
                mBuilder.dismiss()
            }
        }


        binding.deleteProfile.setOnClickListener {
            val mBuilder =
                MaterialAlertDialogBuilder(requireContext(), R.style.RoundShapeTheme).create()
            val view = layoutInflater.inflate(R.layout.logout_dialog, null)
            val title = view.findViewById<TextView>(R.id.title)
            val no = view.findViewById<AppCompatButton>(R.id.no)
            val yes = view.findViewById<AppCompatButton>(R.id.yes)
            mBuilder.setView(view)
            mBuilder.show()

            title.text = getString(R.string.delete_user_account)

            yes.setOnClickListener {
                mBuilder.dismiss()
                if (checkForInternet(requireContext())) {
                    lifecycleScope.launch {
                        LocalData(requireContext()).driverID.first().let {
                            driverId = it
                        }
                        LocalData(requireContext()).token.first().let {
                            profileViewModel.deleteAccount(it, driverId)
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

            no.setOnClickListener {
                mBuilder.dismiss()
            }
        }



        binding.updateProfile.setOnClickListener {
            if (checkForInternet(requireContext())) {
                lifecycleScope.launch {
                    LocalData(requireActivity()).token.first().let {
                        token = it
                    }
                    LocalData(requireActivity()).driverID.first().let { driverId ->
                        profileViewModel.updateProfile(token ?: "", driverId)
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

        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                //val genderArray = resources.getStringArray(R.array.Gender)
                val genderArray = arrayOf("Select gender", "Male", "Female", "Other")
                profileViewModel.genderObserver.set(genderArray[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val languageArray = resources.getStringArray(R.array.Language)
                    profileViewModel.languageObserver.set(languageArray[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        binding.birthYear.setOnClickListener {
            YearPickerDialog(Date()).apply {
                setListener { view, year, month, dayOfMonth ->
                    profileViewModel.yearObserver.set(year.toString())
                }
            }.show(requireActivity().supportFragmentManager, "MonthYearPickerDialog")
        }


        binding.ringUse.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = binding.root.findViewById(checkedId)
            if (radio.text.toString().equals("yes", true) || radio.text.toString()
                    .equals("はい", true)
            ) {
                binding.ring = true
                profileViewModel.ringObserver.set(binding.ring)
            } else {
                binding.ring = false
                profileViewModel.ringObserver.set(binding.ring)
                binding.ringId.text!!.clear()
            }
        }
    }

    var dialog : Dialog?=null
    fun showProgressBar(){
        dialog=Dialog(requireContext())
        val dialogBinding=ProgressBarFalseBinding.inflate(LayoutInflater.from(requireContext()))
        dialog?.setContentView(dialogBinding.root)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.create()
        dialog?.show()
    }


    override fun observer() {
        profileViewModel.profileResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    profileViewModel.emailObserver.set(it.data!![0].email)
                    profileViewModel.phoneObserver.set(it.data[0].phone)
                    profileViewModel.genderObserver.set(it.data[0].gender)
                    profileViewModel.nameObserver.set(it.data[0].name)
                    profileViewModel.companyCodeObserver.set(it.data[0].corporateCode)
                    profileViewModel.yearObserver.set(it.data[0].yearOfBirth)
                    profileViewModel.heightObserver.set(it.data[0].height)
                    profileViewModel.weightObserver.set(it.data[0].weight)
                    profileViewModel.heartObserver.set(it.data[0].restingHeartRate)
                    profileViewModel.ringObserver.set(it.data[0].ringUse)
                    profileViewModel.ringIdObserver.set(it.data[0].ringId)
                    profileViewModel.languageObserver.set(it.data[0].language)

                    binding.ring = it.data[0].ringUse

                    if (it.data[0].ringUse)
                        binding.ringUse.check(R.id.yes)
                    else
                        binding.ringUse.check(R.id.no)


                    val englishGenderArray = listOf("Select gender", "Male", "Female", "Other")
                    for (i in englishGenderArray.indices) {
                        if (englishGenderArray[i].equals(it.data[0].gender, true)) {
                            binding.genderSpinner.setSelection(i)
                        }
                    }

                    val languageArray = resources.getStringArray(R.array.Language)
                    for (i in languageArray.indices) {
                        if (languageArray[i].equals(it.data[0].language, true)) {
                            binding.languageSpinner.setSelection(i)
                            if (languageArray[i] == "English") {
                                lifecycleScope.launch {
                                    LocaleHelper.setLocale(requireContext(), "en")
                                }
                            } else {
                                lifecycleScope.launch {
                                    LocaleHelper.setLocale(requireContext(), "ja")
                                }
                            }
                        }
                    }
                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            requireActivity(),
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            requireActivity(),
                            language
                        )
                }
            }

        }


        profileViewModel.commonResponse.observe(viewLifecycleOwner) { it ->
            when (it) {
                is NetworkResult.Error -> {
                    //binding.progress.progressBar = false
                    dialog?.dismiss()
                    if (it.message == "401") {
                        LanguageActivity.start(requireActivity())
                    }
                }

                is NetworkResult.Loading -> {
                    showProgressBar()
                    //binding.progress.progressBar = true

                }

                is NetworkResult.Success -> {
                    //binding.progress.progressBar = false
                    dialog?.dismiss()

                    /*  reason for adding this in separate condition is to initialize value before
                        attachBaseContext get called  */
                    if (profileViewModel.languageObserver.get() == "English") {
                        DriverSafetyApp.selectedLanguage = "en"
                    } else {
                        DriverSafetyApp.selectedLanguage = "ja"

                    }

                    if (profileViewModel.languageObserver.get() == "English") {
                        LocaleHelper.setLocale(requireContext(), "en")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.data?.en!!,
                            requireActivity(),
                            language
                        )

                        lifecycleScope.launch(Dispatchers.Main) {
                            //delay(1500L)
                            MainActivity.start(requireActivity())
                        }


                    } else {
                        dialog?.dismiss()
                        LocaleHelper.setLocale(requireContext(), "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.data?.ja!!,
                            requireActivity(),
                            language
                        )

                        lifecycleScope.launch(Dispatchers.Main) {
                           // delay(1500L)
                            MainActivity.start(requireActivity())
                        }
                    }


                }

                is NetworkResult.Validation -> {
                   // binding.progress.progressBar = false
                    dialog?.dismiss()
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            requireActivity(),
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            requireActivity(),
                            language
                        )
                }
            }
        }

        profileViewModel.logoutResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    lifecycleScope.launch {
                        LocalData(requireContext()).clearDataStore()
                        LoginActivity.startWithFinish(requireActivity())
                    }

                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            requireActivity(),
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            requireActivity(),
                            language
                        )

                }
            }

        }

        profileViewModel.deleteAccountResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        lifecycleScope.launch {
                            LocalData(requireContext()).clearDataStore()
                            LanguageActivity.start(requireActivity())
                        }
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    lifecycleScope.launch {
                        LocalData(requireContext()).clearDataStore()
                        LanguageActivity.start(requireActivity())
                    }

                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            requireActivity(),
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            requireActivity(),
                            language
                        )

                }
            }
        }

        profileViewModel.validationResponse.observe(viewLifecycleOwner) {
            ShowSnackBar.showBar(binding.frameLayout, it, requireActivity(), language)
        }
    }


}