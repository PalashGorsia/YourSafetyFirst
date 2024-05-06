package com.app.yoursafetyfirst.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.request.FCMRequest
import com.app.yoursafetyfirst.response.CommonResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    var apiRepository: ApiRepository,
    var application: Application
) : ViewModel() {

    private val _response = MutableLiveData<NetworkResult<CommonResponse>>()
    val updateTokenResponse: LiveData<NetworkResult<CommonResponse>> = _response


    fun updateToken(fcmRequest:  FCMRequest) = viewModelScope.launch {

        _response.postValue(NetworkResult.Loading())
        try {
            apiRepository.updateFCM(fcmRequest).let {
                if (it.statusCode == 200) {
                    //_response.postValue(NetworkResult.Success(it.data!!))
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