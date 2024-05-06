package com.app.yoursafetyfirst.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.DialogMonthYearPickerBinding
import java.util.Calendar
import java.util.Date

class YearPickerDialog(private val date: Date = Date()) : DialogFragment() {

    companion object {
        private val MAX_YEAR: Int = Calendar.getInstance().get(Calendar.YEAR)-18
    }


    private lateinit var binding: DialogMonthYearPickerBinding

    private var listener: DatePickerDialog.OnDateSetListener? = null

    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMonthYearPickerBinding.inflate(requireActivity().layoutInflater)
        val cal: Calendar = Calendar.getInstance().apply { time = date }

        binding.pickerYear.run {
            minValue = 1906
            maxValue = MAX_YEAR
        }

        binding.pickerYear.setOnValueChangedListener { picker, oldVal, newVal ->
            binding.pickerYear.value = newVal
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(requireActivity().resources.getString(R.string.select_year))
            .setView(binding.root)
            .setPositiveButton(requireActivity().resources.getString(R.string.ok)) { _, _ ->
                listener?.onDateSet(
                    null,
                    binding.pickerYear.value,
                    0,
                    0
                )
            }
            .setNegativeButton(requireActivity().resources.getString(R.string.cancel)) { _, _ -> dialog?.cancel() }
            .create()
    }
}