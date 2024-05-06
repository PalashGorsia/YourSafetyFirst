package com.app.yoursafetyfirst.ui.forgotpassword

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.CommonResponse
import com.app.yoursafetyfirst.response.ForgotPasswordResponse
import com.app.yoursafetyfirst.response.ResetResponse
import com.app.yoursafetyfirst.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class ForgotViewModel @Inject constructor(
    var apiRepository: ApiRepository,
    var application: Application
) : ViewModel() {

    private val _currentScreen = MutableLiveData<Int>()
    val currentScreen: LiveData<Int>
        get() = _currentScreen

    fun setScreenNumber(screen: Int = 1) {
        _currentScreen.value = screen
    }

    fun getScreenNumber(): Int? {
        return _currentScreen.value
    }


    private val _response = MutableLiveData<NetworkResult<ForgotPasswordResponse>>()
    val forgotResponse: LiveData<NetworkResult<ForgotPasswordResponse>> = _response

    private val _otpResponse = MutableLiveData<NetworkResult<CommonResponse>>()
    val otpResponse: LiveData<NetworkResult<CommonResponse>> = _otpResponse

    private val _resetPasswordResponse = MutableLiveData<NetworkResult<ResetResponse>>()
    val resetPasswordResponse: LiveData<NetworkResult<ResetResponse>> = _resetPasswordResponse


    private val _validationResponse = MutableLiveData<Int>()
    val validationResponse: LiveData<Int> = _validationResponse


    val emailObserver = ObservableField<String>()

    val input1Observer = ObservableField<String>()
    val input2Observer = ObservableField<String>()
    val input3Observer = ObservableField<String>()
    val input4Observer = ObservableField<String>()
    val input5Observer = ObservableField<String>()

    val passwordObserver = ObservableField<String>()
    val confirmPasswordObserver = ObservableField<String>()


    suspend fun sendEmail() {
        var email: String = ""

        emailObserver.get()?.let {
            email = it
        }

        if (email.trim().isEmpty())
            _validationResponse.postValue(R.string.enter_email)
        else if (!Constants.isValidString(email.trim()))
            _validationResponse.postValue(R.string.enter_valid_email)
        else {
            _response.postValue(NetworkResult.Loading())
            try {
                apiRepository.forgotPassword(email.trim()).let {
                    if (it.statusCode == 200) {
                        _response.postValue(NetworkResult.Success(it.data!!))
                    } else {
                        _response.postValue(NetworkResult.Validation(it.message!!))
                    }

                }
            } catch (e: IOException) {
                _response.postValue(NetworkResult.Error(e.message!!, null))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _response.postValue(
                        NetworkResult.Error(
                            "401",
                            null
                        )
                    )
                }else{
                    _response.postValue(NetworkResult.Error(e.message!!, null))
                }
            }
        }
    }

   suspend fun verifyOTP(id: String) {
        var input1: String = ""
        var input2: String = ""
        var input3: String = ""
        var input4: String = ""
        var input5: String = ""

        input1Observer.get()?.let {
            input1 = it
        }

        input2Observer.get()?.let {
            input2 = it
        }

        input3Observer.get()?.let {
            input3 = it
        }

        input4Observer.get()?.let {
            input4 = it
        }

        input5Observer.get()?.let {
            input5 = it
        }

        if (input1.isEmpty() && input2.isEmpty() && input3.isEmpty() && input4.isEmpty() && input5.isEmpty())
            _validationResponse.postValue(R.string.enter_otp_field)
        else if (id.isEmpty())
            _validationResponse.postValue(R.string.enter_id)
        else {
            _otpResponse.postValue(NetworkResult.Loading())
            try {
                apiRepository.verifyOTP(input1 + input2 + input3 + input4 + input5, id).let {
                    if (it.statusCode == 200) {
                        _otpResponse.postValue(NetworkResult.Success(it.data!!))
                    } else {
                        _otpResponse.postValue(NetworkResult.Validation(it.message!!))
                    }

                }
            } catch (e: IOException) {
                _otpResponse.postValue(NetworkResult.Error(e.message!!, null))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _otpResponse.postValue(
                        NetworkResult.Error(
                            "401",
                            null
                        )
                    )
                }else{
                    _otpResponse.postValue(NetworkResult.Error(e.message!!, null))

                }
            }
        }
    }

    suspend fun resetPassword(id: String)  {
        var password: String = ""
        var confirmPassword: String = ""

        passwordObserver.get()?.let {
            password = it
        }

        confirmPasswordObserver.get()?.let {
            confirmPassword = it
        }


        if (password.isEmpty())
            _validationResponse.postValue(R.string.enter_password_)
        else if (confirmPassword.isEmpty())
            _validationResponse.postValue(R.string.enter_confirm_password_)
        else if (password != confirmPassword)
            _validationResponse.postValue(R.string.password_match)
        else if (id.isEmpty())
            _validationResponse.postValue(R.string.enter_id)
        else {
            _resetPasswordResponse.postValue(NetworkResult.Loading())
            try {
                apiRepository.updatePassword(password, confirmPassword, id).let {
                    if (it.statusCode == 200) {
                        _resetPasswordResponse.postValue(NetworkResult.Success(it.data!!))
                    } else {
                        _resetPasswordResponse.postValue(NetworkResult.Validation(it.message!!))
                    }

                }
            } catch (e: IOException) {
                _resetPasswordResponse.postValue(NetworkResult.Error(e.message!!, null))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _resetPasswordResponse.postValue(
                        NetworkResult.Error(
                            "401",
                            null
                        )
                    )
                }else{
                    _resetPasswordResponse.postValue(NetworkResult.Error(e.message!!, null))

                }
            }
        }
    }


}