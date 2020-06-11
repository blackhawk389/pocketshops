package com.appabilities.sold.screens.my_account.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.appabilities.sold.R;
import com.appabilities.sold.base.sidemenu.SideMenuBaseActivity;
import com.appabilities.sold.databinding.ActivityMyAccountBinding;
import com.appabilities.sold.model.RevenueModel;
import com.appabilities.sold.networking.response.BaseResponse;
import com.appabilities.sold.screens.home.view.HomeActivity;
import com.appabilities.sold.screens.my_account.MyAccountContract;
import com.appabilities.sold.screens.my_account.MyAccountPresenter;
import com.appabilities.sold.utils.SnackUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyAccountActivity extends SideMenuBaseActivity implements MyAccountContract.View{

    ActivityMyAccountBinding bi;
    MyAccountPresenter presenter;
    int month = 0;
    int year = 0;
    String date = "";
    RevenueModel revenueModel;
    boolean from_notification = false;
    private float available_amount;
    private String formatter_balance;
    private float total_deducted_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = (ActivityMyAccountBinding) setContentLayout(R.layout.activity_my_account);
        bi.setView(this);
        presenter = new MyAccountPresenter(this);
        bi.setPresenter(presenter);

        if(getIntent().getExtras() != null){
            from_notification = getIntent().getExtras().getBoolean("from_notification");
        }

        presenter.initScreen();
    }

    @Override
    public void setupViews() {
        initDrawer(bi.toolbar, "My Revenue");
        final String[] monthList = getResources().getStringArray(R.array.month_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, monthList);
        bi.txtMonthList.setAdapter(adapter);

        final String[] yearList = getResources().getStringArray(R.array.year_list);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, yearList);
        bi.txtYearList.setAdapter(adapterYear);

        month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        year = Calendar.getInstance().get(Calendar.YEAR);

        bi.txtMonthList.setText(String.valueOf(month));
        //bi.txtYearList.setText(String.valueOf(year));

        bi.txtMonthList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                month = Integer.parseInt(monthList[position]);
                date = month +"-"+ year;
                presenter.getRevenues(date, loginResponse.access_token);
            }
        });

        bi.txtYearList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                year = Integer.parseInt(yearList[position]);
                date = month +"-"+ year;
                presenter.getRevenues(date, loginResponse.access_token);
            }
        });


        bi.btnNextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.btnNextRegister.startAnimation();
                if(revenueModel != null){
                    presenter.requestWithdrawl(loginResponse.access_token, total_deducted_amount);
                }

            }
        });

        date = month+"-"+year;
        //presenter.getRevenues(date, loginResponse.access_token);
        presenter.getRevenues("", loginResponse.access_token);
    }

    @Override
    public void onClickMonth() {
        bi.txtMonthList.showDropDown();
    }

    @Override
    public void onClickYear() {
        bi.txtYearList.showDropDown();
    }

    @Override
    public void onSuccess(RevenueModel body) {
        revenueModel = body;
        bi.setRevenueModel(body);
        bi.executePendingBindings();



        available_amount = Float.parseFloat(body.availableAmount.replace(",", ""));
        float total_amount = Float.parseFloat(body.totalEarning.replace(",", ""));
        float dedected_amount = Float.parseFloat(body.deductedCommission.replace(",", ""));
        float requested_amount = Float.parseFloat(body.requested_amount.replace(",", ""));
        float received_amount = Float.parseFloat(body.received_amount.replace(",", ""));



        total_deducted_amount = (total_amount - dedected_amount) - received_amount;
        DecimalFormat formatter = new DecimalFormat("#,###,###.00");

        formatter_balance = formatter.format(total_deducted_amount);
//        String received = formatter.format(received_amount);
//        String requested= formatter.format(requested_amount);



        bi.totalEarning.setText(formatter_balance);
        bi.availableAmount.setText(formatter_balance);
        bi.requestedAmount.setText(body.requested_amount);
        bi.receivedAmount.setText(body.requested_amount);
        bi.nonAvailableAmount.setText(body.requested_amount);

        if(requested_amount < total_deducted_amount){
            bi.btnNextRegister.setVisibility(View.VISIBLE);
        }else if(requested_amount == total_deducted_amount){
            bi.btnNextRegister.setEnabled(false);
            bi.btnNextRegister.setVisibility(View.GONE);
        }

//        if(available_amount == 0.0 || body.availableAmount.equalsIgnoreCase("")){
//            bi.btnNextRegister.setEnabled(false);
//            bi.btnNextRegister.setVisibility(View.GONE);
//           // bi.btnNextRegister.setBackgroundColor(getColor(R.color.gray));
//        }else{
//            bi.btnNextRegister.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onSuccessWithdrawl(BaseResponse body) {
        bi.btnNextRegister.revertAnimation();
        Toast.makeText(this, body.msg, Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 700);

    }

    @Override
    public void onFailure() {
        SnackUtils.showSnackMessage(MyAccountActivity.this, "No Revenue Found", false);
        bi.requestedAmount.setText("00");
        bi.receivedAmount.setText("00");
        bi.btnNextRegister.setVisibility(View.GONE);
    }

    @Override
    public void onFailureWithdrawl() {
        SnackUtils.showSnackMessage(MyAccountActivity.this, "Couldn't submitted withdrawl request!", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectMenuOption(R.id.nav_my_revenue);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(from_notification){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            finish();
        }
    }
}
