package com.app.yoursafetyfirst.ui.signup

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.request.RegisterRequest
import com.app.yoursafetyfirst.response.RegisterResponse
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.Constants.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    var apiRepository: ApiRepository, var application: Application
) : ViewModel() {

    private val _response = MutableLiveData<NetworkResult<RegisterResponse>>()
    val registerResponse: LiveData<NetworkResult<RegisterResponse>> = _response

    private val _validationResponse = MutableLiveData<Int>()
    val validationResponse: LiveData<Int> = _validationResponse


    val emailObserver = ObservableField<String>()
    val passwordObserver = ObservableField<String>()
    val confirmPasswordObserver = ObservableField<String>()
    val phoneNumberObserver = ObservableField<String>()
    val userNameObserver = ObservableField<String>()
    val genderObserver = ObservableField<String>()
    val yearObserver = ObservableField<String>()
    val heightObserver = ObservableField<String>()
    val weightObserver = ObservableField<String>()
    val heartObserver = ObservableField<String>()
    val companyCodeObserver = ObservableField<String>()
    val ringIdObserver = ObservableField<String>()
    val ringObserver = ObservableField<Boolean>()
    val ringClickedObserver = ObservableField<Boolean>()
    val ringUseObserver = ObservableField<String>()


    suspend fun registerUser(language: String) {

        var email: String = ""
        var password: String = ""
        var confirmPassword: String = ""
        var username: String = ""
        var phoneNumber: String = ""
        var gender: String = ""
        var height: Int = 0
        var weight: Int = 0
        var heart: Int = 0
        var companyCode: String = ""
        var ringId: String = ""
        var ringUse: Boolean = false
        var ringClicked: Boolean = false
        var year: String = ""
        var ringUseOrNot: String = ""

        ringUseObserver.get()?.let {
            ringUseOrNot = it
        }

        ringClickedObserver.get()?.let {
            ringClicked = it
        }

        emailObserver.get()?.let {
            email = it
        }

        passwordObserver.get()?.let {
            password = it
        }

        confirmPasswordObserver.get()?.let {
            confirmPassword = it
        }

        userNameObserver.get()?.let {
            username = it
        }

        phoneNumberObserver.get()?.let {
            phoneNumber = it
        }

        genderObserver.get()?.let {
            gender = it
        }

        heightObserver.get()?.let {
            height = if (it.isEmpty()) {
                0
            } else {
                it.toInt()
            }
        }

        weightObserver.get()?.let {
            weight = if (it.isEmpty())
                0
            else
                it.toInt()
        }


        heartObserver.get()?.let {
            heart = if (it.isEmpty())
                0
            else
                it.toInt()
        }

        companyCodeObserver.get()?.let {
            companyCode = it
        }
        ringIdObserver.get()?.let {
            ringId = it
        }
        ringObserver.get()?.let {
            ringUse = it
        }
        yearObserver.get()?.let {
            year = it
        }


        if (username.isEmpty())
            _validationResponse.postValue(R.string.enter_username_)
        else if (email.isEmpty())
            _validationResponse.postValue(R.string.enter_email)
        else if (!Constants.isValidString(email))
            _validationResponse.postValue(R.string.enter_valid_email)
        else if (password.isEmpty())
            _validationResponse.postValue(R.string.enter_password_)
        else if (!isValidPassword(password))
            _validationResponse.postValue(R.string.password_weak)
        else if (confirmPassword.isEmpty())
            _validationResponse.postValue(R.string.enter_confirm_password_)
        else if (confirmPassword != password)
            _validationResponse.postValue(R.string.password_match)
        /*  else if (phoneNumber.isEmpty())
              _validationResponse.postValue(R.string.enter_phonenumber_)*/
        else if (phoneNumber.isNotEmpty() && phoneNumber.length < 11)
            _validationResponse.postValue(R.string.valid_number)
        else if (gender.equals("Select gender", true))
            _validationResponse.postValue(R.string.enter_gender)
        /*  else if (year.isEmpty())
              _validationResponse.postValue(R.string.enter_birth)*/
        /*else if (height == 0)
            _validationResponse.postValue(R.string.enter_height_)*/
        else if (height > 0 && height < 54 || height > 300)
            _validationResponse.postValue(R.string.height_min)
        /* else if (weight == 0)
             _validationResponse.postValue(R.string.enter_weight_)*/
        else if (weight > 0 && weight < 40 || weight > 360)
            _validationResponse.postValue(R.string.weight_min)
        /* else if (heart == 0)
             _validationResponse.postValue(R.string.enter_heart)*/
        else if (heart > 0 && heart < 40 || heart > 440)
            _validationResponse.postValue(R.string.heart_min)
        /*   else if (companyCode.isEmpty())
               _validationResponse.postValue(R.string.enter_companycode)*/
        else if (companyCode.isNotEmpty() && companyCode.length < 5)
            _validationResponse.postValue(R.string.invalid_companycode)
        else if (ringUse && ringId.isEmpty())
            _validationResponse.postValue(R.string.please_enter_ring_id)
        else if (!ringClicked)
            _validationResponse.postValue(R.string.select_ring)
        else {
            _response.postValue(NetworkResult.Loading())
            try {
                apiRepository.registerUser(
                    RegisterRequest(
                        username,
                        email,
                        phoneNumber,
                        gender,
                        password,
                        confirmPassword,
                        "active",
                        "Android",
                        "",
                        companyCode,
                        year,
                        if (height == 0) {
                            ""
                        } else {
                            height.toString()
                        },
                        if (weight == 0) {
                            ""
                        } else {
                            weight.toString()
                        },
                        if (heart == 0) {
                            ""
                        } else {
                            heart.toString()
                        },
                        ringId,
                        ringUse,
                        "DRIVER",
                        language
                    )
                ).let {
                    if (it.statusCode == 200) {
                        _response.postValue(NetworkResult.Success(it.data!!))
                    } else {
                        _response.postValue(NetworkResult.Validation(it.message!!))
                    }

                }
            } catch (e: IOException) {
                _response.postValue(NetworkResult.Error(e.message.toString(), null))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _response.postValue(
                        NetworkResult.Error(
                            "401",
                            null
                        )
                    )
                } else {
                    _response.postValue(
                        NetworkResult.Error(
                            "Email is already registered with us.",
                            null
                        )
                    )
                }

            }
        }
    }
}