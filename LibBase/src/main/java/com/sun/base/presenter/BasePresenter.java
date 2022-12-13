package com.sun.base.presenter;

import com.sun.base.bean.StrongReference;
import com.sun.base.base.iview.IPresenterView;

import java.util.Objects;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public class BasePresenter<T extends IPresenterView> {

    protected StrongReference<T> mView;

    public BasePresenter(T view) {
        mView = new StrongReference<>(view);
        if (null != view) {
            view.addPresenter(this);
        }
    }

    public boolean isCreated() {
        return mView.get() != null;
    }

    public void clearView() {
        if (isCreated()) {
            mView.clear();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || getClass() != o.getClass()) {
            return false;
        }
        BasePresenter<?> that = (BasePresenter<?>) o;
        return Objects.equals(mView, that.mView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mView);
    }
}
