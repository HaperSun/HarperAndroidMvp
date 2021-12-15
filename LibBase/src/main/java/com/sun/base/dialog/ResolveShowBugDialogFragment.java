package com.sun.base.dialog;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 解决调用show方法Caused by: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState的bug
 * * 参考链接：https://stackoverflow.com/questions/14262312/java-lang-illegalstateexception-can-not-perform-this-action-after-onsaveinstanc
 */
public class ResolveShowBugDialogFragment extends DialogFragment {

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    boolean mIsStateAlreadySaved = false;
    boolean mPendingShowDialog = false;

    @Override
    public void onResume() {
        onResumeFragments();
        super.onResume();
    }

    public void onResumeFragments() {
        mIsStateAlreadySaved = false;
        if (mPendingShowDialog) {
            mPendingShowDialog = false;
            showSnoozeDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsStateAlreadySaved = true;
    }

    private void showSnoozeDialog() {
        if (mIsStateAlreadySaved) {
            mPendingShowDialog = true;
        } else {
            FragmentManager fm = getFragmentManager();
            ResolveShowBugDialogFragment snoozeDialog = new ResolveShowBugDialogFragment();
            snoozeDialog.show(fm, "BaseDialogFragment");
        }
    }
}
