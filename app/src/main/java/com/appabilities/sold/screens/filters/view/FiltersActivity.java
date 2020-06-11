package com.appabilities.sold.screens.filters.view;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.appabilities.sold.R;
import com.appabilities.sold.base.BaseActivity;
import com.appabilities.sold.base.BaseRecyclerAdapter;
import com.appabilities.sold.databinding.ActivityFiltersBinding;
import com.appabilities.sold.networking.response.CategoriesResponse;
import com.appabilities.sold.networking.response.SubCategoryResponse;
import com.appabilities.sold.screens.filters.FiltersContract;
import com.appabilities.sold.screens.filters.FiltersPresenter;
import com.appyvet.rangebar.RangeBar;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FiltersActivity extends BaseActivity implements FiltersContract.View {

    ActivityFiltersBinding bi;
    FiltersPresenter presenter;
    private LabelFlexAdapter sortByAdapter;
    private LabelFlexCategoryAdapter categoryAdapter;
    private LabelFlexSubCategoryAdapter subCategoryAdapter;
    private CategoriesResponse selectedCategory;
    private SubCategoryResponse selectedSubCategory;
    String selectedSort = "date", categoryID = "", subCategoryID = "";
    private int minPrice = 0, maxPrice = 1000, filtersCount = 0;
    private List<CategoriesResponse> categoryList;
    private List<SubCategoryResponse> subCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_filters);
        presenter = new FiltersPresenter(this);
        bi.setPresenter(presenter);
        bi.setView(this);
        presenter.initScreen();

        if (getIntent().hasExtra("category") && getIntent().hasExtra("subcategory") && getIntent().hasExtra("sort"))
        {
            categoryID = getIntent().getStringExtra("category");
            subCategoryID = getIntent().getStringExtra("subcategory");
            selectedSort = getIntent().getStringExtra("sort");
            minPrice = getIntent().getIntExtra("minPrice", 0);
            maxPrice = getIntent().getIntExtra("maxPrice", 1000);
            bi.rangePriceFilters.setRangePinsByValue(minPrice, maxPrice);
            sortByAdapter.selectItem(selectedSort);
        }
    }

    @Override
    public void setupViews()
    {
        // Toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FILTERS");

        categoryList = new ArrayList<>();
        categoryList.add(new CategoriesResponse("Any"));
        subCategoryList = new ArrayList<>();
        subCategoryList.add(new SubCategoryResponse("Any"));
        // Price Range
        bi.rangePriceFilters.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                bi.txtPriceRange.setText("SAR " + leftPinValue + " - " + "SAR " + rightPinValue);
                minPrice = Integer.valueOf(leftPinValue);
                maxPrice = Integer.valueOf(rightPinValue);
            }
        });
        bi.rangePriceFilters.setRangePinsByValue(minPrice, maxPrice);

        // Sort By
        final FlowLayoutManager sortLayoutManager = new FlowLayoutManager();
        sortLayoutManager.setAutoMeasureEnabled(true);
        bi.recyclerSortBy.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(5, 5, 5, 5);
            }
        });
        bi.recyclerSortBy.setLayoutManager(sortLayoutManager);
        sortByAdapter = new LabelFlexAdapter(R.layout.flex_label_item_layout, new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, ViewDataBinding binding, int position) {
                sortByAdapter.selectItem(position);
                selectedSort = sortByAdapter.getItemForServer(position);
            }
        });
        bi.recyclerSortBy.setAdapter(sortByAdapter);

        // Category
        FlowLayoutManager categoryLayoutManager = new FlowLayoutManager();
        categoryLayoutManager.setAutoMeasureEnabled(true);
        bi.recyclerCategory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(5, 5, 5, 5);
            }
        });
        bi.recyclerCategory.setLayoutManager(categoryLayoutManager);
        categoryAdapter = new LabelFlexCategoryAdapter(R.layout.flex_label_item_layout, new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, ViewDataBinding binding, int position) {
                categoryAdapter.selectItem(position);
                selectedCategory = categoryAdapter.getItem(position);
                subCategoryList.clear();
                selectedSubCategory = null;
                subCategoryList.add(new SubCategoryResponse("Any"));
                if (selectedCategory.subcategories != null)
                    subCategoryList.addAll(selectedCategory.subcategories);
                updateSubCategoryAdapter();
            }
        });
        bi.recyclerCategory.setAdapter(categoryAdapter);

        // Sub-Category
        FlowLayoutManager subCategoryLayoutManager = new FlowLayoutManager();
        subCategoryLayoutManager.setAutoMeasureEnabled(true);
        bi.recyclerSubCategory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(5, 5, 5, 5);
            }
        });
        bi.recyclerSubCategory.setLayoutManager(subCategoryLayoutManager);
        subCategoryAdapter = new LabelFlexSubCategoryAdapter(R.layout.flex_label_item_layout, new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, ViewDataBinding binding, int position) {
                subCategoryAdapter.selectItem(position);
                selectedSubCategory = subCategoryAdapter.getItem(position);
            }
        });
        bi.recyclerSubCategory.setAdapter(subCategoryAdapter);
    }

    @Override
    public void setSortFilters() {
        sortByAdapter.setItems(Arrays.asList(getResources().getStringArray(R.array.sort_type)));
        if (selectedSort.equals("rating")){
            sortByAdapter.selectItem(0);
        }else if (selectedSort.equals("date")){
            sortByAdapter.selectItem(1);
        }else if (selectedSort.equals("name")){
            sortByAdapter.selectItem(2);
        }
    }


    @Override
    public void btnApplyFiltersClick() {
        Intent ii = new Intent();
        ii.putExtra("selectedCategory", Parcels.wrap(selectedCategory));
        ii.putExtra("selectedSubCategory", Parcels.wrap(selectedSubCategory));
        ii.putExtra("selectedSort", selectedSort);
        ii.putExtra("minPrice", Integer.toString(minPrice));
        ii.putExtra("maxPrice", Integer.toString(maxPrice));
        setResult(RESULT_OK, ii);
        finish();
    }

    @Override
    public void onCategoryRequestSuccessful(List<CategoriesResponse> categoryListItem) {
        categoryList.addAll(categoryListItem);
        if (categoryID != null && !categoryID.equals("")){
            for (CategoriesResponse item :
                    categoryListItem) {
                if (item.categoryID.equals(categoryID)){
                    selectedCategory = item;
                    if (item.subcategories != null){
                        subCategoryList.clear();
                        subCategoryList.add(new SubCategoryResponse("Any"));
                        subCategoryList.addAll(item.subcategories);
                    }
                    if (subCategoryID != null && !subCategoryID.equals("")){
                        for (SubCategoryResponse innerItem :
                                item.subcategories) {
                            if (innerItem.categoryID.equals(subCategoryID)){
                                selectedSubCategory = innerItem;
                            }
                        }
                    }
                }

            }
        }
        updateCategoryAdapter();
        updateSubCategoryAdapter();
    }

    @Override
    public void onCategoryRequestNotFound() {
    }

    @Override
    public void onCategoryRequestFail(String s) {

    }

    @Override
    public void updateCategoryAdapter() {
        bi.recyclerCategory.setVisibility(View.VISIBLE);
        bi.progressCategory.setVisibility(View.GONE);
        categoryAdapter.setItems(categoryList);
        if (selectedCategory != null)
            categoryAdapter.selectItem(selectedCategory);

    }

    @Override
    public void updateSubCategoryAdapter() {
        bi.recyclerSubCategory.setVisibility(View.VISIBLE);
        bi.progressSubCategory.setVisibility(View.GONE);
        if (subCategoryList == null ||
                (subCategoryList.get(0).categoryName.equals("Any") && subCategoryList.size() <= 1)){
            subCategoryList = new ArrayList<>();
            subCategoryList.add(new SubCategoryResponse("None"));
        }
        subCategoryAdapter.setItems(subCategoryList);

        if (selectedSubCategory != null)
            subCategoryAdapter.selectItem(selectedSubCategory);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filters_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_CANCELED);
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_reset_filters:
                bi.rangePriceFilters.setRangePinsByValue(0, 1000);
                sortByAdapter.selectItem(0);
                categoryAdapter.selectItem(0);
                subCategoryAdapter.selectItem(0);
                minPrice = 0;
                maxPrice = 1000;
                //selectedCategory = selectedSubCategory = "Any";
                selectedSort = "date";
                selectedCategory = new CategoriesResponse("Any");
                selectedSubCategory = new SubCategoryResponse("Any");
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }
}
