package com.practicum.playlistmaker.search.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.library.ui.activity.FavoritesState
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity(), OnTrackListClickListener {

    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var searchTrackAdapter : SearchTrackAdapter
    private lateinit var savedTracksAdapter : SearchTrackAdapter
    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        searchTrackAdapter = SearchTrackAdapter(ArrayDeque<Track>(), this)
        savedTracksAdapter = SearchTrackAdapter(ArrayDeque<Track>(), this)
        binding.recyclerView.adapter = searchTrackAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.background)) {
                v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.observeIsClearButtonVisible().observe(this) {
            if (it == true) {
                binding.clearButton.visibility = View.VISIBLE
            } else {
                binding.clearButton.visibility = View.GONE
            }
        }

        viewModel.observeState().observe(this) {
            render(it)
        }

        //------------------------------------------------------------------------------------
        binding.btnGoBack.setOnClickListener {
            finish()
        }

        //------------------------------------------------------------------------------------
        binding.clearButton.setOnClickListener {
            viewModel.clearTheInputField()
            binding.inputEditText.setText(SearchViewModel.EMPTY_LINE) // Попробуй без неё
        }

        //------------------------------------------------------------------------------------
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.inputEditText.hasFocus()) {
                    viewModel.handleInput(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.notifyUserIsEnteringText(hasFocus)
            if(!hasFocus) {
                hideKeyboard()
            } else {
                // ...
            }
        }

        binding.inputEditText.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Показываем клавиатуру
                view.requestFocus()
                showKeyboardImmediately()
                viewModel.notifyUserClicksInputField(binding.inputEditText.text.toString())
                return@setOnTouchListener true
            }
            false
        }

        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        // Нажатие на лупу на экранной клавиатуре
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.makeRequestNow()
                true
            }
            false
        }

        // Обработка клика по фону для скрытия клавиатуры
        binding.background.setOnClickListener {
            hideKeyboard()
            binding.inputEditText.clearFocus()
        }

        binding.btnClearSearchHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        binding.updateButton.setOnClickListener {
            viewModel.makeRequestNow()
        }

        binding.historyBlankLayout.setOnClickListener {
            binding.inputEditText.clearFocus()
            hideKeyboard()
        }

        binding.txtSearchHistory.setOnClickListener {
            binding.inputEditText.clearFocus()
            hideKeyboard()
        }

    }

    override fun onResume() {
        super.onResume()
        // Восстанавливаем состояние при возвращении в активность
        if (binding.inputEditText.hasFocus()) {
            showKeyboardImmediately()
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Blank -> {
                binding.apply {
                    recyclerView.visibility = View.GONE
                    searchBlock.visibility = View.GONE
                    nothingFoundLayout.visibility = View.GONE
                    connectionProblemsLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    txtSearchHistory.visibility =  View.GONE
                    btnClearSearchHistory.visibility =  View.GONE
                    blankLayout.visibility = View.VISIBLE
                }
            }
            is SearchState.Loading -> {
                binding.apply {
                    recyclerView.visibility = View.GONE
                    searchBlock.visibility = View.GONE
                    nothingFoundLayout.visibility = View.GONE
                    connectionProblemsLayout.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    txtSearchHistory.visibility = View.GONE
                    btnClearSearchHistory.visibility = View.GONE
                    blankLayout.visibility = View.GONE
                }
            }
            is SearchState.NothingFound -> {
                binding.apply {
                    recyclerView.visibility = View.GONE
                    searchBlock.visibility = View.GONE
                    nothingFoundLayout.visibility = View.VISIBLE
                    connectionProblemsLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    txtSearchHistory.visibility =  View.GONE
                    btnClearSearchHistory.visibility =  View.GONE
                    blankLayout.visibility = View.GONE
                }
            }
            is SearchState.NoInternet -> {
                binding.apply {
                    recyclerView.visibility = View.GONE
                    searchBlock.visibility = View.GONE
                    nothingFoundLayout.visibility = View.GONE
                    connectionProblemsLayout.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    txtSearchHistory.visibility =  View.GONE
                    btnClearSearchHistory.visibility =  View.GONE
                    blankLayout.visibility = View.GONE
                }
            }
            is SearchState.History -> {
                savedTracksAdapter.updateData(state.tracks)
                binding.apply {
                    recyclerView.visibility = View.VISIBLE
                    searchBlock.visibility = View.VISIBLE
                    nothingFoundLayout.visibility = View.GONE
                    connectionProblemsLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    txtSearchHistory.visibility =  View.VISIBLE
                    btnClearSearchHistory.visibility =  View.VISIBLE
                    recyclerView.adapter = savedTracksAdapter
                    blankLayout.visibility = View.GONE
                }
            }
            is SearchState.FoundTracks -> {
                searchTrackAdapter.updateData(state.tracks)
                binding.apply {
                    recyclerView.visibility = View.VISIBLE
                    searchBlock.visibility = View.VISIBLE
                    nothingFoundLayout.visibility = View.GONE
                    connectionProblemsLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    txtSearchHistory.visibility =  View.GONE
                    btnClearSearchHistory.visibility =  View.GONE
                    recyclerView.adapter = searchTrackAdapter
                    blankLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun showKeyboardImmediately() {
        if (binding.inputEditText.isAttachedToWindow && binding.inputEditText.windowToken != null) {
            inputMethodManager.showSoftInput(binding.inputEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            // Если View не готов, отложим показ
            binding.inputEditText.post {
                inputMethodManager.showSoftInput(binding.inputEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: binding.inputEditText
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun clickDebounce(): Boolean {
        return viewModel.clickDebounce()
    }

}