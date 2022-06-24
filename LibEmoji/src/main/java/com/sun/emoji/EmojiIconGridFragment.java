package com.sun.emoji;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.sun.emoji.model.EmojiIcon;
import com.sun.emoji.model.People;

/**
 * @author: Harper
 * @date: 2022/6/24
 * @note:
 */
public class EmojiIconGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    private OnEmojiIconClickedListener mOnEmojiIconClickedListener;
    private EmojiIconRecents mRecents;
    private EmojiIcon[] mEmojiIcons;
    private
    @EmojiIcon.Type
    int mEmojiconType;
    private boolean mUseSystemDefault = false;

    private static final String ARG_USE_SYSTEM_DEFAULTS = "useSystemDefaults";
    private static final String ARG_EMOJICONS = "emojicons";
    private static final String ARG_EMOJICON_TYPE = "emojiconType";

    protected static EmojiIconGridFragment newInstance(EmojiIcon[] emojiIcons, EmojiIconRecents recents) {
        return newInstance(EmojiIcon.TYPE_UNDEFINED, emojiIcons, recents, false);
    }

    protected static EmojiIconGridFragment newInstance(
            @EmojiIcon.Type int type, EmojiIconRecents recents, boolean useSystemDefault) {
        return newInstance(type, null, recents, useSystemDefault);
    }

    protected static EmojiIconGridFragment newInstance(
            @EmojiIcon.Type int type, EmojiIcon[] emojiIcons, EmojiIconRecents recents, boolean useSystemDefault) {
        EmojiIconGridFragment emojiGridFragment = new EmojiIconGridFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EMOJICON_TYPE, type);
        args.putParcelableArray(ARG_EMOJICONS, emojiIcons);
        args.putBoolean(ARG_USE_SYSTEM_DEFAULTS, useSystemDefault);
        emojiGridFragment.setArguments(args);
        emojiGridFragment.setRecents(recents);
        return emojiGridFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emojicon_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        GridView gridView = (GridView) view.findViewById(R.id.Emoji_GridView);
        Bundle bundle = getArguments();
        if (bundle == null) {
            mEmojiconType = EmojiIcon.TYPE_UNDEFINED;
            mEmojiIcons = People.DATA;
            mUseSystemDefault = false;
        } else {
            //noinspection WrongConstant
            mEmojiconType = bundle.getInt(ARG_EMOJICON_TYPE);
            if (mEmojiconType == EmojiIcon.TYPE_UNDEFINED) {
                Parcelable[] parcels = bundle.getParcelableArray(ARG_EMOJICONS);
                mEmojiIcons = new EmojiIcon[parcels.length];
                for (int i = 0; i < parcels.length; i++) {
                    mEmojiIcons[i] = (EmojiIcon) parcels[i];
                }
            } else {
                mEmojiIcons = EmojiIcon.getEmojicons(mEmojiconType);
            }
            mUseSystemDefault = bundle.getBoolean(ARG_USE_SYSTEM_DEFAULTS);
        }
        gridView.setAdapter(new EmojiIconAdapter(view.getContext(), mEmojiIcons, mUseSystemDefault));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(ARG_EMOJICONS, mEmojiIcons);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEmojiIconClickedListener) {
            mOnEmojiIconClickedListener = (OnEmojiIconClickedListener) context;
        } else if (getParentFragment() instanceof OnEmojiIconClickedListener) {
            mOnEmojiIconClickedListener = (OnEmojiIconClickedListener) getParentFragment();
        } else {
            throw new IllegalArgumentException(context + " must implement interface " + OnEmojiIconClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        mOnEmojiIconClickedListener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnEmojiIconClickedListener != null) {
            mOnEmojiIconClickedListener.onEmojiconClicked((EmojiIcon) parent.getItemAtPosition(position));
        }
        if (mRecents != null) {
            mRecents.addRecentEmoji(view.getContext(), ((EmojiIcon) parent.getItemAtPosition(position)));
        }
    }

    private void setRecents(EmojiIconRecents recents) {
        mRecents = recents;
    }

    public interface OnEmojiIconClickedListener {
        void onEmojiconClicked(EmojiIcon emojiIcon);
    }
}
