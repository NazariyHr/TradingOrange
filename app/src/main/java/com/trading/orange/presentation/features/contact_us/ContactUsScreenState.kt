package com.trading.orange.presentation.features.contact_us

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactUsScreenState(
    val title: String = "Contact Us"
) : Parcelable