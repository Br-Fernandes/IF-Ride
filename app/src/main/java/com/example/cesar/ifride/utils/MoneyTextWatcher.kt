package com.example.cesar.ifride.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MoneyTextWatcher(private val editText: EditText): TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val value = s?.toString()?.replace(",", "")?.toLongOrNull() ?: 0L
        val formattedValue = String.format("%02d,%02d", value / 100, value % 100)
        editText.removeTextChangedListener(this)
        editText.setText(formattedValue)
        editText.setSelection(formattedValue.length)
        editText.addTextChangedListener(this)

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {

    }
}