package com.javilena87.fichaje.presentation.login

import android.view.View

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val userRemembered: String? = null,
    val userRememberedVisibility: Int = View.INVISIBLE,
    val userNameVisibility: Int = View.VISIBLE,
    val passwordVisibility: Int = View.VISIBLE,
    val dataCleared: Boolean = true
)