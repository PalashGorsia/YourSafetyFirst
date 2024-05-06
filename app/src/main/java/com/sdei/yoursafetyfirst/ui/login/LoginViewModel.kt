package com.app.yoursafetyfirst.ui.login

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.request.SignInRequest
import com.app.yoursafetyfirst.response.RegisterResponse
import com.app.yoursafetyfirst.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    var apiRepository: ApiRepository,
    var application: Application
) : ViewModel() {


    private val _response = MutableLiveData<NetworkResult<RegisterResponse>>()
    val signInResponse: LiveData<NetworkResult<RegisterResponse>> = _response

    val emailObserver = ObservableField<String>()
    val passwordObserver = ObservableField<String>()

    private val _validationResponse = MutableLiveData<Int>()
    val validationResponse: LiveData<Int> = _validationResponse


    suspend fun login(language: String?, uniqueId: String, fcmToken: String)  {

        var email: String = ""
        var password: String = ""


        emailObserver.get()?.let {
            email = it
        }

        passwordObserver.get()?.let {
            password = it
        }


        if (email.trim().isEmpty()) {
            _validationResponse.postValue(R.string.enter_email)
        } else if (!Constants.isValidString(email.trim())) {
            _validationResponse.postValue(R.string.enter_valid_email)
        } else if (password.trim().isEmpty()) {
            _validationResponse.postValue(R.string.enter_password_)
        } else {
            _response.postValue(NetworkResult.Loading())
            try {
                apiRepository.signInUser(
                    SignInRequest(
                        email.trim(),
                        password.trim(),
                        uniqueId,
                        fcmToken,
                        "DRIVER", language,"Android"
                    )
                ).let {
                    if (it.statusCode == 200) {
                        _response.postValue(NetworkResult.Success(it.data!!))
                    } else {
                        _response.postValue(NetworkResult.Validation(it.message!!))
                    }

                }
            } catch (e: IOException) {
                _response.postValue(NetworkResult.Error(e.message!!, null))
            } catch (e: HttpException) {
                _response.postValue(NetworkResult.Error(e.message!!, null))
            }
        }
    }
}