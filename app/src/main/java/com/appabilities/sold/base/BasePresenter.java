package com.appabilities.sold.base;



import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * Created by Admin on 6/12/2017.
 */

public abstract class BasePresenter<T> {

    protected T _view;

    protected BasePresenter(T view)
    {
        attachView(view);
    }

    /**
     * Call this method in {Activity#onCreate}
     * or {Fragment#onAttach(Context)} to attach the MVP View object
     */
    @CallSuper
    private void attachView(@NonNull T view) {
        _view = view;
    }

    /**
     * Call this method in {Activity#onDestroy()}
     * or {Fragment#onDetach()} to detach the MVP View object
     */
    @CallSuper
    public void detachView() {
        _view = null;
    }

    /**
     * Check if the view is attached.
     * This checking is only necessary when returning from an asynchronous call
     */
    protected final boolean isViewAttached() {
        return _view != null;
    }


}
