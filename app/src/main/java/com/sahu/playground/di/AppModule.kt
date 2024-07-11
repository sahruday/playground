package com.sahu.playground.di

import android.content.Context
import android.content.res.Resources
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sahu.playground.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideResource(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): Context = context.applicationContext

    @Provides
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


    @Provides
//    fun provideRingtone(@ApplicationContext context: Context): Ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
    fun provideRingtone(@ApplicationContext context: Context): Ringtone = RingtoneManager.getRingtone(context, Uri.parse("android.resource://" + context.packageName + "/" + R.raw.dialing_tone))
}