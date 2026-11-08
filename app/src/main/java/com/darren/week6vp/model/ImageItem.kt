package com.darren.week6vp.model

import androidx.annotation.DrawableRes

data class ImageItem(
    @DrawableRes val image: Int,
    val name: String
)
