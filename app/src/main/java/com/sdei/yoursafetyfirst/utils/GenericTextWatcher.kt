package com.app.yoursafetyfirst.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class GenericTextWatcher(
    private val etNext: TextInputEditText,
    private val etPrev: TextInputEditText
) : TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        if (text.length == 1) etNext.requestFocus() else if (text.isEmpty()) etPrev.requestFocus()
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
}