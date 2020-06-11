package com.appabilities.sold.utils;

import android.content.Context;

import com.appabilities.sold.networking.response.ProductResponse;
import com.iamhabib.easy_preference.EasyPreference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hamza on 8/21/2017.
 */

public class AppConstants {

    public static class HTTP{
        public static String BASE_URL = "https://www.pocket-shops.com/";
        public static String TEST_PAYMENT_URL = "https://test.oppwa.com/";
        public static String PAYMENT_URL = "https://oppwa.com/";
    }

    public static class PAYMENT_KEYS_TEST{
        public static String USER_ID = "8a8294185edcfa7c015edd0d728a006c";
        public static String PASSWORD = "qtJyH3qhsR";
        public static String ENTITY_ID = "8a8294185edcfa7c015edd0fadae0084";
        public static String CURRENCY = "SAR";
        public static String PAYMENT_TYPE = "DB";
        public static String SHOPPER_RESULT_URL = "https://www.pocket-shops.com/paymentStatus.php";
        public static String NOTIFICATION_URL = "https://www.example.com/notify";
        public static String PAYMENT_STATUS_CHECK = "https://test.oppwa.com/";
        public static String TEST_MODE = "EXTERNAL";


    }

    public static class PAYMENT_KEYS{
        public static String USER_ID = "8ac9a4c86759b5a301675b1da6db1147";
        public static String PASSWORD = "wasTpHgb2P";
        public static String ENTITY_ID = "8ac9a4cc6759b5a501675b1f02310e5a";
        public static String CURRENCY = "SAR";
        public static String PAYMENT_TYPE = "DB";
        public static String SHOPPER_RESULT_URL = "https://www.pocket-shops.com/paymentStatus.php";
        public static String NOTIFICATION_URL = "https://www.example.com/notify";
        public static String TEST_MODE = "EXTERNAL";

    }

    public static class VALUES
    {
        public static final String FOLLOWING = "following";
        public static final String FOLLOWER = "follower ";

        public static final String STATUS_FOLLOW = "1";
        public static final String STATUS_UNFOLLOW = "0";

        public static final String PRIVACY_POLICY = "https://pocket-shops.com/app/privacy_policy.html";
        public static final String REFUND_POLICY = "https://pocket-shops.com/app/refund_policy.html";
        public static final String TERMS_CONDITIONS = "https://pocket-shops.com/app/terms_conditions.html";

    }

    public enum USER_TYPE
    {
        NONE,
        FOLLOWER,
        FOLLOWING
    }

    public enum AUCTION_STATUS
    {
        UNKNOWN,
        OPEN,
        EXPIRED,
        AWARDED
    }

    public static class BUNDLE_KEYS
    {
        public static final String KEY_PRODUCTS_MODE = "key_my_products";
        public static final String EDIT_MODE = "edit_mode";
        public static final String KEY_USER_TYPE = "key_user_type";
    }

    public static enum KEYS{
        PRODUCT_ITEM,
        TRANSITION_VIEW,
        CATEGORY_ITEM,
        SUB_CATEGORY_ITEM,
        CATEGORY_ID,
        CATEGORY_NAME,
        IS_FROM_CATEGORY,
        SEARCH_MODEL,
        IS_FROM_AUCTION,
        ORDER_ITEM,
        SALE_ITEM,
        IS_REFRESH,
        ON_NOTIFICATION,
        BIDS_ITEM,
        ADVERT_ITEM,
        PRODUCT_ID,
        REQUEST_ITEM,
        OFFER_ITEM,
        IS_FROM_MY_REQUEST,
        SELLER_ID,
        REG_ID,
        EDIT_MODE,
        CHAT_METADATA,
        PRIVACY_POLICY,
        REFUND_POLICY,
        BIDDER,
        TERMS_CONDITIONS;

    }


    public static AUCTION_STATUS getAuctionStatus(ProductResponse productModel)
    {
        AppConstants.AUCTION_STATUS status = AppConstants.AUCTION_STATUS.OPEN;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date strDate = null;

        try {
            strDate = sdf.parse(productModel.auctionExpDate);
            if (new Date().after(strDate) && productModel.aucted == 0) {
                // Expired
                status = AppConstants.AUCTION_STATUS.EXPIRED;
                return status;
            }
            else
            {
                // Not Expired
                if (productModel.aucted == 1)
                {
                    // Awarded
                    status = AppConstants.AUCTION_STATUS.AWARDED;
                    return status;
                }
                else {
                    // Open for Bids
                    status = AppConstants.AUCTION_STATUS.OPEN;
                    return status;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return AppConstants.AUCTION_STATUS.UNKNOWN;
        }
    }

    public static String dateDiff(String productServerDate){
        Date productDate = setDateParsing(productServerDate);
        long diffInMillisec = new Date().getTime() - productDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
        long seconds = diffInSec % 60;
        diffInSec/= 60;
        long minutes =diffInSec % 60;
        diffInSec /= 60;
        long hours = diffInSec % 24;
        diffInSec /= 24;
        long days = diffInSec;

        if (days > 0){
            return days +" day ago";
        }else if (hours > 0){
            return hours +" hour ago";
        }else if (minutes > 0){
            return minutes +" minute ago";
        }
        return "0 minute ago";
    }

    public static Date setDateParsing(String date) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);
        Date datee = new Date();
        try {
            datee = (Date)format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datee;
    }


    public synchronized static String regId(Context context) {
        return EasyPreference.with(context).getString(KEYS.REG_ID.name(),null);
    }
}
