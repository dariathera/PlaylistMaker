package com.practicum.playlistmaker.search.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.domain.GetTracksInteractor
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.ui.activity.SearchState
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.util.Resource
import debounce
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistorySaver : GetHistoryInteractor,
    private val getTracksInteractor : GetTracksInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(
        SearchState.Blank)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val isClearButtonVisibleLiveData = MutableLiveData<Boolean>(false)
    fun observeIsClearButtonVisible(): LiveData<Boolean> = isClearButtonVisibleLiveData

    private val history : ArrayDeque<Track> = searchHistorySaver.getFromMemory()

    var userInput : String = EMPTY_LINE
    private var isUserEnteringText : Boolean = false

    private val trackSearchDebounce = debounce<Unit>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { _ ->
        stateLiveData.postValue(
            SearchState.Loading
        )
        makeRequest()
    }

    private val trackSearchWithoutDebounce = debounce<Unit>(
        0,
        viewModelScope,
        true
    ) { _ ->
        stateLiveData.postValue(
            SearchState.Loading
        )
        makeRequest()
    }

    private fun createHistoryStateValue() : SearchState {
        history.clear()
        history.addAll(searchHistorySaver.getFromMemory())
        return if (history.isEmpty()) {
            SearchState.Blank
        } else {
            SearchState.History(history)
        }
    }

    fun clearTheInputField() {

        // Так можно писать только потому, что кроме отложенного запроса
        // у нас нет других отложенных задач.
        viewModelScope.coroutineContext.cancelChildren()

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
        trackSearchDebounce(Unit)
    }

    fun notifyUserClicksInputField(inputtedText: String) {
        if (inputtedText.isEmpty()) {
            stateLiveData.postValue(
                createHistoryStateValue()
            )
        }
    }

    fun makeRequestNow() {
        viewModelScope.coroutineContext.cancelChildren()
        trackSearchWithoutDebounce(Unit)
    }

    private fun makeRequest() {
        if (!userInput.isEmpty()) {

            viewModelScope.launch {
                getTracksInteractor
                    .searchMusic(userInput)
                    .collect { response ->
                        handleResponse(response)
                    }
            }
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

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val EMPTY_LINE = ""
    }
}


