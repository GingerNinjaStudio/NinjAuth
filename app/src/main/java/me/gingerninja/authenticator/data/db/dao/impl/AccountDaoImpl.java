package me.gingerninja.authenticator.data.db.dao.impl;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import me.gingerninja.authenticator.data.db.dao.AccountDao;
import me.gingerninja.authenticator.data.db.entity.Account;
import me.gingerninja.authenticator.data.db.provider.DatabaseHandler;

public class AccountDaoImpl implements AccountDao {
    private final DatabaseHandler databaseHandler;

    public AccountDaoImpl(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    private ReactiveEntityStore<Persistable> getStore() {
        return databaseHandler.getEntityStore();
    }

    @Override
    public Observable<List<Account>> getAll() {
        return getStore()
                .select(Account.class)
                .orderBy(Account.POSITION.asc())
                .get()
                .observableResult()
                .map(accounts -> accounts.observable().toList().blockingGet());
    }

    @Override
    public Single<Account> save(Account account) {
        return getStore().upsert(account);
    }

    @Override
    public Completable delete(Account account) {
        return getStore().delete(account);
    }
}