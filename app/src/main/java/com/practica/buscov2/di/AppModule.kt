package com.practica.buscov2.di

import android.content.Context
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.util.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    @Named("apiBusco")
    fun providesRetrofitBusco(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesApi(@Named("apiBusco") retrofit: Retrofit): ApiBusco {
        return retrofit.create(ApiBusco::class.java)
    }

    @Singleton
    @Provides
    fun provideStoreToken(@ApplicationContext context: Context): StoreToken {
        return StoreToken(context)
    }
}