package com.sun.emoji;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.sun.emoji.model.EmojiIcon;


/**
 * @author: Harper
 * @date: 2022/6/24
 * @note:
 */
public class EmojiIconRecentGridFragment extends EmojiIconGridFragment implements EmojiIconRecents {

    private EmojiIconAdapter mAdapter;
    private boolean mUseSystemDefault = false;

    private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";

    protected static EmojiIconRecentGridFragment newInstance() {
        return newInstance(false);
    }

    protected static EmojiIconRecentGridFragment newInstance(boolean useSystemDefault) {
        EmojiIconRecentGridFragment fragment = new EmojiIconRecentGridFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(USE_SYSTEM_DEFAULT_KEY, useSystemDefault);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUseSystemDefault = getArguments().getBoolean(USE_SYSTEM_DEFAULT_KEY);
        } else {
            mUseSystemDefault = false;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        EmojiIconRecentsManager recents = EmojiIconRecentsManager
                .getInstance(view.getContext());

        mAdapter = new EmojiIconAdapter(view.getContext(), recents, mUseSystemDefault);
        GridView gridView = (GridView) view.findViewById(R.id.Emoji_GridView);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
    }

    @Override
    public void addRecentEmoji(Context context, EmojiIcon emojiIcon) {
        EmojiIconRecentsManager recents = EmojiIconRecentsManager
                .getInstance(context);
        recents.push(emojiIcon);

        // notify dataset changed
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

}
