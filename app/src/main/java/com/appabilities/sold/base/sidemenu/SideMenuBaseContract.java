package com.appabilities.sold.base.sidemenu;

import com.appabilities.sold.networking.response.LoginResponse;

/**
 * Created by Wajahat Karim on 4/23/2017.
 */
public interface SideMenuBaseContract {

    public interface View {
        public void launchCategoriesActivity();
        public void launchMyAccountActivity();
        public void launchMyProfileActivity();
        public void launchAdHistoryActivity();
        public void launchMySaleActivity();
        public void launchSettingsActivity();
        public void launchMyOrdersActivity();
        public void launchMyAuctionsActivity();
        public void launchMyRequestActivity();
        public void launchSellerActivity();
        void launchChatListActivity();

        public void updateHeader(String name, String email, String userImg);
    }

    public interface Actions {

        public void updateHeaderData(LoginResponse loginResponse);

    }

    public interface Repository {

    }
}