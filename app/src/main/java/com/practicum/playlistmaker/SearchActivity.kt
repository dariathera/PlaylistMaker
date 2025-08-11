package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    // Логика работы с кнопками

    private lateinit var btnGoBack: Button
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var background: LinearLayout
    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        btnGoBack = findViewById(R.id.btnGoBack)
        inputEditText = findViewById(R.id.editText)
        clearButton = findViewById(R.id.clearIcon)
        background = findViewById(R.id.background)
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.background)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //------------------------------------------------------------------------------------
        btnGoBack.setOnClickListener {
            finish()
        }

        //------------------------------------------------------------------------------------
        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        //------------------------------------------------------------------------------------
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
                userInput = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        //------------------------------------------------------------------------------------
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnClickListener {
            inputEditText.requestFocus()
        }

        //------------------------------------------------------------------------------------
        background.setOnClickListener {
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }
    }

    //------------------------------------------------------------------------------------
    // Сохранение пользовательского ввода

    companion object {
        private const val USER_INPUT = "USER_INPUT"
        private const val EMPTY_LINE = ""
    }

    var userInput: String = EMPTY_LINE

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_INPUT, userInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userInput = savedInstanceState.getString(USER_INPUT, EMPTY_LINE)
        inputEditText.setText(userInput)
    }
}