package com.z99a.zarifboyevjavohir.vikipediya.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.ApiIsExistedInWikiData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WikidataRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class EnglishRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UzbekRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RussianRetrofit

    private const val WIKIDATA_BASE_URL = "https://www.wikidata.org/w/"
    private const val WIKIPEDIA_BASE_URL_TEMPLATE = "https://%s.wikipedia.org/w/"

    @Provides
    @Singleton
    @WikidataRetrofit
    fun provideWikidataRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WIKIDATA_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    @EnglishRetrofit
    fun provideEnglishRetrofit(okHttpClient: OkHttpClient,moshi: Moshi): Retrofit {
        val baseUrl = String.format(WIKIPEDIA_BASE_URL_TEMPLATE, "en")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @UzbekRetrofit
    fun provideUzbekRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        val baseUrl = String.format(WIKIPEDIA_BASE_URL_TEMPLATE, "uz")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @RussianRetrofit
    fun provideRussianRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        val baseUrl = String.format(WIKIPEDIA_BASE_URL_TEMPLATE, "ru")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }



    @Provides
    @Singleton
    fun provideApiIsExistedInWikiData(@WikidataRetrofit retrofit: Retrofit): ApiIsExistedInWikiData {
        return retrofit.create(ApiIsExistedInWikiData::class.java)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(3))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .sslSocketFactory(getUnsafeOkHttpClient().socketFactory, provideX509TrustManager())
            .build()
    }

    class RetryInterceptor(private val maxRetries: Int) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var response: Response
            var tryCount = 0
            val request = chain.request()

            while (true) {
                try {
                    response = chain.proceed(request)
                    if (response.isSuccessful) return response
                } catch (e: Exception) {
                    if (tryCount >= maxRetries) throw e
                    tryCount++
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideX509TrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        }
    }

    private fun getUnsafeOkHttpClient(): SSLContext {
        val trustAllCerts = arrayOf<TrustManager>(
            provideX509TrustManager()
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        return sslContext
    }
}
