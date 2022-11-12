package com.jakob.tippy

import android.animation.ArgbEvaluator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {

    // Main
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
    private lateinit var ivGitHub: ImageView

    // For Translation
    private lateinit var tvBaseLabel: TextView
    private lateinit var tvTipLabel: TextView
    private lateinit var tvTotalLabel: TextView
    private lateinit var tvPeopleLabel: TextView
    private lateinit var tvServiceLabel: TextView
    private lateinit var tvCountryLabel: TextView
    //private lateinit var etPeopleAmount: EditText
    private lateinit var tvFooter: TextView
    private lateinit var tvPerPerson: TextView
    private lateinit var tvGitHub: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Main
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
        ivGitHub = findViewById(R.id.ivGitHub)

        // For Translation
        tvBaseLabel = findViewById(R.id.tvBaseLabel)
        tvTipLabel = findViewById(R.id.tvTipLabel)
        tvTotalLabel = findViewById(R.id.tvTotalLabel)
        tvPeopleLabel = findViewById(R.id.tvPeopleLabel)
        tvServiceLabel = findViewById(R.id.tvServiceLabel)
        tvCountryLabel = findViewById(R.id.tvCountryLabel)
        //etPeopleAmount = findViewById(R.id.etPeopleAmount)
        tvFooter = findViewById(R.id.tvFooter)
        tvPerPerson = findViewById(R.id.tvPerPerson)
        tvGitHub = findViewById(R.id.tvGitHub)

        seekBarTip.progress = INITIAL_TIP_PERCENT

        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"

        updateTipDescription(INITIAL_TIP_PERCENT)
        getCurrencyTypeFromSharedPreferences()

        etPeopleAmount.setText("1")

        updateTvPerPerson()

        ivGitHub.setOnClickListener(View.OnClickListener {
            // This is the link to my GitHub
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/FleetAdmiralJakob/Tippy"))
            startActivity(browserIntent)
        })

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
                updateLanguage()
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
                // If the text equals 1, then tvperperson should display nothing
                updateTvPerPerson()
            }
        })
    }

    private fun updateTvPerPerson() {
        if (etPeopleAmount.text.toString() == "1") {
            tvPerPerson.text = ""
        } else {
            updateLanguage()
        }
    }

    private fun updateLanguage() {
        // Every string of a language is located in a own string.xml file so I call the right language based on the selected position of my country spinner.
        when (countrySpinner.selectedItemPosition) {
            0 -> {
                // DE
                tvBaseLabel.text = getString(R.string.base_de)
                tvTipLabel.text = getString(R.string.tip_de)
                tvTotalLabel.text = getString(R.string.total_de)
                tvPeopleLabel.text = getString(R.string.people_de)
                tvServiceLabel.text = getString(R.string.service_de)
                tvCountryLabel.text = getString(R.string.country_de)
                etBaseAmount.hint = getString(R.string.bill_amount_de)
                tvFooter.text = getString(R.string.credit_de)
                tvPerPerson.text = getString(R.string.per_person_de)
                tvGitHub.text = getString(R.string.view_on_github_de)
            }
            1 -> {
                // EN Normal (Default Value)
                tvBaseLabel.text = getString(R.string.base)
                tvTipLabel.text = getString(R.string.tip)
                tvTotalLabel.text = getString(R.string.total)
                tvPeopleLabel.text = getString(R.string.people)
                tvServiceLabel.text = getString(R.string.service)
                tvCountryLabel.text = getString(R.string.country)
                etBaseAmount.hint = getString(R.string.bill_amount)
                tvFooter.text = getString(R.string.credit)
                tvPerPerson.text = getString(R.string.per_person)
                tvGitHub.text = getString(R.string.view_on_github)
            }
            2 -> {
                // EN Normal (Default Value)
                tvBaseLabel.text = getString(R.string.base)
                tvTipLabel.text = getString(R.string.tip)
                tvTotalLabel.text = getString(R.string.total)
                tvPeopleLabel.text = getString(R.string.people)
                tvServiceLabel.text = getString(R.string.service)
                tvCountryLabel.text = getString(R.string.country)
                etBaseAmount.hint = getString(R.string.bill_amount)
                tvFooter.text = getString(R.string.credit)
                tvPerPerson.text = getString(R.string.per_person)
                tvGitHub.text = getString(R.string.view_on_github)
            }
            3 -> {
                // EN Normal (Default Value)
                tvBaseLabel.text = getString(R.string.base)
                tvTipLabel.text = getString(R.string.tip)
                tvTotalLabel.text = getString(R.string.total)
                tvPeopleLabel.text = getString(R.string.people)
                tvServiceLabel.text = getString(R.string.service)
                tvCountryLabel.text = getString(R.string.country)
                etBaseAmount.hint = getString(R.string.bill_amount)
                tvFooter.text = getString(R.string.credit)
                tvPerPerson.text = getString(R.string.per_person)
                tvGitHub.text = getString(R.string.view_on_github)
            }
            4 -> {
                // JP
            }
            5 -> {
                // PL
            }
            6 -> {
                // RU
            }
        }
        updateTvPerPerson()
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