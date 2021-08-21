package com.gett.delivery.mobile.assignment.data

import com.gett.delivery.mobile.assignment.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object NetworkService {
    val directionsApiRetrofit: Retrofit = Retrofit.Builder().baseUrl(BuildConfig.DIRECTIONS_API_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    val roadApiRetrofit: Retrofit = Retrofit.Builder().baseUrl(BuildConfig.ROADS_API_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
}

interface DirectionsService {
    @GET("json?key=${BuildConfig.GOOGLE_KEY_VALUE}")
    suspend fun getDirectionObject(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String): DirectionObject

    @GET("json?key=${BuildConfig.GOOGLE_KEY_VALUE}")
    suspend fun getDirectionObject(
        @Query("origin") origin: String,
        @Query("destination") destination: String): DirectionObject
}

interface SnapToRoadService {
    @GET("snapToRoads?interpolate=true&key=${BuildConfig.GOOGLE_KEY_VALUE}")
    suspend fun getSnappedPoints(@Query("path") path: String): SnappedPointsObject
}



//F2:DE:1B:7A:6C:07:AE:91:9D:43:E7:DB:E6:4C:EB:5D:6B:CC:98:B9   (SHA-1)
