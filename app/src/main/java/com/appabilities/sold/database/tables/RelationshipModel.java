package com.appabilities.sold.database.tables;

import com.appabilities.sold.database.SoldDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Hamza on 9/13/2017.
 */

@Table(database = SoldDatabase.class)
public class RelationshipModel extends BaseModel {

    @Column
    @PrimaryKey
    public String relationshipId;
    /**
     * The User, who is following someone.
     */
    @Column public String followerId;
    /**
     * The user, who is being followed by someone (follower).
     */
    @Column public String followingId;

    @Column public String textValue;


}
