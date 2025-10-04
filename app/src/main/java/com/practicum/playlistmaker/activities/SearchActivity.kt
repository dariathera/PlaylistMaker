package com.practicum.playlistmaker.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.lists.searchTrack.SearchTrackAdapter
import com.practicum.playlistmaker.data.mockdata.mockTrackList
import com.practicum.playlistmaker.net.BadResponse
import com.practicum.playlistmaker.net.EmptyResponse
import com.practicum.playlistmaker.net.GoodResponse
import com.practicum.playlistmaker.net.NetworkInteracter

class SearchActivity : AppCompatActivity() {

    private lateinit var btnGoBack: Button
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var background: LinearLayout
    private lateinit var inputMethodManager: InputMethodManager
    private var userInput: String = EMPTY_LINE
    private lateinit var recyclerView : RecyclerView
    private lateinit var searchTrackAdapter : SearchTrackAdapter
    private val networkInteracter : NetworkInteracter by lazy { NetworkInteracter() }
    private lateinit var nothingFoundLayout : LinearLayout
    private lateinit var connectionProblemsLayout : LinearLayout
    private lateinit var updateButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        btnGoBack = findViewById(R.id.btnGoBack)
        inputEditText = findViewById(R.id.editText)
        clearButton = findViewById(R.id.clearIcon)
        background = findViewById(R.id.background)
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        nothingFoundLayout = findViewById(R.id.nothingFound)
        connectionProblemsLayout = findViewById(R.id.connectionProblems)
        updateButton = findViewById(R.id.btn_update)


        // Создание списка треков
        recyclerView = findViewById(R.id.recyclerView)
        searchTrackAdapter = SearchTrackAdapter(mockTrackList())
        recyclerView.adapter = searchTrackAdapter

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
            recyclerView.visibility = View.GONE
            nothingFoundLayout.visibility = View.GONE
            connectionProblemsLayout.visibility = View.GONE
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

        //------------------------------------------------------------------------------------
        // поиск
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                makeRequest()
                true
            }
            false
        }

        updateButton.setOnClickListener {
            makeRequest()
        }
    }

    //------------------------------------------------------------------------------------
    // Сохранение пользовательского ввода
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_INPUT, userInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userInput = savedInstanceState.getString(USER_INPUT, EMPTY_LINE)
        inputEditText.setText(userInput)
    }

    private fun makeRequest() {
        if (!inputEditText.text.isEmpty()) {
            networkInteracter.getMusic(inputEditText.text.toString()) { response ->
                when (response) {
                    is GoodResponse -> {
                        searchTrackAdapter.updateData(response.tracks)
                        recyclerView.visibility = View.VISIBLE
                        nothingFoundLayout.visibility = View.GONE
                        connectionProblemsLayout.visibility = View.GONE
                    }
                    is EmptyResponse -> {
                        recyclerView.visibility = View.GONE
                        nothingFoundLayout.visibility = View.VISIBLE
                        connectionProblemsLayout.visibility = View.GONE
                    }
                    is BadResponse -> {
                        recyclerView.visibility = View.GONE
                        nothingFoundLayout.visibility = View.GONE
                        connectionProblemsLayout.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            recyclerView.visibility = View.GONE
            nothingFoundLayout.visibility = View.GONE
            connectionProblemsLayout.visibility = View.GONE
        }
    }

    //------------------------------------------------------------------------------------
    // Константы
    companion object {
        private const val USER_INPUT = "USER_INPUT"
        private const val EMPTY_LINE = ""
    }
}
