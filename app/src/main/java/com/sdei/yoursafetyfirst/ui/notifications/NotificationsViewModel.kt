package com.app.yoursafetyfirst.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.response.Notification
import com.app.yoursafetyfirst.response.NotificationList
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(var apiRepository: ApiRepository) : ViewModel() {

    private val _response = MutableLiveData<NetworkResult<NotificationList>>()
    val notificationListResponse: LiveData<NetworkResult<NotificationList>> = _response


    private val _notificationDetailResponse =
        MutableLiveData<NetworkResult<ArrayList<Notification>>>()
    val notificationDetailResponse: LiveData<NetworkResult<ArrayList<Notification>>> =
        _notificationDetailResponse


    suspend fun getNotificationList(authorization: String, page: String) {
        _response.postValue(NetworkResult.Loading())
        try {
            apiRepository.getNotificationList(authorization, page).let {
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
            } else {
                _response.postValue(NetworkResult.Error(e.message!!, null))

            }
        }
    }

    suspend fun getNotification(authorization: String, id: String) {
        _notificationDetailResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.getNotification(authorization, id).let {
                if (it.statusCode == 200) {
                    _notificationDetailResponse.postValue(NetworkResult.Success(it.data!!))
                } else {
                    _notificationDetailResponse.postValue(NetworkResult.Validation(it.message!!))
                }

            }
        } catch (e: IOException) {
            _notificationDetailResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                _notificationDetailResponse.postValue(
                    NetworkResult.Error(
                        "401",
                        null
                    )
                )
            } else {
                _notificationDetailResponse.postValue(NetworkResult.Error(e.message!!, null))
            }
        }
    }

}