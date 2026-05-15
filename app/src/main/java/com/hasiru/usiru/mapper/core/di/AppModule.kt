package com.hasiru.usiru.mapper.core.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.hasiru.usiru.mapper.BuildConfig
import com.hasiru.usiru.mapper.data.ai.GeminiSpeciesService
import com.hasiru.usiru.mapper.data.local.HasiruDatabase
import com.hasiru.usiru.mapper.data.remote.api.HasiruApiService
import com.hasiru.usiru.mapper.data.repository.AuthRepositoryImpl
import com.hasiru.usiru.mapper.data.repository.DashboardRepositoryImpl
import com.hasiru.usiru.mapper.data.repository.PitRepositoryImpl
import com.hasiru.usiru.mapper.data.repository.SpeciesRepositoryImpl
import com.hasiru.usiru.mapper.data.repository.TreeRepositoryImpl
import com.hasiru.usiru.mapper.domain.ai.SpeciesIdentificationService
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.domain.repository.DashboardRepository
import com.hasiru.usiru.mapper.domain.repository.PitRepository
import com.hasiru.usiru.mapper.domain.repository.SpeciesRepository
import com.hasiru.usiru.mapper.domain.repository.TreeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides @Singleton
    fun provideMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HasiruDatabase =
        Room.databaseBuilder(context, HasiruDatabase::class.java, "hasiru_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else HttpLoggingInterceptor.Level.NONE
            }
        )
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): HasiruApiService =
        retrofit.create(HasiruApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindAuth(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindTree(impl: TreeRepositoryImpl): TreeRepository
    @Binds @Singleton abstract fun bindPit(impl: PitRepositoryImpl): PitRepository
    @Binds @Singleton abstract fun bindSpecies(impl: SpeciesRepositoryImpl): SpeciesRepository
    @Binds @Singleton abstract fun bindDashboard(impl: DashboardRepositoryImpl): DashboardRepository
    @Binds @Singleton abstract fun bindAi(impl: GeminiSpeciesService): SpeciesIdentificationService
}
