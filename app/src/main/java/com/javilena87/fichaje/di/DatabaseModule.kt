package com.javilena87.fichaje.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.javilena87.fichaje.data.db.FICHAJE_DATABASE_NAME
import com.javilena87.fichaje.data.db.FichajeDatabase
import com.javilena87.fichaje.data.db.HolidayRegDao
import com.javilena87.fichaje.data.db.firebase.FirebaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): FichajeDatabase {
        return Room.databaseBuilder(
            context,
            FichajeDatabase::class.java,
            FICHAJE_DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideHolidayRegDao(fichajeDb: FichajeDatabase): HolidayRegDao {
        return fichajeDb.getHolidayRegDao()
    }

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): DatabaseReference {
        return Firebase.database.reference
    }

    @Provides
    fun providesFirebaseDao(firebaseReference: DatabaseReference): FirebaseDataSource {
        return FirebaseDataSource(firebaseReference)
    }
}