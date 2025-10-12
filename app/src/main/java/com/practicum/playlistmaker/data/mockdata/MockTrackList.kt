package com.practicum.playlistmaker.data.mockdata

import com.practicum.playlistmaker.data.objects.Track

// Я убрала изменяемое поле isFixed у объекта Track,
// поэтому не mutableListOf()
internal fun mockTrackList() : MutableList<Track> {
    val list = mutableListOf(

        // Трек 1
        Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg",
            -1
        ),

        // Трек 2
        Track(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
            -2

        ),

        // Трек 3
        Track(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
            -3
        ),

        // Трек 4
        Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
            -4
        ),

        // Трек 5
        Track(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg",
            -5
        ),

        // Трек длинный
        Track(
            "Длинное название трека Длинное название трека Длинное название трека",
            "Длинное имя исполнителя Длинное имя исполнителя Длинное имя исполнителя",
            "10:00:00",
            "https://некорректная_ссылка.jpg",
            -6
        )

    )

    return list
}
