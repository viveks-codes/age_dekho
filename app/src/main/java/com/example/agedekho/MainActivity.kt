package com.example.agedekho

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var gender = "1"
    var year: String = "0"
    var height: String = "0"
    var religion: String = "0"
    var caste: String = "0"
    var motherTongue: String = "0"
    var country: String = "0"
    private val array: Array<Int> = arrayOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
        12, 14, 14, 15, 16, 17, 18, 19, 19, 19,
        20, 21, 22, 23, 24, 24, 25, 26, 27, 28, 30,
        29, 30, 31, 32, 33, 34, 35, 36,
        37, 38, 39, 40, 8, 42
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ArrayAdapter.createFromResource(
            this,
            R.array.religions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            religionSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.castes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            casteSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.country,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            countrySpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.motherTongue,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            motherTongueSpinner.adapter = adapter
        }

        religionSpinner.onItemSelectedListener = this
        casteSpinner.onItemSelectedListener = this
        motherTongueSpinner.onItemSelectedListener = this
        countrySpinner.onItemSelectedListener = this

        yearOfBirthEd.requestFocus()

        heightEd.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (heightEd.text.toString().isNotEmpty()) {
                    textInputLayout2.clearFocus()
                    hideKB(v)
                    textInputLayout2.isErrorEnabled = false
                } else {
                    textInputLayout2.error = "This Field cannot be Empty"
                    textInputLayout2.requestFocus()
                }
                true
            } else {
                textInputLayout2.isErrorEnabled = false
                false
            }
        }

        yearOfBirthEd.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (yearOfBirthEd.text.toString().isNotEmpty()) {
                    textInputLayout.clearFocus()
                    textInputLayout.isErrorEnabled = false
                    heightEd.requestFocus()
                } else {
                    textInputLayout.error = "This Field cannot be Empty"
                    textInputLayout.requestFocus()
                }
                true
            } else {
                textInputLayout2.isErrorEnabled = false
                false
            }
        }

        predict_btn.setOnClickListener {

            result_txt.visibility = View.GONE

            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

            if (!isConnected) {
                result_txt.text = getString(R.string.internet_not_available)
                result_txt.visibility = View.VISIBLE
                result_txt.text
                return@setOnClickListener
            }

            if (heightEd.text.toString().isEmpty()) {
                textInputLayout2.error = "This Field cannot be Empty"
                textInputLayout2.requestFocus()
                showKB()
            }

            if (yearOfBirthEd.text.toString().isEmpty()) {
                textInputLayout.error = "This Field cannot be Empty"
                textInputLayout.requestFocus()
                showKB()
            }
            if (yearOfBirthEd.text.toString().isNotEmpty() && heightEd.text.toString()
                    .isNotEmpty()
            ) {
                year = yearOfBirthEd.text.toString()
                height = heightEd.text.toString()
            } else {
                return@setOnClickListener
            }
            religion = religionSpinner.selectedItemPosition.toString()
            caste = array[casteSpinner.selectedItemPosition].toString()
            motherTongue = motherTongueSpinner.selectedItemPosition.toString()
            country = countrySpinner.selectedItemPosition.toString()

            progress_bar.visibility = View.VISIBLE
            result_txt.visibility = View.GONE
            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET,
                "https://age-dekho.herokuapp.com/predict?gender=${gender}&height_cms=${height}&caste=${caste}&religion=${religion}&mother_tongue=${motherTongue}&country=${country}",
                { response ->
                    result_txt.text = getString(
                        R.string.result_txt,
                        (year.toInt() + response.toFloat().toInt()).toString()
                    )
                    result_txt.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE
                },
                {
                    result_txt.text = getString(R.string.errorInFetching)
                    result_txt.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE
                })
            queue.add(stringRequest)
        }
    }

    private fun hideKB(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKB() {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
