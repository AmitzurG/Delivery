package com.gett.delivery.mobile.assignment.di

import com.gett.delivery.mobile.assignment.data.DirectionsService
import com.gett.delivery.mobile.assignment.data.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object DirectionsServiceModule {
    @Provides
    fun provideDirectionsService(): DirectionsService = NetworkService.directionsApiRetrofit.create(DirectionsService::class.java)
}