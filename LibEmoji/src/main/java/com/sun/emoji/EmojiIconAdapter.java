package com.sun.emoji;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sun.emoji.model.EmojiIcon;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/24
 * @note:
 */
class EmojiIconAdapter extends ArrayAdapter<EmojiIcon> {

    private boolean mUseSystemDefault = false;

    public EmojiIconAdapter(Context context, List<EmojiIcon> data) {
        super(context, R.layout.item_emojicon, data);
        mUseSystemDefault = false;
    }

    public EmojiIconAdapter(Context context, List<EmojiIcon> data, boolean useSystemDefault) {
        super(context, R.layout.item_emojicon, data);
        mUseSystemDefault = useSystemDefault;
    }

    public EmojiIconAdapter(Context context, EmojiIcon[] data) {
        super(context, R.layout.item_emojicon, data);
        mUseSystemDefault = false;
    }

    public EmojiIconAdapter(Context context, EmojiIcon[] data, boolean useSystemDefault) {
        super(context, R.layout.item_emojicon, data);
        mUseSystemDefault = useSystemDefault;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_emojicon, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (EmojiIconTextView) v.findViewById(R.id.emojicon_icon);
            holder.icon.setUseSystemDefault(mUseSystemDefault);
            v.setTag(holder);
        }
        EmojiIcon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(emoji.getEmoji());
        return v;
    }

    static class ViewHolder {
        EmojiIconTextView icon;
    }
}