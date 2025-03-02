package com.simon.vpdassesment.di

import com.simon.vpdassesment.data.datasource.AuthDatasource
import com.simon.vpdassesment.data.datasource.AuthDatasourceImpl
import com.simon.vpdassesment.data.repositories.AuthRepository
import com.simon.vpdassesment.data.repositories.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object InjectionModule {

    @Provides
    @Singleton
    fun provideAuthDatasource(): AuthDatasource {
        return AuthDatasourceImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(datasource: AuthDatasource): AuthRepository {
        return AuthRepositoryImpl(datasource)
    }
}