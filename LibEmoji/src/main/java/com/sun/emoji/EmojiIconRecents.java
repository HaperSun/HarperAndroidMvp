package com.sun.emoji;

import android.content.Context;

import com.sun.emoji.model.EmojiIcon;


/**
 * @author Daniele Ricci
 */
public interface EmojiIconRecents {
    void addRecentEmoji(Context context, EmojiIcon emojiIcon);
}
