package me.gingerninja.authenticator.di.module;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.gingerninja.authenticator.data.db.dao.AccountDao;
import me.gingerninja.authenticator.data.db.dao.LabelDao;
import me.gingerninja.authenticator.data.db.dao.impl.AccountDaoImpl;
import me.gingerninja.authenticator.data.db.dao.impl.LabelDaoImpl;
import me.gingerninja.authenticator.data.db.provider.DatabaseHandler;

@Module
public class DataModule {
    @Provides
    @Singleton
    public DatabaseHandler provideDatabaseHandler(Context context) {
        return new DatabaseHandler(context);
    }

    @Provides
    @Singleton
    public AccountDao provideAccountDao(DatabaseHandler databaseHandler) {
        return new AccountDaoImpl(databaseHandler);
    }

    @Provides
    @Singleton
    public LabelDao provideCategoryDao(DatabaseHandler databaseHandler) {
        return new LabelDaoImpl(databaseHandler);
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }
}
