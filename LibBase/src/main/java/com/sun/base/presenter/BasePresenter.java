package com.sun.base.presenter;

import com.sun.base.bean.StrongReference;
import com.sun.base.base.iview.IAddPresenterView;
/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public abstract class BasePresenter<T extends IAddPresenterView> {

    protected StrongReference<T> mView;

    public BasePresenter(T view) {
        mView = new StrongReference<T>(view);
        if (view != null) {
            view.addPresenter(this);
        }
    }

    public boolean isDetached() {
        return mView.get() == null;
    }

    public void detachView() {
        if (!isDetached()) {
            mView.clear();
        }
    }
}
