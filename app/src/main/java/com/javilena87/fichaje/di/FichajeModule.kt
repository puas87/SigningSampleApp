package com.javilena87.fichaje.di

import com.javilena87.fichaje.data.DefaultDataSource
import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import com.javilena87.fichaje.domain.HolidayRepository
import com.javilena87.fichaje.data.db.HolidaysDataSource
import com.javilena87.fichaje.data.repository.FichajeRepositoryImpl
import com.javilena87.fichaje.data.repository.FichajeSharedPrefsRepositoryImpl
import com.javilena87.fichaje.data.repository.HolidayRepositoryImpl
import com.javilena87.fichaje.domain.usecases.GetUserRememberedUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class HolidaysSource

@Qualifier
annotation class DatabaseSource

@Qualifier
annotation class RemoteSource

@Qualifier
annotation class PreferencesSource

@InstallIn(SingletonComponent::class)
@Module
abstract class FichajeModule {

    @HolidaysSource
    @Singleton
    @Binds
    abstract fun bindsHolidaySource(impl: HolidaysDataSource): DefaultDataSource

    @DatabaseSource
    @Singleton
    @Binds
    abstract fun bindsHolidayRepository(impl: HolidayRepositoryImpl): HolidayRepository

    @RemoteSource
    @Singleton
    @Binds
    abstract fun bindsFichajeRepository(impl: FichajeRepositoryImpl): FichajeRepository

    @PreferencesSource
    @Singleton
    @Binds
    abstract fun bindsFichajePrefs(impl: FichajeSharedPrefsRepositoryImpl): FichajeSharedPrefsRepository

}