package com.app.yoursafetyfirst.ui.profile

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.repository.ApiRepository
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.request.ChangePasswordRequest
import com.app.yoursafetyfirst.request.UpdateProfileRequest
import com.app.yoursafetyfirst.response.BaseMessage
import com.app.yoursafetyfirst.response.DeleteAccountResponse
import com.app.yoursafetyfirst.response.ProfileResponse
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.Constants.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    var apiRepository: ApiRepository, var application: Application
) : ViewModel() {

    private val _response = MutableLiveData<NetworkResult<ArrayList<ProfileResponse>>>()
    val profileResponse: LiveData<NetworkResult<ArrayList<ProfileResponse>>> = _response

    private val _commonResponse = MutableLiveData<NetworkResult<BaseMessage>>()
    val commonResponse: LiveData<NetworkResult<BaseMessage>> = _commonResponse

    private val _logoutResponse = MutableLiveData<NetworkResult<String>>()
    val logoutResponse: LiveData<NetworkResult<String>> = _logoutResponse

    private val _deleteAccountResponse = MutableLiveData<NetworkResult<DeleteAccountResponse>>()
    val deleteAccountResponse: LiveData<NetworkResult<DeleteAccountResponse>> =
        _deleteAccountResponse

    private val _changePasswordResponse = MutableLiveData<NetworkResult<String>>()
    val changePasswordResponse: LiveData<NetworkResult<String>> = _changePasswordResponse

    private val _validationResponse = MutableLiveData<Int>()
    val validationResponse: LiveData<Int> = _validationResponse


    val emailObserver = ObservableField<String>()
    val genderObserver = ObservableField<String>()
    val phoneObserver = ObservableField<String>()
    val nameObserver = ObservableField<String>()
    val languageObserver = ObservableField<String>()

    val currentPasswordObserver = ObservableField<String>()
    val newPasswordObserver = ObservableField<String>()
    val confirmPasswordObserver = ObservableField<String>()

    val yearObserver = ObservableField<String>()
    val heightObserver = ObservableField<String>()
    val weightObserver = ObservableField<String>()
    val heartObserver = ObservableField<String>()
    val companyCodeObserver = ObservableField<String>()
    val ringIdObserver = ObservableField<String>()
    val ringObserver = ObservableField<Boolean>()

    suspend fun getProfile(authorization: String, id: String) {
        //_response.postValue(NetworkResult.Loading())
        try {
            apiRepository.getProfile(authorization, id).let {
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

    suspend fun updateProfile(authorization: String, id: String) {
        var email: String = ""
        var username: String = ""
        var phoneNumber: String = ""
        var gender: String = ""
        var height: Int = 0
        var weight: Int = 0
        var heart: Int = 0
        var companyCode: String = ""
        var ringId: String = ""
        var ringUse: Boolean = false
        var year: String = ""
        var language: String = ""


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

        emailObserver.get()?.let {
            email = it
        }


        nameObserver.get()?.let {
            username = it
        }

        phoneObserver.get()?.let {
            phoneNumber = it
        }

        genderObserver.get()?.let {
            gender = it
        }

        languageObserver.get()?.let {
            language = it
        }


        if (username.isEmpty())
            _validationResponse.postValue(R.string.enter_username_)
        else if (email.isEmpty())
            _validationResponse.postValue(R.string.enter_email)
        else if (!Constants.isValidString(email))
            _validationResponse.postValue(R.string.enter_valid_email)
        /*   else if (phoneNumber.isEmpty())
               _validationResponse.postValue(R.string.enter_phonenumber_)*/
        else if (phoneNumber.isNotEmpty() && phoneNumber.length < 11)
            _validationResponse.postValue(R.string.valid_number)
        else if (gender.equals("Select gender", true))
            _validationResponse.postValue(R.string.enter_gender)
        /*    else if (year.isEmpty())
                _validationResponse.postValue(R.string.enter_birth)*/
        /* else if (height == 0)
             _validationResponse.postValue(R.string.enter_height_)*/
        else if (height > 0 && height < 54 || height > 300)
            _validationResponse.postValue(R.string.height_min)
        /* else if (weight == 0)
             _validationResponse.postValue(R.string.enter_weight_)*/
        else if (weight > 0 && weight < 40 || weight > 360)
            _validationResponse.postValue(R.string.weight_min)
        /*   else if (heart == 0)
               _validationResponse.postValue(R.string.enter_heart)*/
        else if (heart > 0 && heart < 40 || heart > 440)
            _validationResponse.postValue(R.string.heart_min)
        /* else if (companyCode.isEmpty())
             _validationResponse.postValue(R.string.enter_companycode)*/
        else if (companyCode.isNotEmpty() && companyCode.length < 5)
            _validationResponse.postValue(R.string.invalid_companycode)
        else if (ringUse && ringId.isEmpty())
            _validationResponse.postValue(R.string.please_enter_ring_id)
        else {
            _commonResponse.postValue(NetworkResult.Loading())
            try {
                apiRepository.updateProfile(
                    authorization,
                    UpdateProfileRequest(
                        id,
                        username,
                        email,
                        phoneNumber,
                        gender,
                        "active",
                        "Android",
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
                        _commonResponse.postValue(NetworkResult.Success(it.message!!))
                    } else {
                        it.message?.let { _commonResponse.postValue(NetworkResult.Validation(it)) }
                    }

                }
            } catch (e: IOException) {
                _commonResponse.postValue(NetworkResult.Error(e.message!!, null))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _commonResponse.postValue(
                        NetworkResult.Error(
                            "401",
                            null
                        )
                    )
                } else {
                    _commonResponse.postValue(NetworkResult.Error(e.message!!, null))
                }
            }
        }
    }

    suspend fun logout(token: String) {
        _logoutResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.logout(token).let {
                if (it.statusCode == 200) {
                    _logoutResponse.postValue(NetworkResult.Success("Success!"))
                } else {
                    it.message?.let {
                        _logoutResponse.postValue(NetworkResult.Validation(it))
                    }
                }

            }
        } catch (e: IOException) {
            _logoutResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            _logoutResponse.postValue(NetworkResult.Error(e.message!!, null))
        }
    }

    suspend fun changePassword(token: String) {

        var oldPassword: String = ""
        var newPassword: String = ""
        var confirmPassword: String = ""

        currentPasswordObserver.get()?.let {
            oldPassword = it
        }

        confirmPasswordObserver.get()?.let {
            confirmPassword = it
        }

        newPasswordObserver.get()?.let {
            newPassword = it
        }

        if (oldPassword.trim().isEmpty())
            _validationResponse.postValue(R.string.old_password)
        else if (newPassword.trim().isEmpty())
            _validationResponse.postValue(R.string.enter_new_password_)
        else if (!isValidPassword(newPassword.trim()))
            _validationResponse.postValue(R.string.weak_password)
        else if (confirmPassword.trim().isEmpty())
            _validationResponse.postValue(R.string.enter_confirm_password_)
        else if (newPassword.trim() != confirmPassword.trim())
            _validationResponse.postValue(R.string.password_match)
        else if (oldPassword.trim() == newPassword.trim())
            _validationResponse.postValue(R.string.same_password)
        else {
            _changePasswordResponse.postValue(NetworkResult.Loading())
            try {
                apiRepository.changePassword(
                    token,
                    ChangePasswordRequest(oldPassword, newPassword, confirmPassword)
                ).let {
                    Log.e("aaa", "    $it")
                    if (it.statusCode == 200) {
                        _changePasswordResponse.postValue(NetworkResult.Success(""))
                    } else {
                        it.message?.let {
                            _changePasswordResponse.postValue(NetworkResult.Validation(it))
                        }
                    }

                }
            } catch (e: IOException) {
                _changePasswordResponse.postValue(NetworkResult.Error(e.message!!, null))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _changePasswordResponse.postValue(
                        NetworkResult.Error(
                            "401",
                            null
                        )
                    )
                } else {
                    _changePasswordResponse.postValue(NetworkResult.Error(e.message!!, null))
                }
            }
        }
    }


    suspend fun deleteAccount(token: String, id: String) {
        _deleteAccountResponse.postValue(NetworkResult.Loading())
        try {
            apiRepository.deleteUserAccount(token, id).let {
                if (it.statusCode == 200) {
                    _deleteAccountResponse.postValue(NetworkResult.Success(it.data!!))
                } else {
                    it.message?.let {
                        _deleteAccountResponse.postValue(NetworkResult.Validation(it))
                    }
                }

            }
        } catch (e: IOException) {
            _deleteAccountResponse.postValue(NetworkResult.Error(e.message!!, null))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                _deleteAccountResponse.postValue(
                    NetworkResult.Error(
                        "401",
                        null
                    )
                )
            } else {
                _deleteAccountResponse.postValue(NetworkResult.Error(e.message!!, null))
            }
        }
    }
}
