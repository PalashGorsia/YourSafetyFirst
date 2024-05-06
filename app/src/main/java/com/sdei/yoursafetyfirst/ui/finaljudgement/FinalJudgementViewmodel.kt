package com.app.yoursafetyfirst.ui.finaljudgement

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.OverallJudgementResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class FinalJudgementViewmodel @Inject constructor(
    var apiRepository: ApiRepository,
    var application: Application
) : ViewModel() {

    private val _response = MutableLiveData<NetworkResult<OverallJudgementResponse>>()
    val judgementResponse: LiveData<NetworkResult<OverallJudgementResponse>> = _response


    suspend fun getDiagnostic(authorization: String,id: String) {
        _response.postValue(NetworkResult.Loading())
        try {
            apiRepository.getDiagnostic(authorization,id).let {
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
            }else {
                _response.postValue(NetworkResult.Error(e.message!!, null))
            }
        }
    }
}
