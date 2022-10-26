package com.sun.library.emoji;

import android.content.Context;

import com.sun.library.emoji.model.EmojiIcon;


/**
 * @author Daniele Ricci
 */
public interface EmojiIconRecents {
    void addRecentEmoji(Context context, EmojiIcon emojiIcon);
}
