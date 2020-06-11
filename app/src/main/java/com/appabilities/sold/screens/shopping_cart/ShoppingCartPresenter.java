package com.appabilities.sold.screens.shopping_cart;

import android.view.View;

import androidx.annotation.Nullable;

import com.appabilities.sold.base.BasePresenter;
import com.appabilities.sold.database.tables.CartItemModel;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;

/**
 * Created by Hamza on 10/12/2017.
 */
public class ShoppingCartPresenter extends BasePresenter<ShoppingCartContract.View> implements ShoppingCartContract.Actions {

    public ShoppingCartPresenter(ShoppingCartContract.View view) {
        super(view);
    }

    public void initScreen() {
        _view.setupViews();
    }

    @Override
    public void getCartItems() {
        SQLite.select().from(CartItemModel.class)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<CartItemModel>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @Nullable List<CartItemModel> tResult) {
                        _view.onRequestSuccessful(tResult);
                    }
                })
                .execute();
    }
}