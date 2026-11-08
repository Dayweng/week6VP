package com.darren.week6vp.model

data class Community(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val members: Int,
    val icon: String
)