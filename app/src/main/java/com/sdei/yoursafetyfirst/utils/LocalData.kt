package com.app.yoursafetyfirst.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class LocalData(private val context: Context) {

    // Create the dataStore and give it a name same as user_pref
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val SAVECREDENTIALS = booleanPreferencesKey("savecredentials")
        val NAME = stringPreferencesKey("name")
        val DRIVER_ID = stringPreferencesKey("driver_id")
        val TOKEN = stringPreferencesKey("token")
        val LANGUAGE = stringPreferencesKey("language")
        val RING_TOKEN = stringPreferencesKey("ring_token")
        val FIREBASE_TOKEN = stringPreferencesKey("firebase_token")
        val PRIVACY_POLICY = stringPreferencesKey("privacy_policy")
        val RING_USE = stringPreferencesKey("ring_use")
        val CURRENT_DATE = stringPreferencesKey("current_date")
        val TOTAL_STEP_COUNT = intPreferencesKey("total_step_count")
        val TODAY_STEP_COUNT = intPreferencesKey("today_step_count")

        /*ring credentials*/
        val RING_CREDENTIALS = booleanPreferencesKey("ring_credentials")
        val RING_EMAIL = stringPreferencesKey("ring_email")
        val RING_PASSWORD = stringPreferencesKey("ring_password")

    }


    // Store user data
    suspend fun storeRingCredentials(checked: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[RING_CREDENTIALS] = checked
        }

    }

    suspend fun storeRingEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[RING_EMAIL] = email
        }

    }

    suspend fun storeRingPassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[RING_PASSWORD] = password
        }

    }


    suspend fun storeUserInfo(email: String, name: String, driverID: String, token: String, ringUse: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL] = email
            preferences[NAME] = name
            preferences[DRIVER_ID] = driverID
            preferences[TOKEN] = token
            preferences[RING_USE] = ringUse
        }

    }

    suspend fun storeSelectedLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }

    }

    suspend fun storeAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }

    }

    suspend fun saveCredentials(checked: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SAVECREDENTIALS] = checked
        }

    }

    suspend fun storePassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[PASSWORD] = password
        }

    }


    suspend fun storeTotalStepCount(step: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_STEP_COUNT] = step
        }

    }

    suspend fun storeTodayStepCount(step: Int) {
        context.dataStore.edit { preferences ->
            preferences[TODAY_STEP_COUNT] = step
        }

    }

    suspend fun storeCurrentDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_DATE] = date
        }

    }

    suspend fun storePrivacyPolicy(privacy: String) {
        context.dataStore.edit { preferences ->
            preferences[PRIVACY_POLICY] = privacy
        }

    }

    suspend fun storeRingToken(ring_token: String) {
        context.dataStore.edit { preferences ->
            preferences[RING_TOKEN] = ring_token
        }

    }

    suspend fun storeFireBaseToken(firebaseToken: String) {
        context.dataStore.edit { preferences ->
            preferences[FIREBASE_TOKEN] = firebaseToken
        }
    }


    suspend fun clearDataStore() {
        context.dataStore.edit {
            //it.remove(EMAIL)
            it.remove(NAME)
            it.remove(DRIVER_ID)
            it.remove(TOKEN)
            it.remove(RING_TOKEN)
            it.remove(RING_USE)
            it.remove(FIREBASE_TOKEN)
        }
    }

    suspend fun clearRingToken() {
        context.dataStore.edit { it.remove(RING_TOKEN) }
    }

    suspend fun clearSaveCredentials() {
        context.dataStore.edit { it.remove(SAVECREDENTIALS) }
    }

    suspend fun clearRingCredentials() {
        context.dataStore.edit { it.remove(RING_CREDENTIALS) }
    }


    suspend fun clearCurrentDate() {
        context.dataStore.edit { it.remove(CURRENT_DATE) }
    }

    suspend fun clearFirebaseToken() {
        context.dataStore.edit { it.remove(FIREBASE_TOKEN) }
    }

    suspend fun clearLanguage() {
        context.dataStore.edit { it.remove(LANGUAGE) }
    }

    val todayStepCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TODAY_STEP_COUNT] ?: 0
    }

    val totalStepCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TOTAL_STEP_COUNT] ?: 0
    }

    val getSavedCredentials: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SAVECREDENTIALS] ?: false
    }

    val currentDate: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENT_DATE] ?: ""
    }

    val driverID: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DRIVER_ID] ?: ""
    }

    val privacyPolicy: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PRIVACY_POLICY] ?: ""
    }

    val name: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[NAME] ?: ""
    }

    val password: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PASSWORD] ?: ""
    }

    val email: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EMAIL] ?: ""
    }

    val token: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TOKEN] ?: ""
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: ""
    }

    val ringToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[RING_TOKEN] ?: ""
    }

    val firebaseToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FIREBASE_TOKEN] ?: ""
    }

    val ring_use: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[RING_USE] ?: ""
    }

    val getRingCredentials : Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[RING_CREDENTIALS] ?: false
    }

    val ringEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[RING_EMAIL] ?: ""
    }

    val ringPassword: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[RING_PASSWORD] ?: ""
    }

}