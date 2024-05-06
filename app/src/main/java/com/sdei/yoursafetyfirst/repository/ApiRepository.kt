package com.app.yoursafetyfirst.repository

import com.app.yoursafetyfirst.api.ApiServices
import com.app.yoursafetyfirst.request.AddObservationRequest
import com.app.yoursafetyfirst.request.ChangePasswordRequest
import com.app.yoursafetyfirst.request.DeclarationRequest
import com.app.yoursafetyfirst.request.FCMRequest
import com.app.yoursafetyfirst.request.ObservationRequest
import com.app.yoursafetyfirst.request.RegisterRequest
import com.app.yoursafetyfirst.request.SaveQuestionerRequest
import com.app.yoursafetyfirst.request.SignInRequest
import com.app.yoursafetyfirst.request.UpdateProfileRequest
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ApiRepository @Inject constructor(
    private val apiServices: ApiServices,
) {
    suspend fun registerUser(register: RegisterRequest) = apiServices.userRegister(register)

    suspend fun signInUser(signInRequest: SignInRequest) = apiServices.signInUser(signInRequest)
    suspend fun deleteUserAccount(token: String, id: String) =
        apiServices.deleteUserAccount(token, id)

    suspend fun forgotPassword(email: String) = apiServices.forgotPassword(email)

    suspend fun verifyOTP(otp: String, id: String) = apiServices.verifyOtp(otp, id)

    suspend fun updatePassword(password: String, confirm_password: String, id: String) =
        apiServices.updatePassword(password, confirm_password, id)

    suspend fun getProfile(authorization: String,id: String) = apiServices.getProfile(authorization,id)

    suspend fun updateProfile(authorization: String, requestParams: UpdateProfileRequest) =
        apiServices.updateProfile(authorization, requestParams)

    suspend fun addDeclaration(authorization: String, requestParams: DeclarationRequest) =
        apiServices.addDeclaration(authorization, requestParams)

    suspend fun logout(authorization: String) = apiServices.logout(authorization)

    suspend fun changePassword(authorization: String, requestParams: ChangePasswordRequest) =
        apiServices.changePassword(authorization, requestParams)

    suspend fun addObservation(authorization: String, requestParams: AddObservationRequest) =
        apiServices.addObservation(authorization, requestParams)

    suspend fun getNotificationList(authorization: String, page: String) =
        apiServices.getAllNotification(authorization, page, "20")

    suspend fun getNotification(authorization: String, id: String) =
        apiServices.getNotification(authorization, id)


    suspend fun addQuestionnaireFeedback(requestParams: SaveQuestionerRequest) =
        apiServices.addQuestionnaireFeedback(requestParams)

    suspend fun addReflexaction(authorization: String, declarationId: String, time: String) =
        apiServices.addReflexaction(authorization, declarationId, time)

    suspend fun getTrafficInfoContent(authorization: String) =
        apiServices.getTrafficInfoContent(authorization)

    suspend fun getAllContent(authorization: String, page: String) =
        apiServices.getAllContent(authorization, page, "10")

    suspend fun getAllPolicy(authorization: String) = apiServices.getAllPolicy(authorization)

    suspend fun getLanguage(authorization: String) = apiServices.getLanguage(authorization)

    suspend fun ringLogin(authorization: String, email: String, password: String) =
        apiServices.ringLogin(authorization, email, password)


    suspend fun getReflex(authorization: String) = apiServices.getReflex(authorization)

    suspend fun getDiagnostic(authorization: String, id: String) =
        apiServices.getDiagnostic(authorization, id)

    suspend fun updateFCM(fcmRequest: FCMRequest) = apiServices.updateFCM(fcmRequest)

    suspend fun getAllDiagnostic(authorization: String, id: String, page: String, search: String) =
        apiServices.getAllDiagnostic(authorization, id, page, "15", search)

    suspend fun getSingleDiagnostic(authorization: String,id: String, id1: String) =
        apiServices.getSingleDiagnostic(authorization,id, id1)

    suspend fun updateObservation(authorization: String, observationRequest: ObservationRequest) =
        apiServices.updateObservation(authorization, observationRequest)


}