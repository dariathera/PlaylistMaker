package com.practicum.playlistmaker.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.GetMusicInteractor
import com.practicum.playlistmaker.domain.entities.BadResponse
import com.practicum.playlistmaker.domain.entities.EmptyResponse
import com.practicum.playlistmaker.domain.entities.GetMusicResponse
import com.practicum.playlistmaker.domain.entities.GoodResponse
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.domain.repository.SearchHistorySaverRepository
import com.practicum.playlistmaker.domain.use_cases.MusicRequestUseCase

class SearchActivity : AppCompatActivity(), OnTrackListClickListener {

    private lateinit var btnGoBack: Button
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var background: LinearLayout
    private lateinit var inputMethodManager: InputMethodManager
    private var userInput: String = EMPTY_LINE
    private lateinit var recyclerView : RecyclerView
    private lateinit var searchTrackAdapter : SearchTrackAdapter
    private lateinit var nothingFoundLayout : LinearLayout
    private lateinit var connectionProblemsLayout : LinearLayout
    private lateinit var updateButton : Button
    private lateinit var txtSearchHistory : TextView
    private lateinit var btnClearSearchHistory : Button
    private lateinit var savedTracksAdapter : SearchTrackAdapter
    private lateinit var searchBlock : LinearLayout
    private lateinit var handler : Handler
    private lateinit var searchRunnable : Runnable
    private lateinit var progressBar : ProgressBar
    private lateinit var searchRunnableWithProgress : Runnable
    private var isClickAllowed = true
    private lateinit var searchHistorySaver : SearchHistorySaverRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        btnGoBack = findViewById(R.id.btnGoBack)
        inputEditText = findViewById(R.id.editText)
        clearButton = findViewById(R.id.clearIcon)
        background = findViewById(R.id.background)
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        nothingFoundLayout = findViewById(R.id.nothingFound)
        connectionProblemsLayout = findViewById(R.id.connectionProblems)
        updateButton = findViewById(R.id.btn_update)
        txtSearchHistory = findViewById(R.id.search_history)
        btnClearSearchHistory = findViewById(R.id.clear_search_history)
        recyclerView = findViewById(R.id.recyclerView)
        searchTrackAdapter = SearchTrackAdapter(ArrayDeque<Track>(), this)
        searchHistorySaver = Creator.getSearchHistorySaverRepository()
        savedTracksAdapter = SearchTrackAdapter(searchHistorySaver.getFromMemory(), this)
        recyclerView.adapter = searchTrackAdapter
        searchBlock = findViewById(R.id.searchBlock)
        handler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { makeRequest() }
        progressBar = findViewById(R.id.progressBar)
        searchRunnableWithProgress = Runnable {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            searchBlock.visibility = View.GONE
            nothingFoundLayout.visibility = View.GONE
            connectionProblemsLayout.visibility = View.GONE
            searchRunnable.run()
        }

        txtSearchHistory.visibility = View.GONE
        btnClearSearchHistory.visibility = View.GONE
        progressBar.visibility = View.GONE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.background)) {
            v, insets ->
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
                showHistory(inputEditText.hasFocus() && s?.isEmpty() == true)
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            showHistory(hasFocus && inputEditText.text.isEmpty())
        }

        btnClearSearchHistory.setOnClickListener {
            searchHistorySaver.clearHistory()
            showHistory(true)
        }

        //------------------------------------------------------------------------------------
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnClickListener {
            inputEditText.requestFocus()
        }

        //------------------------------------------------------------------------------------
        background.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        //------------------------------------------------------------------------------------
        // поиск
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnableWithProgress) // удаляем отложенный поисковый запрос
                searchRunnableWithProgress.run() // выполняем запрос сейчас
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
            val searchText = inputEditText.text.toString()
            val musicRequestUseCase = MusicRequestUseCase()
            val consumer = object : GetMusicInteractor.GetMusicConsumer {
                override fun consume(response: GetMusicResponse) {
                    handler.post {
                        handleResponse(response)
                    }
                }
            }
            musicRequestUseCase.makeRequest(searchText, consumer)
        } else {
            recyclerView.visibility = View.GONE
            searchBlock.visibility = View.GONE
            nothingFoundLayout.visibility = View.GONE
            connectionProblemsLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun handleResponse(response: GetMusicResponse) {
        when (response) {
            is GoodResponse -> {
                searchTrackAdapter.updateData(response.tracks)
                recyclerView.visibility = View.VISIBLE
                searchBlock.visibility = View.VISIBLE
                nothingFoundLayout.visibility = View.GONE
                connectionProblemsLayout.visibility = View.GONE
                progressBar.visibility = View.GONE
            }


            is EmptyResponse -> {
                recyclerView.visibility = View.GONE
                searchBlock.visibility = View.GONE
                nothingFoundLayout.visibility = View.VISIBLE
                connectionProblemsLayout.visibility = View.GONE
                progressBar.visibility = View.GONE
            }

            is BadResponse -> {
                recyclerView.visibility = View.GONE
                searchBlock.visibility = View.GONE
                nothingFoundLayout.visibility = View.GONE
                connectionProblemsLayout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showHistory (isHistoryVisible : Boolean) {
        txtSearchHistory.visibility =  if (isHistoryVisible) View.VISIBLE else View.GONE
        btnClearSearchHistory.visibility =  if (isHistoryVisible) View.VISIBLE else View.GONE
        savedTracksAdapter.updateData(searchHistorySaver.getFromMemory())
        recyclerView.adapter = if (isHistoryVisible) savedTracksAdapter else searchTrackAdapter
        recyclerView.visibility = View.VISIBLE
        searchBlock.visibility = View.VISIBLE
        if (searchHistorySaver.getFromMemory().isEmpty()) {
            hideHistory()
        }
    }

    private fun hideHistory() {
        recyclerView.visibility = View.GONE
        txtSearchHistory.visibility = View.GONE
        btnClearSearchHistory.visibility =  View.GONE
    }

    //------------------------------------------------------------------------------------
    // Отложенный запрос
    // Создаем новый Runnable, который сначала устанавливает видимость progressBar, а затем выполняет searchRunnable
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnableWithProgress)
        handler.postDelayed(searchRunnableWithProgress, SEARCH_DEBOUNCE_DELAY)
    }

    //------------------------------------------------------------------------------------

    override fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({
                isClickAllowed = true
            }, CLICK_DEBOUNCE_DELAY)
        } else {
        }
        return current
    }

    //------------------------------------------------------------------------------------
    // Константы
    companion object {
        private const val USER_INPUT = "USER_INPUT"
        private const val EMPTY_LINE = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}