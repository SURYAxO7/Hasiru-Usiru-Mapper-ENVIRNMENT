package com.hasiru.usiru.mapper.presentation.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MAIN = "main"
    const val DASHBOARD = "dashboard"
    const val MAP = "map"
    const val TAG_TREE = "tag_tree/{lat}/{lng}"
    const val REPORT_PIT = "report_pit/{lat}/{lng}"
    const val SPECIES_GUIDE = "species"
    const val SPECIES_DETAIL = "species/{id}"
    const val COMMUNITY = "community"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
    const val SETTINGS = "settings"

    fun tagTree(lat: Double, lng: Double) = "tag_tree/$lat/$lng"
    fun reportPit(lat: Double, lng: Double) = "report_pit/$lat/$lng"
    fun speciesDetail(id: String) = "species/$id"
}
