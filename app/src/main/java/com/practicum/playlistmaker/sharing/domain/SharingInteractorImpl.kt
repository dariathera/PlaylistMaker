package com.practicum.playlistmaker.sharing.domain

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.util.FormatTools

class SharingInteractorImpl(private val context: Context,
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.android_developer_course_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.developers_email),
            context.getString(R.string.subject_of_mail_to_developers),
            context.getString(R.string.text_of_mail_to_developers)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.user_agreement_link)
    }

    override fun sharePlaylist(playlist: Playlist) {
        externalNavigator.shareLink(createPlaylistContentString(playlist))
    }

    private fun createPlaylistContentString(playlist: Playlist): String {
        var s: String = playlist.playlist.name
        s += "\n"
        if (playlist.playlist.description.isNotEmpty()) {
            s += playlist.playlist.description
            s += "\n"
        }
        s += context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.trackList.size,
            playlist.trackList.size
        )
        var number: Int = 1
        for (track in playlist.trackList) {
            s += "\n%d. %s - %s (%s)".format(
                number,
                track.artistName,
                track.trackName,
                FormatTools.millisToMmss(track.trackTime))
            number++
        }
        return s
    }
}