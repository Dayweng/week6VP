package com.darren.week6vp.navigation

import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object CommunitiesRoute

@Serializable
data class ClubProjectsRoute(val communityId: String)