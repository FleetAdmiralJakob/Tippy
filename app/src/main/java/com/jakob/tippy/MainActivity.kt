package com.jakob.tippy

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var etPeopleAmount: EditText
    private lateinit var serviceSpinner: Spinner
    private lateinit var currenciesSpinner: Spinner
    private lateinit var tvCurrency: TextView
    private lateinit var countrySpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercent = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etPeopleAmount = findViewById(R.id.etPeopleAmount)
        serviceSpinner = findViewById(R.id.serviceSpinner)
        currenciesSpinner = findViewById(R.id.currenciesSpinner)
        tvCurrency = findViewById(R.id.tvCurrency)
        countrySpinner = findViewById(R.id.countrySpinner)

        seekBarTip.progress = INITIAL_TIP_PERCENT

        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"

        updateTipDescription(INITIAL_TIP_PERCENT)
        getCurrencyTypeFromSharedPreferences()

        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTipPercent.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        serviceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSeekBarTip()
                computeTipAndTotal()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        currenciesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveCurrencyTypeForNextTime()
                updateCurrencyDescription()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCurrencySpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
            }
        })

        etPeopleAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
            }
        })
    }

    private fun updateCurrencySpinner() {
        // Update selected item form the currency spinner based on the country spinner
        val currency = when (countrySpinner.selectedItemPosition) {
            0 -> 0
            1 -> 0
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            6 -> 5
            else -> 0
        }
        currenciesSpinner.setSelection(currency)
    }

    private fun getCurrencyTypeFromSharedPreferences() {
        //TODO("Not yet implemented")
    }

    private fun saveCurrencyTypeForNextTime() {
        //TODO("Not yet implemented")
    }

    private fun updateCurrencyDescription() {
        val currency2 = currenciesSpinner.selectedItem.toString()
        tvCurrency.text = currency2
    }

    private fun updateSeekBarTip() {
        val tipPercent = when (serviceSpinner.selectedItemPosition) {
            0 -> 5
            1 -> 12
            2 -> 17
            3 -> 22
            4 -> 27
            else -> 0
        }
        seekBarTip.progress = tipPercent
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor 😕"
            in 10..14 -> "Acceptable 😐"
            in 15..19 -> "Good 😌"
            in 20..24 -> "Great 😁"
            else -> "Amazing 😍"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        if (etPeopleAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        // 1. Get the value of the base and tip percent
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val peopleAmount = etPeopleAmount.text.toString().toDouble()

        val tipPercent = seekBarTip.progress
        // 2. Compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = (baseAmount + tipAmount) / peopleAmount
        // 3. Update the UI with the values
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}