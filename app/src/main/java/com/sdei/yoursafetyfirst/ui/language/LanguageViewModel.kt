package com.app.yoursafetyfirst.ui.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.LanguageResponse
import com.app.yoursafetyfirst.response.PrivacyPolicyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(var apiRepository: ApiRepository) : ViewModel() {

    private val _allPolicyResponse = MutableLiveData<NetworkResult<PrivacyPolicyResponse>>()
    val allPolicyResponse: LiveData<NetworkResult<PrivacyPolicyResponse>> = _allPolicyResponse

    private val _languageResponse = MutableLiveData<NetworkResult<LanguageResponse>>()
    val languageResponse: LiveData<NetworkResult<LanguageResponse>> = _languageResponse


    suspend fun getAllPolicy(authorization: String) {
        _allPolicyResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.getAllPolicy(authorization).let {
                if (it.statusCode == 200) {
                    _allPolicyResponse.postValue(NetworkResult.Success(it.data!!))
                } else {
                    _allPolicyResponse.postValue(NetworkResult.Validation(it.message!!))
                }

            }
        } catch (e: IOException) {
            _allPolicyResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                _allPolicyResponse.postValue(
                    NetworkResult.Error(
                        "401",
                        null
                    )
                )
            } else {
                _allPolicyResponse.postValue(NetworkResult.Error(e.message!!, null))

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