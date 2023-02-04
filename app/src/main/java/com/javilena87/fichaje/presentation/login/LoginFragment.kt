package com.javilena87.fichaje.presentation.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.javilena87.fichaje.R
import com.javilena87.fichaje.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    val loginViewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText = binding.username
        val usernameLayout = binding.usernameInputLayout
        val passwordEditText = binding.password
        val passwordLayout = binding.passwordInputLayout
        val loginButton = binding.login
        val deleteData = binding.deleteData
        val loadingProgressBar = binding.loading
        val rememberedUserTextView = binding.rememberedUser

        setViewsState(
            usernameLayout,
            passwordLayout,
            rememberedUserTextView,
            deleteData
        )

        initLoginData()

        onLoginResult(loadingProgressBar)

        setTextActions(usernameEditText, passwordEditText)

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }

        deleteData.setOnClickListener {
            loginViewModel.clearSharedData()
        }

    }

    private fun initLoginData() {
        loginViewModel.initResult()
        loginViewModel.initUserRemembered()
    }

    private fun setTextActions(
        usernameEditText: EditText,
        passwordEditText: EditText
    ) {
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
                closeKeyboard()
            }
            false
        }
    }

    private fun onLoginResult(loadingProgressBar: ProgressBar) {
        loginViewModel.loginResult.observe(requireActivity(),
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.let {
                    if (it) {
                        navigateToCheckFragment()
                    } else {
                        showLoginFailed(R.string.login_failed)
                    }
                    closeKeyboard()
                }
            })
    }

    private fun closeKeyboard() {
        binding.let {
            val imm =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.password.windowToken, 0)
        }
    }

    private fun setViewsState(
        userNameLayout: View,
        passwordLayout: View,
        rememberedUserTextView: TextView,
        deleteData: Button
    ) {
        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginFormState.userRemembered?.let {
                    rememberedUserTextView.text =
                        getString(R.string.welcome, it)
                }
                deleteData.isEnabled = !loginFormState.dataCleared
                loginFormState.userNameVisibility.let {
                    userNameLayout.visibility = it
                }
                loginFormState.passwordVisibility.let {
                    passwordLayout.visibility = it
                }
                loginFormState.userRememberedVisibility.let {
                    rememberedUserTextView.visibility = it
                }
            })
    }

    private fun navigateToCheckFragment() {
        val action =
            LoginFragmentDirections
                .actionLoginFragmentToResultFragment2()
        findNavController().navigate(action)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Snackbar.make(
            binding.root,
            errorString,
            BaseTransientBottomBar.LENGTH_LONG
        )
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_alpha
                )
            ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}