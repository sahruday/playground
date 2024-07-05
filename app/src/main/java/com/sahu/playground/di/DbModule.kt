package com.sahu.playground.di

import android.content.Context
import androidx.room.Room
import com.sahu.playground.data.cache.AppDatabase
import com.sahu.playground.data.cache.dao.TableDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun provideDB(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "Playground.db").build()

    @Singleton
    @Provides
    fun provideTableDao(database: AppDatabase): TableDao = database.tableDao()
}