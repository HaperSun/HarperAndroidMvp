package com.sun.luck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sun.luck.R;
import com.sun.luck.i.ItemView;


public class LuckyMonkeyPanelView extends FrameLayout {


    private ImageView bg_1;
    private ImageView bg_2;

    private LuckyMonkeyPanelItemView itemView1, itemView2, itemView3,
            itemView4, itemView6,
            itemView7, itemView8, itemView9;

    private ItemView[] itemViewArr = new ItemView[8];
    private int currentIndex = 0;
    private int currentTotal = 0;
    private int stayIndex = 0;

    private boolean isMarqueeRunning = false;
    private boolean isGameRunning = false;
    private boolean isTryToStop = false;

    private static final int DEFAULT_SPEED = 300;
    private static final int MIN_SPEED = 80;
    private int currentSpeed = DEFAULT_SPEED;

    LuckyMonkeyAnimationListener mListener;

    public LuckyMonkeyPanelView(@NonNull Context context) {
        this(context, null);
    }

    public LuckyMonkeyPanelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyMonkeyPanelView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_lucky_mokey_panel, this);
        setupView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startMarquee();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopMarquee();
        super.onDetachedFromWindow();
    }

    private void setupView() {
        bg_1 = (ImageView) findViewById(R.id.bg_1);
        bg_2 = (ImageView) findViewById(R.id.bg_2);
        itemView1 = (LuckyMonkeyPanelItemView) findViewById(R.id.item1);
        itemView2 = (LuckyMonkeyPanelItemView) findViewById(R.id.item2);
        itemView3 = (LuckyMonkeyPanelItemView) findViewById(R.id.item3);
        itemView4 = (LuckyMonkeyPanelItemView) findViewById(R.id.item4);
        itemView6 = (LuckyMonkeyPanelItemView) findViewById(R.id.item6);
        itemView7 = (LuckyMonkeyPanelItemView) findViewById(R.id.item7);
        itemView8 = (LuckyMonkeyPanelItemView) findViewById(R.id.item8);
        itemView9 = (LuckyMonkeyPanelItemView) findViewById(R.id.item9);

        itemViewArr[0] = itemView4;
        itemViewArr[1] = itemView1;
        itemViewArr[2] = itemView2;
        itemViewArr[3] = itemView3;
        itemViewArr[4] = itemView6;
        itemViewArr[5] = itemView9;
        itemViewArr[6] = itemView8;
        itemViewArr[7] = itemView7;
    }

    private void stopMarquee() {
        isMarqueeRunning = false;
        isGameRunning = false;
        isTryToStop = false;
    }

    private void startMarquee() {
        isMarqueeRunning = true;
        new Thread(() -> {
            while (isMarqueeRunning) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                post(() -> {
                    if (bg_1 != null && bg_2 != null) {
                        if (VISIBLE == bg_1.getVisibility()) {
                            bg_1.setVisibility(GONE);
                            bg_2.setVisibility(VISIBLE);
                        } else {
                            bg_1.setVisibility(VISIBLE);
                            bg_2.setVisibility(GONE);
                        }
                    }
                });
            }
        }).start();
    }

    private long getInterruptTime() {
        currentTotal++;
        if (isTryToStop) {
            currentSpeed += 20;
            if (currentSpeed > DEFAULT_SPEED) {
                currentSpeed = DEFAULT_SPEED;
            }
        } else {
            if (currentTotal / itemViewArr.length > 0) {
                currentSpeed -= 100;
            }
            if (currentSpeed < MIN_SPEED) {
                currentSpeed = MIN_SPEED;
            }
        }
        return currentSpeed;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void startGame() {
        isGameRunning = true;
        isTryToStop = false;
        currentSpeed = DEFAULT_SPEED;
        new Thread(() -> {
            while (isGameRunning) {
                try {
                    Thread.sleep(getInterruptTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                post(() -> {
                    int preIndex = currentIndex;
                    currentIndex++;
                    if (currentIndex >= itemViewArr.length) {
                        currentIndex = 0;
                    }

                    itemViewArr[preIndex].setFocus(false);
                    itemViewArr[currentIndex].setFocus(true);

                    if (isTryToStop && currentSpeed == DEFAULT_SPEED && stayIndex == currentIndex) {
                        isGameRunning = false;
                        if (mListener != null) {
                            mListener.onAnimationEnd();
                        }
                    }
                });
            }
        }).start();
    }

    public void tryToStop(int position) {
        stayIndex = position;
        isTryToStop = true;
    }

    public void reset() {
        isGameRunning = false;
        isTryToStop = false;
        isMarqueeRunning = true;
        currentIndex = 0;
        currentTotal = 0;
        stayIndex = 0;
        currentSpeed = DEFAULT_SPEED;
        mListener = null;

        for (ItemView itemView : itemViewArr) {
            itemView.setFocus(false);
        }
    }

    public void setGameListener(LuckyMonkeyAnimationListener listener) {
        mListener = listener;
    }

    public interface LuckyMonkeyAnimationListener {
        void onAnimationEnd();
    }
}
