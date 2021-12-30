package com.sun.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * @author: Harper
 * @date:   2021/12/30
 * @note:
 */
@SuppressLint("AppCompatCustomView")
public class EnterEditText extends EditText {
    public EnterEditText(Context context) {
        super(context);
    }

    public EnterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnterEditText(Context context, AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EnterEditText(Context context, AttributeSet attrs, int
            defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        if(inputConnection != null){
            outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        return inputConnection;
    }
}
