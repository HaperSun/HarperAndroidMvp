package com.sun.emoji.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.DynamicDrawableSpan;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: Harper
 * @date: 2022/6/24
 * @note:
 */
public class EmojiIcon implements Parcelable {
    @IntDef({DynamicDrawableSpan.ALIGN_BASELINE, DynamicDrawableSpan.ALIGN_BOTTOM})
    public @interface Alignment {
    }

    @IntDef({TYPE_UNDEFINED, TYPE_PEOPLE, TYPE_NATURE, TYPE_OBJECTS, TYPE_PLACES, TYPE_SYMBOLS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_PEOPLE = 1;
    public static final int TYPE_NATURE = 2;
    public static final int TYPE_OBJECTS = 3;
    public static final int TYPE_PLACES = 4;
    public static final int TYPE_SYMBOLS = 5;

    public static EmojiIcon[] getEmojicons(@Type int type) {
        switch (type) {
            case TYPE_PEOPLE:
                return People.DATA;
            case TYPE_NATURE:
                return Nature.DATA;
            case TYPE_OBJECTS:
                return Objects.DATA;
            case TYPE_PLACES:
                return Places.DATA;
            case TYPE_SYMBOLS:
                return Symbols.DATA;
        }
        throw new IllegalArgumentException("Invalid emojicon type: " + type);
    }

    public static final Creator<EmojiIcon> CREATOR = new Creator<EmojiIcon>() {
        @Override
        public EmojiIcon createFromParcel(Parcel in) {
            return new EmojiIcon(in);
        }

        @Override
        public EmojiIcon[] newArray(int size) {
            return new EmojiIcon[size];
        }
    };

    private int icon;

    private char value;

    private String emoji;

    public EmojiIcon(int icon, char value, String emoji) {
        this.icon = icon;
        this.value = value;
        this.emoji = emoji;
    }

    public EmojiIcon(Parcel in) {
        this.icon = in.readInt();
        this.value = (char) in.readInt();
        this.emoji = in.readString();
    }

    private EmojiIcon() {
    }

    public EmojiIcon(String emoji) {
        this.emoji = emoji;
    }

    public static EmojiIcon fromResource(int icon, int value) {
        EmojiIcon emoji = new EmojiIcon();
        emoji.icon = icon;
        emoji.value = (char) value;
        return emoji;
    }

    public static EmojiIcon fromCodePoint(int codePoint) {
        EmojiIcon emoji = new EmojiIcon();
        emoji.emoji = newString(codePoint);
        return emoji;
    }

    public static EmojiIcon fromChar(char ch) {
        EmojiIcon emoji = new EmojiIcon();
        emoji.emoji = Character.toString(ch);
        return emoji;
    }

    public static EmojiIcon fromChars(String chars) {
        EmojiIcon emoji = new EmojiIcon();
        emoji.emoji = chars;
        return emoji;
    }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeInt(value);
        dest.writeString(emoji);
    }

    public char getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EmojiIcon && emoji.equals(((EmojiIcon) o).emoji);
    }

    @Override
    public int hashCode() {
        return emoji.hashCode();
    }

}
