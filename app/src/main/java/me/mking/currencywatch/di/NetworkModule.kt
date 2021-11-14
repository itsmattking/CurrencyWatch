package me.mking.currencywatch.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @ExperimentalSerializationApi
    @Provides
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.exchangerate.host/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    fun okhttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor()
                .apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
    }
}