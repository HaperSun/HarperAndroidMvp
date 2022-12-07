package com.sun.demo2.fragment;

import android.os.Bundle;
import android.os.Handler;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.demo2.R;
import com.sun.demo2.databinding.FragmentSudoKuTurnTableBinding;

import java.util.Random;

/**
 * @author Harper
 * @date 2022/5/31
 * note:
 */
public class SudokuTurnTableFragment extends BaseMvpFragment<FragmentSudoKuTurnTableBinding> {

    //抽奖时间
    private long drawTime;
    //中奖标记
    private int MARK_LUCKY = 6;
    private static Handler handler = new Handler();

    public static SudokuTurnTableFragment getInstance() {
        SudokuTurnTableFragment fragment = new SudokuTurnTableFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_sudo_ku_turn_table;
    }

    @Override
    public void initView() {
        vdb.idDrawBtn.setOnClickListener(v -> {
            if (System.currentTimeMillis() - drawTime < 5000) {
                showToast("心急吃不了热豆腐，请5秒后再点击哦");
                return;
            }
            //开始抽奖
            if (!vdb.luckyPanel.isGameRunning()) {
                drawTime = System.currentTimeMillis();
                vdb.luckyPanel.startGame();
                getLuck();
            }
        });
    }

    @Override
    public void initData() {

    }

    private void getLuck() {
        //延长时间
        long delay = 0;
        long duration = System.currentTimeMillis() - drawTime;
        if (duration < 5000) {
            delay = 5000 - duration;
        }
        handler.postDelayed(() -> {
            if (mActivity.isFinishing()) {
                return;
            }
            vdb.luckyPanel.tryToStop(getPrizePosition(MARK_LUCKY));
            vdb.luckyPanel.setGameListener(() -> {
                //延长1S弹出抽奖结果
                handler.postDelayed(() -> showToast(getPrizeName(MARK_LUCKY)), 1000);
            });
        }, delay);
    }


    /**
     * 根据奖品等级计算出奖品位置
     *
     * @param prizeGrade
     * @return
     */
    private int getPrizePosition(int prizeGrade) {
        switch (prizeGrade) {
            case 1:
                return 0;
            case 2:
                return 4;
            case 3:
                return 2;
            case 4:
                return 5;
            case 5:
                return 7;
            case 6:
                //六等奖有三个位置，随机取一个
                int[] position = {1, 3, 6};
                Random random = new Random();
                return position[random.nextInt(3)];
        }
        return prizeGrade;
    }


    /**
     * 奖品名称
     *
     * @param grade
     * @return
     */
    private String getPrizeName(int grade) {
        switch (grade) {
            case 1:
                return "iPhone 8 手机一部";
            case 2:
                return "Beats 耳机一副";
            case 3:
                return "周大福转运珠一颗";
            case 4:
                return "小米体重称一个";
            case 5:
                return "暴风魔镜VR眼镜一副";
            case 6:
                return "爱奇艺月卡会员";
            default:
                return "";
        }
    }
}
