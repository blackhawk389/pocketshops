package com.appabilities.sold.networking;

import com.appabilities.sold.MyApp;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.networking.services.AdvertisementService;
import com.appabilities.sold.networking.services.CategoriesService;
import com.appabilities.sold.networking.services.OrderService;
import com.appabilities.sold.networking.services.PaymentService;
import com.appabilities.sold.networking.services.ProductService;
import com.appabilities.sold.networking.services.RequestService;
import com.appabilities.sold.networking.services.UserService;
import com.appabilities.sold.utils.AppConstants;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * Created by Wajahat on 8/9/2016.
 */

public class NetController {

    private static NetController ourInstance = new NetController();
    private Retrofit mRetrofit;
    private Retrofit mPaymentRetrofit;
    private OkHttpClient okHttpClient;

    private UserService mUserService;
    private CategoriesService mCategoriesService;
    private AdvertisementService mAdService;
    private ProductService mProductService;
    private OrderService mOrderService;
    private PaymentService mPaymentService;
    private RequestService mRequestService;

    public static NetController getInstance() {
        return ourInstance;
    }

    private NetController() {

    }

    public Retrofit getRetrofit()
    {
        if (mRetrofit == null)
        {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.HTTP.BASE_URL)
                    .client(getHttpClient())
                    //.addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return mRetrofit;
    }

    public Retrofit getRetrofitForPayment()
    {
        if (mPaymentRetrofit == null)
        {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            mPaymentRetrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.HTTP.PAYMENT_URL)
                    .client(getHttpClient())
                    //.addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return mPaymentRetrofit;
    }

    public static BaseResponse getError(ResponseBody body)
    {
        Converter<ResponseBody, BaseResponse> converter = NetController.getInstance().getRetrofit().responseBodyConverter(BaseResponse.class, new Annotation[0]);
        BaseResponse error = null;
        try {
            error = converter.convert(body);
            if (error.msg == null) {
                error.msg = "Unknown Error Occured!";
            }
            else {
                error.msg = error.msg.replace("<p>", "");
                error.msg = error.msg.replace("</p>", "");
            }
            return error;
        } catch (IOException e) {
            e.printStackTrace();
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.msg = e.getMessage();
            return baseResponse;
        }
    }

    public OkHttpClient getHttpClient()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        if (okHttpClient == null)
        {
            okHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .addInterceptor(interceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);

                            if (response.code() == HTTP_BAD_REQUEST)
                            {
                                if (MyApp.getCurrentActivity() instanceof BaseActivity){
                                    BaseActivity baseActivity = (BaseActivity) MyApp.getCurrentActivity();
                                    baseActivity.logoutApp();
                                }
                                return response;
                            }
                            return response;
                        }
                    })
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    public UserService getUserService()
    {
        if (mUserService == null)
        {
            mUserService = getRetrofit().create(UserService.class);
        }
        return mUserService;
    }

    public CategoriesService getCategoriesService()
    {
        if (mCategoriesService == null)
        {
            mCategoriesService = getRetrofit().create(CategoriesService.class);
        }
        return mCategoriesService;
    }

    public AdvertisementService getAdvertisementService(){
        if (mAdService == null){
            mAdService = getRetrofit().create(AdvertisementService.class);
        }
        return mAdService;
    }

    public ProductService getProductService(){
        if (mProductService == null){
            mProductService = getRetrofit().create(ProductService.class);
        }
        return mProductService;
    }

    public OrderService getOrderService(){
        if (mOrderService == null){
            mOrderService = getRetrofit().create(OrderService.class);
        }
        return mOrderService;
    }

    public PaymentService getPaymentService(){
        if (mPaymentService == null){
            mPaymentService = getRetrofitForPayment().create(PaymentService.class);
        }
        return mPaymentService;
    }

    public PaymentService getPaymentStatusService(){
        if (mPaymentService == null){
            mPaymentService = getRetrofitForPayment().create(PaymentService.class);
        }
        return mPaymentService;
    }

    public RequestService getWithdrawlService(){
        if (mRequestService == null){
            mRequestService = getRetrofit().create(RequestService.class);
        }
        return mRequestService;
    }

    public RequestService getRequestService(){
        if (mRequestService == null){
            mRequestService = getRetrofit().create(RequestService.class);
        }
        return  mRequestService;
    }
}
