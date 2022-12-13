package com.sun.base.base.iview;


import com.sun.base.presenter.BasePresenter;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 在构建presenter的时候，自动添加到view层
 */
public interface IPresenterView extends IBaseView {

    /**
     * 将presenter加入map中，方便在OnDestroy中操作
     *
     * @param presenter presenter
     */
    void addPresenter(BasePresenter<? extends IBaseView> presenter);
}
