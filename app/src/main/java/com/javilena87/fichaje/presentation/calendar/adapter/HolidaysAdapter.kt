package com.javilena87.fichaje.presentation.calendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.databinding.ListItemHolidayRegBinding
import com.javilena87.fichaje.domain.model.HolidayDataItem

class HolidaysAdapter : ListAdapter<HolidayDataItem, RecyclerView.ViewHolder>(HolidayDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return HolidayViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is HolidayViewHolder -> {
                val holidayItem = getItem(position) as HolidayDataItem.HolidayItem
                holder.bind(holidayItem.holiday)
            }
        }
    }

}

class HolidayViewHolder private constructor(val binding: ListItemHolidayRegBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HolidayReg) {
        binding.holiday = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): HolidayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemHolidayRegBinding.inflate(layoutInflater, parent, false)
            return HolidayViewHolder(binding)
        }
    }
}


class HolidayDiffCallBack : DiffUtil.ItemCallback<HolidayDataItem>() {
    override fun areItemsTheSame(
        oldItem: HolidayDataItem,
        newItem: HolidayDataItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: HolidayDataItem,
        newItem: HolidayDataItem
    ): Boolean {
        return newItem == oldItem
    }
}