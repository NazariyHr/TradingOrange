package com.trading.orange.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class ImageProvider : Parcelable {
    open suspend fun provideImage(widthPx: Long? = null, heightPx: Long? = null): ByteArray? = null
}