package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.EmailData
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        context.startActivity(Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_to)))
    }

    override fun openLink(link: String) {
        val url = Uri.parse(link)
        val userAgreementIntent = Intent(Intent.ACTION_VIEW, url)
        context.startActivity(userAgreementIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.text)
        }
        context.startActivity(supportIntent)
    }
}