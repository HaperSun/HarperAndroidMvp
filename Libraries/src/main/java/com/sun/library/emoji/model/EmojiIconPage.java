package com.sun.library.emoji.model;

import androidx.annotation.DrawableRes;


/**
 * @author: Harper
 * @date: 2022/6/24
 * @note:
 */
public class EmojiIconPage {
    @EmojiIcon.Type
    private int type;
    private EmojiIcon[] data;
    private boolean useSystemDefaults;
    private
    @DrawableRes
    int icon;

    public EmojiIconPage(@EmojiIcon.Type int type, EmojiIcon[] data, boolean useSystemDefaults, int icon) {
        this.type = type;
        this.data = data;
        this.useSystemDefaults = useSystemDefaults;
        this.icon = icon;
    }

    @EmojiIcon.Type
    public int getType() {
        return type;
    }

    public boolean isUseSystemDefaults() {
        return useSystemDefaults;
    }

    public EmojiIcon[] getData() {
        return data;
    }

    public int getIcon() {
        return icon;
    }
}
