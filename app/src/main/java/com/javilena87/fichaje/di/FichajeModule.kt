package com.javilena87.fichaje.di

import com.javilena87.fichaje.data.DefaultDataSource
import com.javilena87.fichaje.data.db.HolidaysDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class HolidaysSource

@InstallIn(SingletonComponent::class)
@Module
abstract class FichajeModule {

    @HolidaysSource
    @Singleton
    @Binds
    abstract fun bindsHolidayRepository(impl: HolidaysDataSource): DefaultDataSource

}