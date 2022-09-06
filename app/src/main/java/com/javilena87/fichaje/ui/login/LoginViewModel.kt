package com.javilena87.fichaje.ui.login

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.data.model.UserData
import com.javilena87.fichaje.data.prefs.FichajeSharedPrefs
import com.javilena87.fichaje.data.repository.DefaultFichajeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val fichajeRepository: DefaultFichajeRepository,
    private val fichajeSharedPreferences: FichajeSharedPrefs
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
            try {
                var result = getSuccessFromResult(
                    fichajeRepository.login(
                        userName = getUserName(entryUsername),
                        password = if (isPasswordDataChanged(
                                entryPassword
                            )
                        ) entryPassword else getPassword(
                            entryPassword
                        )
                    )
                )
                if (result) {
                    if (isUserNameDataChanged(entryUsername)) {
                        fichajeSharedPreferences.setUserName(
                            entryUsername
                        )
                    }
                    if (isPasswordDataChanged(entryPassword)) {
                        fichajeSharedPreferences.setPassword(
                            entryPassword
                        )
                    }
                } else if (!result && isUserRememebered()) {
                    _loginForm.value = LoginFormState(
                        userRemembered = getUserName(),
                        userRememberedVisibility = getUserRememberedVisibility(),
                        userNameVisibility = getUserNameVisibility(),
                        passwordVisibility = View.VISIBLE,
                        dataCleared = false
                    )
                }
                _loginResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _loginResult.value = false
                _loginForm.value = LoginFormState(
                    userRemembered = getUserName(),
                    userRememberedVisibility = getUserRememberedVisibility(),
                    userNameVisibility = getUserNameVisibility(),
                    passwordVisibility = View.VISIBLE,
                    dataCleared = false
                )
            }
        }
    }

    fun clearSharedData() {
        fichajeSharedPreferences.setUserData("", "")
        _loginForm.value = LoginFormState("")
    }

    private fun getSuccessFromResult(result: UserData): Boolean {
        return result.name.isNotEmpty()    }

    fun getUserRemembered() {
        if (isUserRememebered()) {
            _loginForm.value = LoginFormState(
                userRemembered = getUserName(),
                userRememberedVisibility = getUserRememberedVisibility(),
                userNameVisibility = getUserNameVisibility(),
                passwordVisibility = View.INVISIBLE,
                dataCleared = false
            )
        }
    }

    private fun getUserNameVisibility(): Int {
        return if (isUserRememebered()) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private fun getUserRememberedVisibility(): Int {
        return if (isUserRememebered()) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun isUserNameDataChanged(username: String): Boolean {
        val usernameRemembered = getUserName()
        return username.isNotBlank()
                && usernameRemembered != username
    }

    private fun isPasswordDataChanged(password: String): Boolean {
        val passworRemembered = getPassword()
        return password.isNotBlank()
                && passworRemembered != password
    }

    fun isUserRememebered(): Boolean {
        return getUserName().isNotBlank()
    }

    private fun getUserName(fallback: String = ""): String {
        return fichajeSharedPreferences.getUsername(fallback)
            .ifEmpty { fallback }
    }

    private fun getPassword(fallback: String = ""): String {
        return fichajeSharedPreferences.getPassword(fallback)
            .ifEmpty { fallback }
    }

}