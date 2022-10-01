package com.teamartti.artti

import android.graphics.Bitmap

data class ItemModel(
    val image:Bitmap?
){
    constructor():this(null)
}
