package com.jakob.tippy

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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

        getFromSharedPrefCountry()
        getFromSharedPrefCurrency()
        Toast.makeText(this, "Country & Currency loaded", Toast.LENGTH_SHORT).show()

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
                updateCurrencyDescription()
                saveToSharedPrefCurrency()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCurrencySpinner()
                updateLanguage()
                saveToSharedPrefCountry()
                // Get variable progress
                val progress = seekBarTip.progress
                updateTipDescription(progress)
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

    private fun saveToSharedPrefCurrency() {
        val sharedPrefCur = getSharedPreferences("currency", Context.MODE_PRIVATE)
        val sharedPrefEdiCur = sharedPrefCur.edit()
        val currenciesSpinner: Spinner = findViewById(R.id.currenciesSpinner)

        val position : Int = currenciesSpinner.selectedItemPosition
        sharedPrefEdiCur.apply {
            putInt("position currency", position)
            apply()
        }
    }

    private fun saveToSharedPrefCountry() {
        val sharedPrefCou = getSharedPreferences("country", Context.MODE_PRIVATE)
        val sharedPrefEdiCou = sharedPrefCou.edit()
        val countrySpinner: Spinner = findViewById(R.id.countrySpinner)

        val position : Int = countrySpinner.selectedItemPosition
        sharedPrefEdiCou.apply {
            putInt("position country", position)
            apply()
        }
    }

    private fun getFromSharedPrefCurrency() {
        val currenciesSpinner: Spinner = findViewById(R.id.currenciesSpinner)
        val sharedPrefCur = getSharedPreferences("currency", Context.MODE_PRIVATE)
        val position : Int = sharedPrefCur.getInt("position currency", 0)
        currenciesSpinner.setSelection(position)
    }

    private fun getFromSharedPrefCountry() {
        val countrySpinner: Spinner = findViewById(R.id.countrySpinner)
        val sharedPrefCou = getSharedPreferences("country", Context.MODE_PRIVATE)
        val position : Int = sharedPrefCou.getInt("position country", 0)
        countrySpinner.setSelection(position)
    }

    private fun updateTvPerPerson() {
        if (etPeopleAmount.text.toString() == "1") {
            tvPerPerson.text = ""
        } else {
// Every string of a language is located in a own string.xml file so I call the right language based on the selected position of my country spinner.
            when (countrySpinner.selectedItemPosition) {
                0 -> {
                    // DE
                    tvBaseLabel.text = getString(R.string.base_de)
                    tvTipLabel.text = getString(R.string.tip_de)
                    tvTotalLabel.text = getString(R.string.total)
                    tvPeopleLabel.text = getString(R.string.people_de)
                    tvServiceLabel.text = getString(R.string.service)
                    tvCountryLabel.text = getString(R.string.country_de)
                    etBaseAmount.hint = getString(R.string.bill_amount_de)
                    tvFooter.text = getString(R.string.credit_de)
                    tvPerPerson.text = getString(R.string.per_person_de)
                    tvGitHub.text = getString(R.string.view_on_github_de)
                }
                1 -> {
                    // FR
                    tvBaseLabel.text = getString(R.string.base_fr)
                    tvTipLabel.text = getString(R.string.tip_fr)
                    tvTotalLabel.text = getString(R.string.total)
                    tvPeopleLabel.text = getString(R.string.people_fr)
                    tvServiceLabel.text = getString(R.string.service)
                    tvCountryLabel.text = getString(R.string.country_fr)
                    etBaseAmount.hint = getString(R.string.bill_amount_fr)
                    tvFooter.text = getString(R.string.credit_fr)
                    tvPerPerson.text = getString(R.string.per_person_fr)
                    tvGitHub.text = getString(R.string.view_on_github_fr)
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
                5 -> {
                    // JP
                    tvBaseLabel.text = getString(R.string.base_jp)
                    tvCountryLabel.text = getString(R.string.country_jp)
                }
                6 -> {
                    // PL
                }
                7 -> {
                    // RU
                }
            }
        }
    }

    private fun updateLanguage() {
        // Every string of a language is located in a own string.xml file so I call the right language based on the selected position of my country spinner.
        when (countrySpinner.selectedItemPosition) {
            0 -> {
                // DE
                tvBaseLabel.text = getString(R.string.base_de)
                tvTipLabel.text = getString(R.string.tip_de)
                tvTotalLabel.text = getString(R.string.total)
                tvPeopleLabel.text = getString(R.string.people_de)
                tvServiceLabel.text = getString(R.string.service)
                tvCountryLabel.text = getString(R.string.country_de)
                etBaseAmount.hint = getString(R.string.bill_amount_de)
                tvFooter.text = getString(R.string.credit_de)
                tvPerPerson.text = getString(R.string.per_person_de)
                tvGitHub.text = getString(R.string.view_on_github_de)
            }
            1 -> {
                // FR
                tvBaseLabel.text = getString(R.string.base_fr)
                tvTipLabel.text = getString(R.string.tip_fr)
                tvTotalLabel.text = getString(R.string.total)
                tvPeopleLabel.text = getString(R.string.people_fr)
                tvServiceLabel.text = getString(R.string.service)
                tvCountryLabel.text = getString(R.string.country_fr)
                etBaseAmount.hint = getString(R.string.bill_amount_fr)
                tvFooter.text = getString(R.string.credit_fr)
                tvPerPerson.text = getString(R.string.per_person_fr)
                tvGitHub.text = getString(R.string.view_on_github_fr)
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
            5 -> {
                // JP
                tvBaseLabel.text = getString(R.string.base_jp)
                tvCountryLabel.text = getString(R.string.country_jp)
            }
            6 -> {
                // PL
            }
            7 -> {
                // RU
            }
        }
        if (etPeopleAmount.text.toString() == "1") {
            tvPerPerson.text = ""
        }
    }

    private fun updateCurrencySpinner() {
        // Update selected item form the currency spinner based on the country spinner
        val currency = when (countrySpinner.selectedItemPosition) {
            0 -> 0
            1 -> 0
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            else -> 0
        }
        currenciesSpinner.setSelection(currency)
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
            in 0..9 -> {
                when (countrySpinner.selectedItemPosition) {
                    0 -> getString(R.string.poor_de)
                    1 -> getString(R.string.poor_fr)
                    2 -> getString(R.string.poor)
                    3 -> getString(R.string.poor)
                    4 -> getString(R.string.poor)
                    5 -> getString(R.string.poor)
                    6 -> getString(R.string.poor)
                    7 -> getString(R.string.poor)
                    else -> getString(R.string.poor)
                }
            }
            in 10..14 -> when (countrySpinner.selectedItemPosition) {
                0 -> getString(R.string.acceptable_de)
                1 -> getString(R.string.acceptable_fr)
                2 -> getString(R.string.acceptable)
                3 -> getString(R.string.acceptable)
                4 -> getString(R.string.acceptable)
                5 -> getString(R.string.acceptable)
                6 -> getString(R.string.acceptable)
                7 -> getString(R.string.acceptable)
                else -> getString(R.string.acceptable)
            }
            in 15..19 -> when (countrySpinner.selectedItemPosition) {
                0 -> getString(R.string.good_de)
                1 -> getString(R.string.good_fr)
                2 -> getString(R.string.good)
                3 -> getString(R.string.good)
                4 -> getString(R.string.good)
                5 -> getString(R.string.good)
                6 -> getString(R.string.good)
                7 -> getString(R.string.good)
                else -> getString(R.string.good)
            }
            in 20..24 -> when (countrySpinner.selectedItemPosition) {
                0 -> getString(R.string.great_de)
                1 -> getString(R.string.great_fr)
                2 -> getString(R.string.great)
                3 -> getString(R.string.great)
                4 -> getString(R.string.great)
                5 -> getString(R.string.great)
                6 -> getString(R.string.great)
                7 -> getString(R.string.great)
                else -> getString(R.string.great)
            }
            else -> when (countrySpinner.selectedItemPosition) {
                0 -> getString(R.string.excellent_de)
                1 -> getString(R.string.excellent_fr)
                2 -> getString(R.string.excellent)
                3 -> getString(R.string.excellent)
                4 -> getString(R.string.excellent)
                5 -> getString(R.string.excellent)
                6 -> getString(R.string.excellent)
                7 -> getString(R.string.excellent)
                else -> getString(R.string.excellent)
            }
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