package com.sun.library.emoji;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sun.library.R;
import com.sun.library.emoji.model.EmojiIcon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/24
 * @note:
 */
public class EmojiIconGridView extends GridView implements AdapterView.OnItemClickListener {
    @EmojiIcon.Type
    private int mType;
    private EmojiIcon[] mData;
    private boolean mUseSystemDefaults;
    private EmojiIconGridFragment.OnEmojiIconClickedListener onEmojiIconClickedListener;
    private EmojiIconAdapter mEmojiAdapter;
    private List<EmojiIcon> mEmojiList;

    public EmojiIconGridView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.emojiconGridViewStyle);
        setOnItemClickListener(this);
        setSaveEnabled(true);
        mEmojiList = new ArrayList<>();
        mEmojiAdapter = new EmojiIconAdapter(context, mEmojiList);
        setAdapter(mEmojiAdapter);
    }

    public EmojiIconGridView(Context context) {
        this(context, null);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.data = mData;
        ss.type = mType;
        ss.useSystemDefaults = mUseSystemDefaults;
        ss.scrollX = getScrollX();
        ss.scrollY = getScrollY();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mType = ss.type;
        mData = ss.data;
        mUseSystemDefaults = ss.useSystemDefaults;
        setScrollX(ss.scrollX);
        setScrollY(ss.scrollY);
        setEmojiData(mType, mData, mUseSystemDefaults);
    }

    public void setEmojiData(@EmojiIcon.Type int type, EmojiIcon[] data, boolean useSystemDefaults) {
        this.mType = type;
        if (this.mType != EmojiIcon.TYPE_UNDEFINED) {
            this.mData = EmojiIcon.getEmojicons(type);
        } else {
            this.mData = data;
        }
        this.mUseSystemDefaults = useSystemDefaults;
        if (this.mData == null) {
            mEmojiList.clear();
        } else {
            mEmojiList.clear();
            Collections.addAll(mEmojiList, this.mData);
        }
        mEmojiAdapter.notifyDataSetChanged();
    }

    public void setOnEmojiconClickedListener(EmojiIconGridFragment.OnEmojiIconClickedListener onEmojiIconClickedListener) {
        this.onEmojiIconClickedListener = onEmojiIconClickedListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onEmojiIconClickedListener != null) {
            onEmojiIconClickedListener.onEmojiconClicked((EmojiIcon) parent.getItemAtPosition(position));
        }
    }

    public static class SavedState extends BaseSavedState {
        @EmojiIcon.Type
        int type;
        EmojiIcon[] data;
        boolean useSystemDefaults;
        int scrollX;
        int scrollY;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            //noinspection WrongConstant
            this.type = in.readInt();
            this.data = (EmojiIcon[]) in.readParcelableArray(EmojiIcon.class.getClassLoader());
            this.useSystemDefaults = in.readInt() != 0;
            this.scrollX = in.readInt();
            this.scrollY = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.type);
            out.writeParcelableArray(data, flags);
            out.writeInt(this.useSystemDefaults ? 1 : 0);
            out.writeInt(this.scrollX);
            out.writeInt(this.scrollY);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
