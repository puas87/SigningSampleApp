package com.javilena87.fichaje.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val SHARED_FILE_NAME = "signing_app_secret_shared_prefs"

@InstallIn(SingletonComponent::class)
@Module
class SharedPreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext applicationContext: Context): SharedPreferences =
        EncryptedSharedPreferences.create(
            SHARED_FILE_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
}