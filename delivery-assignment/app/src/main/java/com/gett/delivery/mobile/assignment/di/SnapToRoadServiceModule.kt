package com.gett.delivery.mobile.assignment.di

import com.gett.delivery.mobile.assignment.data.DirectionsService
import com.gett.delivery.mobile.assignment.data.NetworkService
import com.gett.delivery.mobile.assignment.data.SnapToRoadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object SnapToRoadServiceModule {
    @Provides
    fun provideSnapToRoadService(): SnapToRoadService = NetworkService.roadApiRetrofit.create(SnapToRoadService::class.java)
}