package com.appabilities.sold.database;

import com.appabilities.sold.database.tables.CartItemModel;
import com.appabilities.sold.database.tables.ProductImageModel;
import com.appabilities.sold.database.tables.RelationshipModel;
import com.appabilities.sold.database.tables.RelationshipModel_Table;
import com.appabilities.sold.model.UserModel;
import com.appabilities.sold.model.UserModel_Table;
import com.appabilities.sold.networking.response.LoginResponse;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by Hamza on 9/13/2017.
 */

@Database(name = SoldDatabase.NAME, version = SoldDatabase.VERSION)
public class SoldDatabase {
    public static final String NAME = "PocketShopDatabase";
    public static final int VERSION = 100;

    public static void deleteDatabase() {
        deleteAllLogTables();
    }

    public static void deleteAllLogTables() {
        Delete.tables(
                LoginResponse.class,
                RelationshipModel.class,
                UserModel.class,
                ProductImageModel.class,
                CartItemModel.class
        );
    }

    public static BaseModelQueriable<UserModel> getFollowersListQuery(String userId)
    {
        return SQLite.select().from(UserModel.class).where(UserModel_Table.userID.in(new Select(RelationshipModel_Table.followerId).from(RelationshipModel.class).where(RelationshipModel_Table.followingId.is(userId))));

    }

    public static List<UserModel> getFollowingList(String userId)
    {
        List<UserModel> list = SQLite.select().from(UserModel.class)
                .where(UserModel_Table.userID.in(
                        new Select(RelationshipModel_Table.followingId).from(RelationshipModel.class)
                                .where(RelationshipModel_Table.followerId.is(userId)))
                ).queryList();

        return list;
    }

    public static List<UserModel> getFollowersList(String userId)
    {
        List<UserModel> list = SQLite.select().from(UserModel.class).where(UserModel_Table.userID.in(new Select(RelationshipModel_Table.followerId).from(RelationshipModel.class).where(RelationshipModel_Table.followingId.is(userId)))).queryList();
        return list;
    }


    public static BaseModelQueriable<UserModel> getFollowingListQuery(String userId)
    {
        return SQLite.select().from(UserModel.class)
                .where(UserModel_Table.userID.in(
                        new Select(RelationshipModel_Table.followingId).from(RelationshipModel.class)
                                .where(RelationshipModel_Table.followerId.is(userId))));
    }

    public static UserModel getUser(String userID){
        return SQLite.select().from(UserModel.class).where(UserModel_Table.userID.eq(userID)).querySingle();
    }

    public static RelationshipModel getRelation(String id){
        return SQLite.select().from(RelationshipModel.class).where(RelationshipModel_Table.relationshipId.eq(id)).querySingle();
    }
}
