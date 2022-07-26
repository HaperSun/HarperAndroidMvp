package com.sun.base.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Harper
 * @date:   2021/12/30
 * @note: 过滤emoji表情输入
 */
public class EmojiFilter implements InputFilter {

    private static Set<String> filterSet = null;

    private static void addUnicodeRangeToSet(Set<String> set, int start, int end) {
        if (set == null) {
            return;
        }
        if (start > end) {
            return;
        }

        for (int i = start; i <= end; i++) {
            filterSet.add(new String(new int[]{
                    i
            }, 0, 1));
        }
    }

    private static void addUnicodeRangeToSet(Set<String> set, int code) {
        if (set == null) {
            return;
        }
        filterSet.add(new String(new int[]{code}, 0, 1));
    }

    static {
        filterSet = new HashSet<String>();

        // See http://apps.timwhitlock.info/emoji/tables/unicode

        //http://www.unicode.org/emoji/charts/full-emoji-list.html

        // Emoticons ( 1F601 - 1F64F )
        addUnicodeRangeToSet(filterSet, 0x1F601, 0X1F606);
        addUnicodeRangeToSet(filterSet, 0x1F609, 0X1F60D);
        addUnicodeRangeToSet(filterSet, 0x1F60F);
        addUnicodeRangeToSet(filterSet, 0x1F612, 0X1F614);
        addUnicodeRangeToSet(filterSet, 0x1F616);
        addUnicodeRangeToSet(filterSet, 0x1F618);
        addUnicodeRangeToSet(filterSet, 0x1F61A);
        addUnicodeRangeToSet(filterSet, 0x1F61C, 0X1F61E);
        addUnicodeRangeToSet(filterSet, 0x1F620, 0X1F625);
        addUnicodeRangeToSet(filterSet, 0x1F628, 0X1F62B);
        addUnicodeRangeToSet(filterSet, 0x1F62D);
        addUnicodeRangeToSet(filterSet, 0x1F630, 0X1F633);
        addUnicodeRangeToSet(filterSet, 0x1F635);
        addUnicodeRangeToSet(filterSet, 0x1F637, 0X1F640);
        addUnicodeRangeToSet(filterSet, 0x1F645, 0X1F64F);

        // Dingbats ( 2702 - 27B0 )
        addUnicodeRangeToSet(filterSet, 0x2702);
        addUnicodeRangeToSet(filterSet, 0x2705);
        addUnicodeRangeToSet(filterSet, 0x2708, 0x270C);
        addUnicodeRangeToSet(filterSet, 0x270F);
        addUnicodeRangeToSet(filterSet, 0x2712);
        addUnicodeRangeToSet(filterSet, 0x2714);
        addUnicodeRangeToSet(filterSet, 0x2716);
        addUnicodeRangeToSet(filterSet, 0x2728);
        addUnicodeRangeToSet(filterSet, 0x2733, 0x2734);
        addUnicodeRangeToSet(filterSet, 0x2744);
        addUnicodeRangeToSet(filterSet, 0x2747);
        addUnicodeRangeToSet(filterSet, 0x274C);
        addUnicodeRangeToSet(filterSet, 0x274E);
        addUnicodeRangeToSet(filterSet, 0x2753, 0x2755);
        addUnicodeRangeToSet(filterSet, 0x2757);
        addUnicodeRangeToSet(filterSet, 0x2764);
        addUnicodeRangeToSet(filterSet, 0x2795, 0x2797);
        addUnicodeRangeToSet(filterSet, 0x27A1);
        addUnicodeRangeToSet(filterSet, 0X27B0);

        // Transport and map symbols ( 1F680 - 1F6C0 )
        addUnicodeRangeToSet(filterSet, 0X1F680);
        addUnicodeRangeToSet(filterSet, 0X1F683, 0X1F685);
        addUnicodeRangeToSet(filterSet, 0X1F687);
        addUnicodeRangeToSet(filterSet, 0X1F689);
        addUnicodeRangeToSet(filterSet, 0X1F68C);
        addUnicodeRangeToSet(filterSet, 0X1F68F);
        addUnicodeRangeToSet(filterSet, 0X1F691, 0X1F693);
        addUnicodeRangeToSet(filterSet, 0X1F695);
        addUnicodeRangeToSet(filterSet, 0X1F697);
        addUnicodeRangeToSet(filterSet, 0X1F699, 0X1F69A);
        addUnicodeRangeToSet(filterSet, 0X1F6A2);
        addUnicodeRangeToSet(filterSet, 0X1F6A4, 0X1F6A5);
        addUnicodeRangeToSet(filterSet, 0X1F6A7, 0X1F6AD);
        addUnicodeRangeToSet(filterSet, 0X1F6B2);
        addUnicodeRangeToSet(filterSet, 0X1F6B6);
        addUnicodeRangeToSet(filterSet, 0X1F6B9, 0X1F6BE);
        addUnicodeRangeToSet(filterSet, 0X1F6C0);

        // Enclosed characters ( 24C2 - 1F251 )
        addUnicodeRangeToSet(filterSet, 0X24C2);
        addUnicodeRangeToSet(filterSet, 0x1F170, 0x1F171);
        addUnicodeRangeToSet(filterSet, 0x1F17E, 0x1F17F);
        addUnicodeRangeToSet(filterSet, 0x1F18E);
        addUnicodeRangeToSet(filterSet, 0x1F191, 0x1F19A);
        addUnicodeRangeToSet(filterSet, 0x1F1E7, 0x1F1EC);
        addUnicodeRangeToSet(filterSet, 0x1F1EE, 0x1F1F0);
        addUnicodeRangeToSet(filterSet, 0x1F1F3);
        addUnicodeRangeToSet(filterSet, 0x1F1F5);
        addUnicodeRangeToSet(filterSet, 0x1F1F7, 0x1F1FA);
        addUnicodeRangeToSet(filterSet, 0x1F201, 0x1F202);
        addUnicodeRangeToSet(filterSet, 0x1F21A);
        addUnicodeRangeToSet(filterSet, 0x1F22F);
        addUnicodeRangeToSet(filterSet, 0x1F232, 0x1F23A);
        addUnicodeRangeToSet(filterSet, 0X1F250, 0X1F251);

        // Additional emoticons ( 1F600 - 1F636 )
        addUnicodeRangeToSet(filterSet, 0X1F600);
        addUnicodeRangeToSet(filterSet, 0X1F607, 0X1F608);
        addUnicodeRangeToSet(filterSet, 0X1F60E);
        addUnicodeRangeToSet(filterSet, 0X1F610, 0X1F611);
        addUnicodeRangeToSet(filterSet, 0X1F615);
        addUnicodeRangeToSet(filterSet, 0X1F617);
        addUnicodeRangeToSet(filterSet, 0X1F619);
        addUnicodeRangeToSet(filterSet, 0X1F61B);
        addUnicodeRangeToSet(filterSet, 0X1F61F);
        addUnicodeRangeToSet(filterSet, 0X1F626, 0X1F627);
        addUnicodeRangeToSet(filterSet, 0X1F62C);
        addUnicodeRangeToSet(filterSet, 0X1F62E, 0X1F62F);
        addUnicodeRangeToSet(filterSet, 0X1F634);
        addUnicodeRangeToSet(filterSet, 0X1F636);

        // Additional transport and map symbols ( 1F681 - 1F6C5 )
        addUnicodeRangeToSet(filterSet, 0X1F681, 0X1F682);
        addUnicodeRangeToSet(filterSet, 0X1F686);
        addUnicodeRangeToSet(filterSet, 0X1F688);
        addUnicodeRangeToSet(filterSet, 0X1F68A);
        addUnicodeRangeToSet(filterSet, 0X1F68D, 0X1F68E);
        addUnicodeRangeToSet(filterSet, 0X1F690);
        addUnicodeRangeToSet(filterSet, 0X1F694);
        addUnicodeRangeToSet(filterSet, 0X1F696);
        addUnicodeRangeToSet(filterSet, 0X1F698);
        addUnicodeRangeToSet(filterSet, 0X1F69B, 0X1F6A1);
        addUnicodeRangeToSet(filterSet, 0X1F6A3);
        addUnicodeRangeToSet(filterSet, 0X1F6A6);
        addUnicodeRangeToSet(filterSet, 0X1F6AE, 0X1F6B1);
        addUnicodeRangeToSet(filterSet, 0X1F6B3, 0X1F6B5);
        addUnicodeRangeToSet(filterSet, 0X1F6B7, 0X1F6B8);
        addUnicodeRangeToSet(filterSet, 0X1F6BF);
        addUnicodeRangeToSet(filterSet, 0X1F6C1, 0X1F6C5);

        // Other additional symbols ( 1F30D - 1F567 )
        addUnicodeRangeToSet(filterSet, 0X1F30D, 0X1F30E);
        addUnicodeRangeToSet(filterSet, 0X1F310);
        addUnicodeRangeToSet(filterSet, 0X1F312);
        addUnicodeRangeToSet(filterSet, 0X1F316, 0X1F318);
        addUnicodeRangeToSet(filterSet, 0X1F31A);
        addUnicodeRangeToSet(filterSet, 0X1F31C, 0X1F31E);
        addUnicodeRangeToSet(filterSet, 0X1F332, 0X1F333);
        addUnicodeRangeToSet(filterSet, 0X1F34B);
        addUnicodeRangeToSet(filterSet, 0X1F350);
        addUnicodeRangeToSet(filterSet, 0X1F37C);
        addUnicodeRangeToSet(filterSet, 0X1F3C7);
        addUnicodeRangeToSet(filterSet, 0X1F3C9);
        addUnicodeRangeToSet(filterSet, 0X1F3E4);
        addUnicodeRangeToSet(filterSet, 0X1F400, 0X1F40B);
        addUnicodeRangeToSet(filterSet, 0X1F40F, 0X1F410);
        addUnicodeRangeToSet(filterSet, 0X1F413);
        addUnicodeRangeToSet(filterSet, 0X1F415, 0X1F416);
        addUnicodeRangeToSet(filterSet, 0X1F42A);
        addUnicodeRangeToSet(filterSet, 0X1F465);
        addUnicodeRangeToSet(filterSet, 0X1F46C, 0X1F46D);
        addUnicodeRangeToSet(filterSet, 0X1F4AD);
        addUnicodeRangeToSet(filterSet, 0X1F4B6, 0X1F4B7);
        addUnicodeRangeToSet(filterSet, 0X1F4EC, 0X1F4EF);
        addUnicodeRangeToSet(filterSet, 0X1F4F5);
        addUnicodeRangeToSet(filterSet, 0X1F500, 0X1F502);
        addUnicodeRangeToSet(filterSet, 0X1F504, 0X1F507);
        addUnicodeRangeToSet(filterSet, 0X1F509);
        addUnicodeRangeToSet(filterSet, 0X1F515);
        addUnicodeRangeToSet(filterSet, 0X1F52C, 0X1F52D);
        addUnicodeRangeToSet(filterSet, 0X1F55C, 0X1F567);

        //5. Uncategorized
        addUnicodeRangeToSet(filterSet, 0X00A9);
        addUnicodeRangeToSet(filterSet, 0X00AE);
        addUnicodeRangeToSet(filterSet, 0X203C);
        addUnicodeRangeToSet(filterSet, 0X2049);
        /*addUnicodeRangeToSet(filterSet, 0X0030, 0X0039);*/ //数字不过滤
        addUnicodeRangeToSet(filterSet, 0X20E3);
        addUnicodeRangeToSet(filterSet, 0X2122);
        addUnicodeRangeToSet(filterSet, 0X2139);
        addUnicodeRangeToSet(filterSet, 0X2194, 0X2199);
        addUnicodeRangeToSet(filterSet, 0X21A9, 0X21AA);
        addUnicodeRangeToSet(filterSet, 0X231A, 0X231B);
        addUnicodeRangeToSet(filterSet, 0X23E9, 0X23EC);
        addUnicodeRangeToSet(filterSet, 0X23F0);
        addUnicodeRangeToSet(filterSet, 0X23F3);
        addUnicodeRangeToSet(filterSet, 0X25AA, 0X25AB);
        addUnicodeRangeToSet(filterSet, 0X25B6);
        addUnicodeRangeToSet(filterSet, 0X25C0);
        addUnicodeRangeToSet(filterSet, 0X25FB, 0X25FE);
        addUnicodeRangeToSet(filterSet, 0X2600, 0X2601);
        addUnicodeRangeToSet(filterSet, 0X260E);
        addUnicodeRangeToSet(filterSet, 0X2611);
        addUnicodeRangeToSet(filterSet, 0X2614, 0X2615);
        addUnicodeRangeToSet(filterSet, 0X261D);
        addUnicodeRangeToSet(filterSet, 0X263A);
        addUnicodeRangeToSet(filterSet, 0X2648, 0X2653);
        addUnicodeRangeToSet(filterSet, 0X2660);
        addUnicodeRangeToSet(filterSet, 0X2663);
        addUnicodeRangeToSet(filterSet, 0X2665, 0X2666);
        addUnicodeRangeToSet(filterSet, 0X2668);
        addUnicodeRangeToSet(filterSet, 0X267B);
        addUnicodeRangeToSet(filterSet, 0X267F);
        addUnicodeRangeToSet(filterSet, 0X2693);
        addUnicodeRangeToSet(filterSet, 0X26A0, 0X26A1);
        addUnicodeRangeToSet(filterSet, 0X26AA, 0X26AB);
        addUnicodeRangeToSet(filterSet, 0X26BD, 0X26BE);
        addUnicodeRangeToSet(filterSet, 0X26C4, 0X26C5);
        addUnicodeRangeToSet(filterSet, 0X26CE);
        addUnicodeRangeToSet(filterSet, 0X26D4);
        addUnicodeRangeToSet(filterSet, 0X26EA);
        addUnicodeRangeToSet(filterSet, 0X26F2, 0X26F3);
        addUnicodeRangeToSet(filterSet, 0X26F5);
        addUnicodeRangeToSet(filterSet, 0X26FA);
        addUnicodeRangeToSet(filterSet, 0X26FD);
        addUnicodeRangeToSet(filterSet, 0X2934, 0X2935);
        addUnicodeRangeToSet(filterSet, 0X2B05, 0X2B07);
        addUnicodeRangeToSet(filterSet, 0X2B1B, 0X2B1C);
        addUnicodeRangeToSet(filterSet, 0X2B50);
        addUnicodeRangeToSet(filterSet, 0X2B55);
        addUnicodeRangeToSet(filterSet, 0X3030);
        addUnicodeRangeToSet(filterSet, 0X303D);
        addUnicodeRangeToSet(filterSet, 0X3297);
        addUnicodeRangeToSet(filterSet, 0X3299);
        addUnicodeRangeToSet(filterSet, 0X1F004);
        addUnicodeRangeToSet(filterSet, 0X1F0CF);
        addUnicodeRangeToSet(filterSet, 0X1F300, 0X1F30C);
        addUnicodeRangeToSet(filterSet, 0X1F30F);
        addUnicodeRangeToSet(filterSet, 0X1F311);
        addUnicodeRangeToSet(filterSet, 0X1F313, 0X1F315);
        addUnicodeRangeToSet(filterSet, 0X1F319);
        addUnicodeRangeToSet(filterSet, 0X1F31B);
        addUnicodeRangeToSet(filterSet, 0X1F31F, 0X1F320);
        addUnicodeRangeToSet(filterSet, 0X1F330, 0X1F331);
        addUnicodeRangeToSet(filterSet, 0X1F334, 0X1F335);
        addUnicodeRangeToSet(filterSet, 0X1F337, 0X1F34A);
        addUnicodeRangeToSet(filterSet, 0X1F34C, 0X1F34F);
        addUnicodeRangeToSet(filterSet, 0X1F351, 0X1F37B);
        addUnicodeRangeToSet(filterSet, 0X1F380, 0X1F393);
        addUnicodeRangeToSet(filterSet, 0X1F3A0, 0X1F3C4);
        addUnicodeRangeToSet(filterSet, 0X1F3C6);
        addUnicodeRangeToSet(filterSet, 0X1F3C8);
        addUnicodeRangeToSet(filterSet, 0X1F3CA);
        addUnicodeRangeToSet(filterSet, 0X1F3E0, 0X1F3E3);
        addUnicodeRangeToSet(filterSet, 0X1F3E5, 0X1F3F0);
        addUnicodeRangeToSet(filterSet, 0X1F40C, 0X1F40E);
        addUnicodeRangeToSet(filterSet, 0X1F411, 0X1F412);
        addUnicodeRangeToSet(filterSet, 0X1F414);
        addUnicodeRangeToSet(filterSet, 0X1F417, 0X1F429);
        addUnicodeRangeToSet(filterSet, 0X1F42B, 0X1F43E);
        addUnicodeRangeToSet(filterSet, 0X1F440);
        addUnicodeRangeToSet(filterSet, 0X1F442, 0X1F464);
        addUnicodeRangeToSet(filterSet, 0X1F466, 0X1F46B);
        addUnicodeRangeToSet(filterSet, 0X1F46E, 0X1F4AC);
        addUnicodeRangeToSet(filterSet, 0X1F4AE, 0X1F4B5);
        addUnicodeRangeToSet(filterSet, 0X1F4B8, 0X1F4EB);
        addUnicodeRangeToSet(filterSet, 0X1F4EE, 0X1F4EE);
        addUnicodeRangeToSet(filterSet, 0X1F4F0, 0X1F4F4);
        addUnicodeRangeToSet(filterSet, 0X1F4F6, 0X1F4F7);
        addUnicodeRangeToSet(filterSet, 0X1F4F9, 0X1F4FC);
        addUnicodeRangeToSet(filterSet, 0X1F503);
        addUnicodeRangeToSet(filterSet, 0X1F50A, 0X1F514);
        addUnicodeRangeToSet(filterSet, 0X1F516, 0X1F52B);
        addUnicodeRangeToSet(filterSet, 0X1F52E, 0X1F53D);
        addUnicodeRangeToSet(filterSet, 0X1F550, 0X1F55B);
        addUnicodeRangeToSet(filterSet, 0X1F5FB, 0X1F5FF);
    }

    public EmojiFilter() {
        super();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                               int dend) {
        CharSequence destTest = source;
        // check black-list set
        if (filterSet.contains(source.toString())) {
            destTest = "";
        }
        // 过滤一些复制粘贴的输入
        int length = source.length();
        if (source.length() > 1) {//说明是复制粘贴的，不是手动输入的
            StringBuilder sourceFinalSb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                char charVal = source.charAt(i);
                if (!filterSet.contains(String.valueOf(charVal))
                        && !isEmoji(charVal)) {
                    sourceFinalSb.append(charVal);
                }
            }
            if (sourceFinalSb.length() > 0) {
                destTest = sourceFinalSb.toString();
            } else {
                destTest = "";
            }
        }
        //modify by xfchen 2019/1/16 修复特殊机型语音输入时重复的问题
        //返回charsequene就不会重复，返回string就会重复
        return TextUtils.equals(source, destTest) ? source : destTest;
    }

    public static boolean isEmoji(char codePoint) {
        if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
            return false;
        return true;
    }

}
