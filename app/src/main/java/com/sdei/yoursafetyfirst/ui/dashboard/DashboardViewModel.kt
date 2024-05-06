package com.app.yoursafetyfirst.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.DashboardResponse
import com.app.yoursafetyfirst.response.LanguageResponse
import com.app.yoursafetyfirst.response.TrafficListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(var apiRepository: ApiRepository) : ViewModel() {

    private val _trafficListResponse = MutableLiveData<NetworkResult<TrafficListResponse>>()
    val trafficListResponse: LiveData<NetworkResult<TrafficListResponse>> = _trafficListResponse

    private val _allContentResponse = MutableLiveData<NetworkResult<DashboardResponse>>()
    val allContentResponse: LiveData<NetworkResult<DashboardResponse>> = _allContentResponse

    private val _languageResponse = MutableLiveData<NetworkResult<LanguageResponse>>()
    val languageResponse: LiveData<NetworkResult<LanguageResponse>> = _languageResponse


    suspend fun trafficInfoList(token: String) {
        _trafficListResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.getTrafficInfoContent(token).let {
                if (it.statusCode == 200) {
                    _trafficListResponse.postValue(NetworkResult.Success(it.data!!))
                } else {
                    _trafficListResponse.postValue(NetworkResult.Validation(it.message!!))
                }

            }
        } catch (e: IOException) {
            _trafficListResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            _trafficListResponse.postValue(NetworkResult.Error(e.message!!, null))
        }
    }


    suspend fun getAllContent(token: String,pageNumber: Int) {
        _allContentResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.getAllContent(token,pageNumber.toString()).let {
                if (it.statusCode == 200) {
                    _allContentResponse.postValue(NetworkResult.Success(it.data!!))
                } else {
                    _allContentResponse.postValue(NetworkResult.Validation(it.message!!))
                }

            }
        } catch (e: IOException) {
            _allContentResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                _allContentResponse.postValue(
                    NetworkResult.Error(
                        "401",
                        null
                    )
                )
            } else {
                _allContentResponse.postValue(NetworkResult.Error(e.message!!, null))
            }
        }
    }


    suspend fun getLanguage(token: String) {
        _languageResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.getLanguage(token).let {
                if (it.statusCode == 200) {
                    _languageResponse.postValue(NetworkResult.Success(it.data!!))
                } else {
                    _languageResponse.postValue(NetworkResult.Validation(it.message!!))
                }

            }
        } catch (e: IOException) {
            _languageResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            _languageResponse.postValue(NetworkResult.Error(e.message!!, null))
        }
    }
}

