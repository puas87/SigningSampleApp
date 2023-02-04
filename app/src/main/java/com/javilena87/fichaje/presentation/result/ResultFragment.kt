package com.javilena87.fichaje.presentation.result

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.javilena87.fichaje.R
import com.javilena87.fichaje.databinding.ResultFragmentBinding
import com.javilena87.fichaje.receiver.FichajeReceiver
import com.javilena87.fichaje.utils.initAlarm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private val resultViewModel: ResultViewModel by viewModels()

    private var _binding: ResultFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResultFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageEntrada.setOnClickListener(this)
        binding.imageSalida.setOnClickListener(this)
        binding.textEntrada.setOnClickListener(this)
        binding.textSalida.setOnClickListener(this)

        binding.alarmSwitch.isChecked = resultViewModel.checkAlarm()
        binding.alarmSwitch.setOnCheckedChangeListener(this)

        onEnterRequestResult()
        onExitRequestResult()
        onSwitchState()
        onAlarmState()
    }

    private fun onAlarmState() {
        resultViewModel.resultAlarmState.observe(
            viewLifecycleOwner,
            Observer { alarmState ->
                alarmState ?: return@Observer
                alarmState.let {
                    activity?.let { context ->
                        initAlarm(
                            context,
                            alarmState.nextAlarmDay
                        )
                    }
                }
            })
    }

    private fun onSwitchState() {
        resultViewModel.resultAlarmOnOffState.observe(viewLifecycleOwner,
            Observer { alarmState ->
                alarmState ?: return@Observer
                alarmState.let {
                    if (it) {
                        binding.alarmSwitch.text =
                            getText(R.string.check_alarm_enabled)
                        resultViewModel.enableAlarm()
                    } else {
                        binding.alarmSwitch.text =
                            getText(R.string.check_alarm_disabled)
                        cancelAlarm()
                    }
                    binding.alarmSwitch.isChecked = it
                }
            }
        )
    }

    private fun onEnterRequestResult() {
        resultViewModel.resultEnterState.observe(viewLifecycleOwner,
            Observer { requestResult ->
                requestResult ?: return@Observer
                createAndShowSnackBar(requestResult.requestOk, true)
            })
    }

    private fun onExitRequestResult() {
        resultViewModel.resultExitState.observe(viewLifecycleOwner,
            Observer { requestResult ->
                requestResult ?: return@Observer
                createAndShowSnackBar(requestResult.requestOk, false)
            })
    }

    private fun createAndShowSnackBar(
        success: Boolean = true,
        enter: Boolean = true
    ) {
        Snackbar.make(
            binding.root,
            getSnackBarText(success, enter),
            LENGTH_LONG
        )
            .setAnimationMode(ANIMATION_MODE_SLIDE)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    getSnackBarBackground(success)
                )
            ).show()
    }

    private fun getSnackBarBackground(success: Boolean) =
        if (success) R.color.teal_700 else R.color.red_alpha

    private fun getSnackBarText(success: Boolean, enter: Boolean) =
        if (success && enter)
            getString(R.string.enter_ok)
        else if (success)
            getString(R.string.exit_ok)
        else if (enter)
            getString(R.string.enter_fail)
        else
            getString(R.string.exit_fail)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageEntrada, R.id.textEntrada -> {
                resultViewModel.enter()
            }
            R.id.imageSalida, R.id.textSalida -> {
                resultViewModel.exit()
            }
        }
    }

    private fun cancelAlarm() {
        val alarmMgr: AlarmManager =
            activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val enterAlarmIntent: PendingIntent =
            Intent(activity, FichajeReceiver::class.java).let { intent ->
                intent.action = "SCHEDULED_ACTION"
                PendingIntent.getBroadcast(
                    activity,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val exitAlarmIntent: PendingIntent =
            Intent(context, FichajeReceiver::class.java).let { intent ->
                intent.action = "DELAYED_ACTION"
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        alarmMgr.cancel(enterAlarmIntent)
        alarmMgr.cancel(exitAlarmIntent)
    }

    override fun onCheckedChanged(
        buttonView: CompoundButton?,
        isChecked: Boolean
    ) {
        resultViewModel.setAlarm(isChecked)
    }

}