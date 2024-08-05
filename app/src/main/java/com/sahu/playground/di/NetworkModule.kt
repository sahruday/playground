package com.sahu.playground.di

import android.content.res.Resources
import com.google.gson.GsonBuilder
import com.sahu.playground.BuildConfig
//import com.sahu.playground.appUtil.unsafeOkHttpClient
import com.sahu.playground.data.remte.api.Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFilmService(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mpedaabd2792d6312acc.free.beeceptor.com")//TODO Base url
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit) = retrofit.create(Api::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(resources: Resources): OkHttpClient {
//        return (if(BuildConfig.DEBUG) unsafeOkHttpClient else OkHttpClient())
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(createAuthInterceptor(resources))
            .build()
    }

    private fun createAuthInterceptor(resources: Resources): Interceptor {
        return Interceptor { chain ->
            //TODO add token
            val updatedUrl = chain.request().url
//                .newBuilder()
//                .build()
            chain.proceed(
                chain.request().newBuilder()
                    .url(updatedUrl)
                    //If header add here.
                    .build()
            )
        }
    }
}