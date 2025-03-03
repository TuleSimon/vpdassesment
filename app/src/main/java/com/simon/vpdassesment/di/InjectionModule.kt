package com.simon.vpdassesment.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.simon.vpdassesment.core.database.AccountsDao
import com.simon.vpdassesment.core.database.TransactionsDao
import com.simon.vpdassesment.core.database.TransactionsDatabase
import com.simon.vpdassesment.data.datasource.AccountDataSource
import com.simon.vpdassesment.data.datasource.AccountDataSourceImpl
import com.simon.vpdassesment.data.datasource.AuthDatasource
import com.simon.vpdassesment.data.datasource.AuthDatasourceImpl
import com.simon.vpdassesment.data.repositories.AccountRepository
import com.simon.vpdassesment.data.repositories.AccountRepositoryImpl
import com.simon.vpdassesment.data.repositories.AuthRepository
import com.simon.vpdassesment.data.repositories.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object InjectionModule {

    @Provides
    @Singleton
    fun provideAuthDatasource(
        dao:AccountsDao
    ): AuthDatasource {
        return AuthDatasourceImpl(dao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(datasource: AuthDatasource): AuthRepository {
        return AuthRepositoryImpl(datasource)
    }


    @Provides
    @Singleton
    fun provideAccountRepository(datasource: AccountDataSource): AccountRepository {
        return AccountRepositoryImpl(datasource)
    }

    @Provides
    @Singleton
    fun providesTransactionDatabase(@ApplicationContext context: Context): TransactionsDatabase {
        return Room.databaseBuilder(
            context,
            TransactionsDatabase::class.java, "transactions.db",
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesTransactionsDao(transactionsDatabase: TransactionsDatabase): TransactionsDao {
        return (transactionsDatabase.transactionDao())
    }

    @Provides
    @Singleton
    fun provideAccountsDao(transactionsDatabase: TransactionsDatabase): AccountsDao {
        return (transactionsDatabase.accountDao())
    }


    @Provides
    @Singleton
    fun provideAccountDatasource(
        dao: TransactionsDao,
        accountsDao: AccountsDao,
    ): AccountDataSource {
        return AccountDataSourceImpl(dao, accountsDao)
    }


}
