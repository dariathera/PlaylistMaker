package com.practicum.playlistmaker.sharing.domain

import android.content.Context
import com.practicum.playlistmaker.R

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
}