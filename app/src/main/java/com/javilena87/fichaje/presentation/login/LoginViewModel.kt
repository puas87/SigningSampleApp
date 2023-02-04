package com.javilena87.fichaje.presentation.login

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getLogin: DoLoginUseCase,
    private val clearData: ClearDataUseCase,
    private val getUserData: GetUserDataUseCase
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<Boolean?>()
    val loginResult: LiveData<Boolean?> = _loginResult

    fun initResult() {
        _loginResult.value = null
    }

    fun login(username: String, password: String) {
        launchLoginRemote(username, password)
    }

    private fun launchLoginRemote(
        entryUsername: String,
        entryPassword: String
    ) {
        viewModelScope.launch {
            getLogin(entryUsername, entryPassword).apply {
                if (!this.requestOk) {
                    _loginForm.value = LoginFormState(
                        userRemembered = this.userName,
                        userRememberedVisibility = getUserRememberedVisibility(this.userRemembered),
                        userNameVisibility = getUserNameVisibility(this.userRemembered),
                        passwordVisibility = View.VISIBLE,
                        dataCleared = false
                    )
                }
                _loginResult.value = this.requestOk
            }
        }
    }

    fun clearSharedData() {
        clearData()
        _loginForm.value = LoginFormState("")
    }

    fun initUserRemembered() {
        val userData = getUserData()
        if (userData.userRememebered) {
            _loginForm.value = LoginFormState(
                userRemembered = userData.userName,
                userRememberedVisibility = getUserRememberedVisibility(true),
                userNameVisibility = getUserNameVisibility(true),
                passwordVisibility = View.INVISIBLE,
                dataCleared = false
            )
        }
    }

    private fun getUserNameVisibility(userRemembered: Boolean): Int {
        return if (userRemembered) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private fun getUserRememberedVisibility(userRemembered: Boolean): Int {
        return if (userRemembered) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

}