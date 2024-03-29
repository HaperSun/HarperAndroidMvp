package com.sun.demo2.fragment;


import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.demo2.R;
import com.sun.demo2.databinding.FragmentCircleTurntableBinding;
import com.sun.library.luck.turntable.i.RotateListener;
import com.sun.library.luck.turntable.view.LuckDrawView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Harper
 * @date 2022/5/31
 * note:
 */
public class CircleTurntableFragment extends BaseMvpFragment<FragmentCircleTurntableBinding> {

    public static CircleTurntableFragment getInstance() {
        CircleTurntableFragment fragment = new CircleTurntableFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_circle_turntable;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        vdb.circleTurntableView.initData(2, 6);
        vdb.circleTurntableView.setOnResultListener(position -> showToast("您抽中的奖品等级是：" + position));

        vdb.luckDrawView1.setRotateListener(new RotateListener() {
            @Override
            public void rotateEnd(int position, String des) {
                showToast("结束了 " + position + "   " + des);
            }

            @Override
            public void rotating(ValueAnimator valueAnimator) {

            }

            @Override
            public void rotateBefore(ImageView goImg) {
                int position = new Random().nextInt(7) + 1;
                vdb.luckDrawView1.startRotate(position);
            }
        });

        vdb.luckDrawView2.setRotateListener(new RotateListener() {
            @Override
            public void rotateEnd(int position, String des) {
                showToast("结束了 位置：" + position + "   描述：" + des);
            }

            @Override
            public void rotating(ValueAnimator valueAnimator) {

            }

            @Override
            public void rotateBefore(ImageView goImg) {
                //模拟位置
                int position = new Random().nextInt(7) + 1;
                vdb.luckDrawView2.startRotate(position);
            }
        });
        setThird();
    }

    private void setThird() {
        /**
         * 新增使用代码设置属性的方式
         *
         * 请注意：
         *  使用这种方式需要在引入布局文件的时候在布局文件中设置mTypeNums = -1 来告诉我你现在要用代码传入这些属性
         *  使用这种方式需要在引入布局文件的时候在布局文件中设置mTypeNums = -1 来告诉我你现在要用代码传入这些属性
         *  使用这种方式需要在引入布局文件的时候在布局文件中设置mTypeNums = -1 来告诉我你现在要用代码传入这些属性
         *
         *  重要的事情说三遍
         *
         *  例如
         *  <com.cretin.www.wheelsruflibrary.view.WheelSurfView
         *      android:id="@+id/wheelSurfView2"
         *      android:layout_width="match_parent"
         *      android:layout_height="match_parent"
         *      wheelSurfView:typenum="-1"
         *      android:layout_margin="20dp">
         *
         *  然后调用setConfig()方法来设置你的属性
         *
         * 请注意：
         *  你在传入所有的图标文件之后需要调用 WheelSurfView.rotateBitmaps() 此方法来处理一下你传入的图片
         *  你在传入所有的图标文件之后需要调用 WheelSurfView.rotateBitmaps() 此方法来处理一下你传入的图片
         *  你在传入所有的图标文件之后需要调用 WheelSurfView.rotateBitmaps() 此方法来处理一下你传入的图片
         *
         *  重要的事情说三遍
         *
         * 请注意：
         *  .setmColors(colors)
         *  .setmDeses(des)
         *  .setmIcons(mListBitmap)
         *  这三个方法中的参数长度必须一致 否则会报运行时异常
         */
        //颜色
        Integer[] colors = new Integer[]{Color.parseColor("#fef9f7"), Color.parseColor("#fbc6a9")
                , Color.parseColor("#ffdecc"), Color.parseColor("#fbc6a9")
                , Color.parseColor("#ffdecc"), Color.parseColor("#fbc6a9")
                , Color.parseColor("#ffdecc")};
        //文字
        String[] des = new String[]{"王 者 皮 肤", "1 8 0 积 分", "L O L 皮 肤", "谢 谢 参 与", "2 8 积 分", "微 信 红 包", "5 Q 币"};
        //图标
        List<Bitmap> mListBitmap = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            mListBitmap.add(BitmapFactory.decodeResource(getResources(), R.mipmap.iphone));
        }
        //主动旋转一下图片
        mListBitmap = LuckDrawView.rotateBitmaps(mListBitmap);

        //获取第三个视图
        LuckDrawView.Builder build = new LuckDrawView.Builder()
                .setColors(colors)
                .setDeses(des)
                .setIcons(mListBitmap)
                .setType(1)
                .setTypeNum(7)
                .build();
        vdb.luckDrawView3.setConfig(build);
        vdb.luckDrawView3.setRotateListener(new RotateListener() {
            @Override
            public void rotateEnd(int position, String des) {
                showToast("结束了 位置：" + position + "   描述：" + des);
            }

            @Override
            public void rotating(ValueAnimator valueAnimator) {

            }

            @Override
            public void rotateBefore(ImageView goImg) {
                //模拟位置
                int position = new Random().nextInt(7) + 1;
                vdb.luckDrawView3.startRotate(position);
            }
        });
    }

}
