package com.app.yoursafetyfirst.ui.camerapulserate

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.request.AddObservationRequest
import com.app.yoursafetyfirst.response.ObservationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(
    var apiRepository: ApiRepository,
    var application: Application
) : ViewModel() {

    private val _response = MutableLiveData<NetworkResult<ObservationResponse>>()
    val observationResponse: LiveData<NetworkResult<ObservationResponse>> = _response


    suspend fun addObservation(
        authorization: String,
        id: String?,
        count: String,
        type: String,
        steps: String,
        ringToken: String,
        mesurmentArray: ArrayList<String>,
        timeArray: ArrayList<String>
    ) {
        viewModelScope.launch {
            _response.postValue(NetworkResult.Loading())
            try {
                apiRepository.addObservation(
                    authorization,
                    AddObservationRequest(
                        id,
                        type,
                        count,
                        "0",
                        steps,
                        ringToken,
                        mesurmentArray,
                        timeArray
                    )
                ).let {
                    if (it.statusCode == 200) {
                        _response.postValue(NetworkResult.Success(it.data!!))
                    } else {
                        _response.postValue(NetworkResult.Validation(it.message))
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
                } else {
                    _response.postValue(NetworkResult.Error(e.message!!, null))
                }
            }
        }

    }
}