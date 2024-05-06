package com.app.yoursafetyfirst.ui.prevoiusdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.PreviousDataResponse
import com.app.yoursafetyfirst.response.SinglePreviousData
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PreviousDataViewModel @Inject constructor(var apiRepository: ApiRepository) : ViewModel() {

    private val _previousListResponse = MutableLiveData<NetworkResult<PreviousDataResponse>>()
    val previousListResponse: LiveData<NetworkResult<PreviousDataResponse>> = _previousListResponse

    private val _singleDataResponse = MutableLiveData<NetworkResult<SinglePreviousData>>()
    val singleDataResponse: LiveData<NetworkResult<SinglePreviousData>> = _singleDataResponse


    suspend fun getPreviousList(authorization: String,id: String, page: String, search: String)  {
        _previousListResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.getAllDiagnostic(
                authorization,id, page, search
            ).let {
                if (it.statusCode == 200) {
                    _previousListResponse.postValue(
                        NetworkResult.Success(it.data!!)
                    )
                } else {
                    _previousListResponse.postValue(NetworkResult.Validation(it.message!!))
                }

            }
        } catch (e: IOException) {
            _previousListResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                _previousListResponse.postValue(
                    NetworkResult.Error(
                        "401",
                        null
                    )
                )
            }else {
                _previousListResponse.postValue(NetworkResult.Error(e.message!!, null))
            }
        }
    }

    suspend fun getSingleDiagnostic(authorization: String,id: String, id1: String) {
        _singleDataResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.getSingleDiagnostic(authorization,id,id1).let {
                if (it.statusCode == 200) {
                    _singleDataResponse.postValue(NetworkResult.Success(it.data!!))
                } else {
                    _singleDataResponse.postValue(NetworkResult.Validation(it.message!!))
                }

            }
        } catch (e: IOException) {
            _singleDataResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                _singleDataResponse.postValue(
                    NetworkResult.Error(
                        "401",
                        null
                    )
                )
            }else {
                _singleDataResponse.postValue(NetworkResult.Error(e.message!!, null))
            }
        }
    }

}