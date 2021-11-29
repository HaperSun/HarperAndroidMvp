package com.sun.base.ui;


import com.sun.base.presenter.BasePresenter;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 在构建presenter的时候，自动添加到view层
 */

public interface IAddPresenterView extends IBaseView {

    void addPresenter(BasePresenter presenter);
}
