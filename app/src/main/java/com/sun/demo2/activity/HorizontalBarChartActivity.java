package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityHorizontalBarChartBinding;
import com.sun.demo2.model.DataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/4/13
 * @note: Horizontal Bar Charts 横向单柱状图
 */
public class HorizontalBarChartActivity extends BaseMvpActivity {

    private Typeface tfLight;
    private ActivityHorizontalBarChartBinding bind;
    private List<DataBean> mDataBeans;

    public static void start(Context context) {
        Intent intent = new Intent(context, HorizontalBarChartActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_horizontal_bar_chart;
    }

    @Override
    public void initView() {
        getData();
        bind = (ActivityHorizontalBarChartBinding) mViewDataBinding;
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        bind.chart.setDrawBarShadow(false);
        bind.chart.setDrawValueAboveBar(false);
        bind.chart.getDescription().setEnabled(false);
        bind.chart.setMaxVisibleValueCount(60);
        bind.chart.setPinchZoom(false);
        bind.chart.setDrawGridBackground(false);
        XAxis xl = bind.chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tfLight);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);
        YAxis yl = bind.chart.getAxisLeft();
        yl.setTypeface(tfLight);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setAxisMinimum(0f);
        YAxis yr = bind.chart.getAxisRight();
        yr.setTypeface(tfLight);
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);
        bind.chart.setFitBars(true);
        bind.chart.animateY(2500);
        Legend l = bind.chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
        setData(12, 50);
    }

    private void getData() {
        mDataBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mDataBeans.add(new DataBean("第" + i, i));
        }

        int a = 10;
        int b = 30;
        int c = (int) (Math.round(10*100/30)/100.0*100);
        System.out.print(c);
    }

    private void setData(int count, float range) {
        float barWidth = 9f;
        float spaceForBar = 10f;
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < mDataBeans.size(); i++) {
            DataBean bean = mDataBeans.get(i);
            values.add(new BarEntry(i * spaceForBar, bean.getData(), bean.getName()));
        }
        BarDataSet set1;
        if (bind.chart.getData() != null && bind.chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) bind.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            bind.chart.getData().notifyDataChanged();
            bind.chart.notifyDataSetChanged();
        } else {
//            List<Fill> gradientFills = new ArrayList<>();
//            int startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
//            gradientFills.add(new Fill(startColor1, startColor1));
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setColor(Color.rgb(104, 241, 175));
//            set1.setFills(gradientFills);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(barWidth);
            bind.chart.setData(data);
        }
    }


    @Override
    public void initData() {

    }
}