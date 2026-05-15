package com.hasiru.usiru.mapper.data.remote.api

import retrofit2.http.*

interface HasiruApiService {
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Map<String, Any>

    @GET("trees")
    suspend fun getTrees(@Query("city") city: String?): List<Map<String, Any>>

    @POST("trees")
    suspend fun createTree(@Body body: Map<String, Any>): Map<String, Any>

    @GET("pits")
    suspend fun getPits(@Query("city") city: String?): List<Map<String, Any>>

    @GET("analytics/oxygen")
    suspend fun getOxygenAnalytics(@Query("city") city: String): Map<String, Any>

    @GET("species")
    suspend fun getSpecies(): List<Map<String, Any>>

    @GET("leaderboard")
    suspend fun getLeaderboard(@Query("city") city: String): List<Map<String, Any>>

    @GET("admin/stats")
    suspend fun getAdminStats(): Map<String, Any>
}
