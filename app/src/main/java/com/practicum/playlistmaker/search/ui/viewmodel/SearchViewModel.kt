package com.practicum.playlistmaker.search.ui.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.domain.GetTracksInteractor
import com.practicum.playlistmaker.search.domain.UserMakesTracksRequestUseCase
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.ui.activity.SearchState
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.util.Resource

class SearchViewModel(
    private val context: Context
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(
        SearchState.Blank)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val isClearButtonVisibleLiveData = MutableLiveData<Boolean>(false)
    fun observeIsClearButtonVisible(): LiveData<Boolean> = isClearButtonVisibleLiveData

    private val searchHistorySaver : GetHistoryInteractor =
        Creator.provideGetHistoryInteractor()
    private val history : ArrayDeque<Track> = searchHistorySaver.getFromMemory()

    var userInput : String = EMPTY_LINE
    private var isUserEnteringText : Boolean = false

    private var handler : Handler = Handler(Looper.getMainLooper())
    private val searchRunnable : Runnable = Runnable { makeRequest() }
    private val searchRunnableWithProgress : Runnable = Runnable {
        stateLiveData.postValue(
            SearchState.Loading
        )
        searchRunnable.run()
    }

    private var isClickAllowed = true

    private fun createHistoryStateValue() : SearchState {
        history.clear()
        history.addAll(searchHistorySaver.getFromMemory())
        return if (history.isEmpty()) {
            Log.d(
                App.Companion.DEBUG_LOG_TAG,
                "Установлено состояние SearchState.Blank"
            )
            SearchState.Blank
        } else {
            Log.d(
                App.Companion.DEBUG_LOG_TAG,
                "Установлено состояние SearchState.History(history)"
            )
            SearchState.History(history)
        }
    }

    fun clearTheInputField() {
        handler.removeCallbacks(searchRunnableWithProgress)
        userInput = EMPTY_LINE
        stateLiveData.postValue(
            createHistoryStateValue()
        )
        isClearButtonVisibleLiveData.postValue(false)
    }

    fun  handleInput(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            isClearButtonVisibleLiveData.postValue(false)
        } else {
            isClearButtonVisibleLiveData.postValue(true)
        }
        userInput = s.toString()
        if (isUserEnteringText && s?.isEmpty() == true) {
            stateLiveData.postValue(
                createHistoryStateValue()
            )
        }
        searchDebounce()
    }

    fun notifyUserClicksInputField(inputtedText: String) {
        Log.d(
            App.Companion.DEBUG_LOG_TAG,
            "Пользователь нажал на поле ввода"
        )
        if (inputtedText.isEmpty()) {
            Log.d(
                App.Companion.DEBUG_LOG_TAG,
                "Поле ввода пусто"
            )
            stateLiveData.postValue(
                createHistoryStateValue()
            )
        }
    }

    // Отложенный запрос
    // Создаем новый Runnable, который сначала устанавливает видимость progressBar, а затем выполняет searchRunnable
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnableWithProgress)
        handler.postDelayed(searchRunnableWithProgress, SEARCH_DEBOUNCE_DELAY)
    }

    fun makeRequestNow() {
        handler.removeCallbacks(searchRunnableWithProgress)
        searchRunnableWithProgress.run()
    }

    private fun makeRequest() {
        if (!userInput.isEmpty()) {
            val musicRequestUseCase : UserMakesTracksRequestUseCase = Creator.provideUserMakesTracksRequestUseCase(context)
            val consumer = object : GetTracksInteractor.GetMusicConsumer {
                override fun consume(response: Resource<MutableList<Track>>) {
                    handler.post {
                        handleResponse(response)
                    }
                }
            }
            musicRequestUseCase.makeRequest(userInput, consumer)
        } else {
            stateLiveData.postValue(
                createHistoryStateValue()
            )
        }
    }

    private fun handleResponse(response: Resource<MutableList<Track>>) {
        when (response) {
            is Resource.Success<MutableList<Track>> -> {
                if (response.data.isNullOrEmpty()) {
                    stateLiveData.postValue(
                        SearchState.NothingFound
                    )
                } else {
                    stateLiveData.postValue(
                        SearchState.FoundTracks(response.data)
                    )
                }
            }

            is Resource.Error<MutableList<Track>> -> {
                stateLiveData.postValue(
                    SearchState.NoInternet
                )
                Log.e(
                    App.Companion.ERROR_LOG_TAG,
                    "SearchActivity.handleResponse: ${response.message}"
                )
            }
        }
    }

    fun notifyUserIsEnteringText(hasFocus : Boolean) {
        isUserEnteringText = hasFocus
    }

    fun clearSearchHistory() {
        searchHistorySaver.clearHistory()
        stateLiveData.postValue(
            SearchState.Blank
        )
    }

    fun clickDebounce(): Boolean {
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

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }

    companion object {
        fun getFactory(
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(context)
            }
        }

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val EMPTY_LINE = ""
    }
}
