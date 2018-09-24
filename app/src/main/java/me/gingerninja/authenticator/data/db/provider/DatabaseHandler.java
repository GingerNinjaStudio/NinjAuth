package me.gingerninja.authenticator.data.db.provider;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import io.requery.Persistable;
import io.requery.android.sqlcipher.SqlCipherDatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import me.gingerninja.authenticator.BuildConfig;
import me.gingerninja.authenticator.data.db.DbConstants;
import me.gingerninja.authenticator.data.db.entity.Models;

public class DatabaseHandler {
    private final Context context;

    private SqlCipherDatabaseSource databaseSource;
    private ReactiveEntityStore<Persistable> entityStore;

    public DatabaseHandler(Context context) {
        this.context = context;
    }

    public void openDatabase(String password) {
        ReactiveEntityStore<Persistable> store;
        // override onUpgrade to handle migrating to a new version
        SqlCipherDatabaseSource source = new SqlCipherDatabaseSource(context, Models.DEFAULT, DbConstants.NAME, password, DbConstants.VERSION) {
            @Override
            public void onOpen(SQLiteDatabase db) {
                super.onOpen(db);
                // enabling foreign key constraints
                db.execSQL("PRAGMA foreign_keys = ON");
            }

            @Override
            protected void onConfigure(ConfigurationBuilder builder) {
                super.onConfigure(builder);
                builder.setQuoteColumnNames(true);
                builder.setQuoteTableNames(true);
            }
        };

        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }

        Configuration configuration = source.getConfiguration();
        store = ReactiveSupport.toReactiveStore(new EntityDataStore<>(configuration));

        this.databaseSource = source;
        this.entityStore = store;
    }

    public void changePassword(String newPassword) {
        if (databaseSource != null) {
            databaseSource.getWritableDatabase().changePassword(newPassword);
        }
    }

    public ReactiveEntityStore<Persistable> getEntityStore() {
        return entityStore;
    }
}
