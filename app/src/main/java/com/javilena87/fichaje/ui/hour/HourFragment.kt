package com.javilena87.fichaje.ui.hour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.javilena87.fichaje.databinding.HourFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HourFragment : Fragment() {

    private val hourViewModel: HourViewModel by viewModels()

    private var _binding: HourFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = HourFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.hourEntryButton.setOnClickListener {
            hourViewModel.setAlarmTimer(binding.alarmEntryPicker.hour, binding.alarmEntryPicker.minute, true)
            it.isEnabled = false
        }

        binding.hourExitButton.setOnClickListener {
            hourViewModel.setAlarmTimer(binding.alarmExitPicker.hour, binding.alarmExitPicker.minute, false)
            it.isEnabled = false
        }

        binding.alarmEntryPicker.setIs24HourView(true)
        binding.alarmExitPicker.setIs24HourView(true)

        initTimer()
    }

    private fun initTimer() {
        hourViewModel.initEntryTimer()
        hourViewModel.initExitTimer()
        hourViewModel.resultEntryTimerState.observe(viewLifecycleOwner, Observer { timerState ->
            timerState ?: return@Observer
            timerState.let {
                binding.let {
                    binding.alarmEntryPicker.hour = timerState.hour
                    binding.alarmEntryPicker.minute = timerState.minute
                }
            }
        })

        hourViewModel.resultExitTimerState.observe(viewLifecycleOwner, Observer { timerState ->
            timerState ?: return@Observer
            timerState.let {
                binding.let {
                    binding.alarmExitPicker.hour = timerState.hour
                    binding.alarmExitPicker.minute = timerState.minute
                }
            }
        })
        hourViewModel.resultEntryTimerButtonState.observe(viewLifecycleOwner, Observer { buttonState ->
            buttonState ?: return@Observer
            buttonState.let {
                binding.hourEntryButton.isEnabled = !it
            }
        })

        hourViewModel.resultExitTimerButtonState.observe(viewLifecycleOwner, Observer { buttonState ->
            buttonState ?: return@Observer
            buttonState.let {
                binding.hourExitButton.isEnabled = !it
            }
        })
        binding.alarmEntryPicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            hourViewModel.checkNewEntryAlarm(hourOfDay, minute)
        }

        binding.alarmExitPicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            hourViewModel.checkNewExitAlarm(hourOfDay, minute)
        }
    }

}