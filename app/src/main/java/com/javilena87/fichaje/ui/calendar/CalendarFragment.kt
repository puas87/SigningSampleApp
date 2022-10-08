package com.javilena87.fichaje.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.javilena87.fichaje.R
import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.databinding.CalendarFragmentBinding
import com.javilena87.fichaje.ui.calendar.adapter.DeleteItemCallback
import com.javilena87.fichaje.ui.calendar.adapter.HolidaysAdapter
import com.javilena87.fichaje.ui.calendar.adapter.RecyclerItemTouchHelperListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : Fragment(), RecyclerItemTouchHelperListener {

    private val calendarViewModel: CalendarViewModel by viewModels()

    private var _binding: CalendarFragmentBinding? = null

    private val binding get() = _binding!!

    private val holidaysAdapter = HolidaysAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datesRecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.datesRecycler.adapter = holidaysAdapter
        binding.datesRecycler.itemAnimator = DefaultItemAnimator()
        binding.datesRecycler.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        val itemTouchHelperCallback =
            DeleteItemCallback(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.datesRecycler)

        calendarViewModel.getAllHolidays()

        binding.buttonRangeDays.setOnClickListener {
            calendarViewModel.enableRangeClickCalendar()
        }
        binding.buttonOneDay.setOnClickListener {
            calendarViewModel.enableDayClickCalendar()
        }

        calendarViewModel.calendarOneDaySelectionState.observe(
            requireActivity(),
            Observer { calendarOneDaySelectionState ->
                if (calendarOneDaySelectionState.enabled) {
                    manageDatePickerOneDay()
                }
            })

        calendarViewModel.calendarRangeSelectionState.observe(
            requireActivity(),
            Observer { calendarRangeSelectionState ->
                if (calendarRangeSelectionState.enabled) {
                    manageDatePickerRange()
                }
            })

        calendarViewModel.calendarHolidaysListState.observe(
            requireActivity(),
            Observer { calendarHolidayItem ->
                calendarHolidayItem ?: return@Observer
                holidaysAdapter.submitList(calendarHolidayItem.holidaysList)
            }
        )

        calendarViewModel.calendarRemoveHolidayState.observe(
            requireActivity(),
            Observer { calendarHolidayItemToRemove ->
                calendarHolidayItemToRemove ?: return@Observer
                calendarHolidayItemToRemove.holidayItemRemoved?.let {
                    showSnackbar(
                        it
                    )
                }
            })

        calendarViewModel.calendarSelectionErrorState.observe(
            requireActivity(),
            Observer { calendarError ->
                if (calendarError) {
                    showErrorSnackbar()
                }
            })
    }

    private fun manageDatePickerOneDay() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.one_day_selection_title_text)
                .setCalendarConstraints(
                    calendarConstraints()
                )
                .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            calendarViewModel.addHolidaysSelection(selection)
        }

        datePicker.show(parentFragmentManager, "SINGLE_DAY_DIALOG")
    }

    private fun manageDatePickerRange() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.range_selection_title_text)
                .setCalendarConstraints(
                    calendarConstraints()
                )
                .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            calendarViewModel.addHolidaysSelection(
                selection.first,
                selection.second
            )
        }
        dateRangePicker.show(parentFragmentManager, "RANGE_DAY_DIALOG")
    }

    private fun calendarConstraints() = CalendarConstraints.Builder()
        .setStart(MaterialDatePicker.thisMonthInUtcMilliseconds())
        .setValidator(DateValidatorPointForward.now())
        .build()

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder?,
        direction: Int,
        position: Int
    ) {
        viewHolder?.let {
            calendarViewModel.deleteHolidayFromPosition(it.adapterPosition)
        }
    }

    private fun showSnackbar(itemToRemove: HolidayReg) {
        Snackbar.make(
            binding.root,
            getString(R.string.holidays_item_deleted),
            BaseTransientBottomBar.LENGTH_LONG
        ).setAction(getString(R.string.holidays_item_undone)) {
            calendarViewModel.addHolidaysSelection(itemToRemove)
        }
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_alpha
                )
            ).show()
    }

    private fun showErrorSnackbar() {
        Snackbar.make(
            binding.root,
            getString(R.string.holidays_item_duplicated),
            BaseTransientBottomBar.LENGTH_LONG
        ).setBackgroundTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.red_alpha
            )
        ).show()
    }

}