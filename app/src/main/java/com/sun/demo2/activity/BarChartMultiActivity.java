package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityBarChartMultiBinding;

import java.util.ArrayList;
/**
 * @author: Harper
 * @date:   2021/12/6
 * @note: 
 */
public class BarChartMultiActivity extends BaseMvpActivity<ActivityBarChartMultiBinding> {

    private Typeface tfLight;

    public static void start(Context context) {
        Intent intent = new Intent(context, BarChartMultiActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_bar_chart_multi;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.red;
        mTitleColor = R.color.red;
        return true;
    }

    @Override
    public void initView() {
        baseBind.title.setTitle("Bar Charts 单柱状图");
        baseBind.title.setOnTitleClickListener(view -> close());
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
    }

    @Override
    public void initData() {
        vdb.multiBarChart.getDescription().setEnabled(false);
        vdb.multiBarChart.setPinchZoom(false);
        vdb.multiBarChart.setDrawBarShadow(false);
        vdb.multiBarChart.setDrawGridBackground(false);
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(chart);
//        chart.setMarker(mv);
//        Legend l = chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(true);
//        l.setTypeface(tfLight);
//        l.setYOffset(0f);
//        l.setXOffset(10f);
//        l.setYEntrySpace(0f);
//        l.setTextSize(8f);

        XAxis xAxis = vdb.multiBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setLabelCount(3);
//        xAxis.setTypeface(tfLight);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter((value, axis) -> String.valueOf((int) value));

        YAxis leftAxis = vdb.multiBarChart.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        vdb.multiBarChart.getAxisRight().setEnabled(false);
        vdb.multiBarChart.getLegend().setEnabled(false);
        initBarChart(8, 100);
    }

    private void initBarChart(int progressX, int progressY) {
        float groupSpace = 0.08f;
        float barSpace = 0.03f;
        float barWidth = 0.2f;

        int groupCount = progressX + 1;
        int startYear = 1980;
        int endYear = startYear + groupCount;

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();

        float randomMultiplier = progressY * 100000f;

//        for (int i = startYear; i < endYear; i++) {
//            values1.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
//            values2.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
//        }
        values1.add(new BarEntry(0, (float) (Math.random() * randomMultiplier)));
        values2.add(new BarEntry(0, (float) (Math.random() * randomMultiplier)));

        BarDataSet set1, set2;

//        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
//            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
//            set1.setValues(values1);
//            set2.setValues(values2);
//            chart.getData().notifyDataChanged();
//            chart.notifyDataSetChanged();
//        } else {
//            set1 = new BarDataSet(values1, "Company A");
//            set1.setColor(Color.rgb(104, 241, 175));
//            set2 = new BarDataSet(values2, "Company B");
//            set2.setColor(Color.rgb(164, 228, 251));
//            BarData data = new BarData(set1, set2);
//            data.setValueFormatter(new LargeValueFormatter());
//            data.setValueTypeface(tfLight);
//            chart.setData(data);
//        }
        set1 = new BarDataSet(values1, "Company A");
        set1.setColor(Color.rgb(104, 241, 175));
        set2 = new BarDataSet(values2, "Company B");
        set2.setColor(Color.rgb(164, 228, 251));
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new LargeValueFormatter());
        data.setValueTypeface(tfLight);
        vdb.multiBarChart.setData(data);
        vdb.multiBarChart.getBarData().setBarWidth(barWidth);
        vdb.multiBarChart.getXAxis().setAxisMinimum(startYear);
        vdb.multiBarChart.getXAxis().setAxisMaximum(startYear + vdb.multiBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        vdb.multiBarChart.groupBars(startYear, groupSpace, barSpace);
        vdb.multiBarChart.invalidate();
    }
}