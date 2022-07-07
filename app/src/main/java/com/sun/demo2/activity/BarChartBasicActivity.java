package com.sun.demo2.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Fill;
import com.github.mikephil.charting.utils.MPPointF;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityBarChartBasicBinding;
import com.sun.demo2.view.custom.DayAxisValueFormatter;
import com.sun.demo2.view.custom.MyAxisValueFormatter;
import com.sun.demo2.view.custom.XYMarkerView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/11/22
 * @note: 柱状图
 */
public class BarChartBasicActivity extends BaseMvpActivity<ActivityBarChartBasicBinding> implements OnChartValueSelectedListener {

    private Typeface tfLight;

    public static void start(Context context) {
        Intent intent = new Intent(context, BarChartBasicActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_bar_chart_basic;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.black;
        mTitleColor = R.color.black;
        return true;
    }

    @Override
    public void initView() {
        mBaseBind.title.setTitle("Bar Charts 双柱状图");
        mBaseBind.title.setOnTitleClickListener(view -> close());

        bind.flContent.setOnClickListener(v -> {
            try {
                //分享
                Bitmap bitmap = Bitmap.createBitmap(bind.flContent.getWidth(), bind.flContent.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                bind.flContent.draw(c);
                //bitmap文件
                UMImage image = new UMImage(mThis(), bitmap);
                UMImage thumb = new UMImage(mThis(), ThumbnailUtils.extractThumbnail(bitmap, 200, 200));
                image.setThumb(thumb);
                new ShareAction(mThis()).setPlatform(SHARE_MEDIA.WEIXIN).withMedia(image).share();
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.share_failed);
            }
        });
    }

    @Override
    public void initData() {
        bind.chart.setOnChartValueSelectedListener(this);
        bind.chart.setDrawBarShadow(false);
        bind.chart.setDrawValueAboveBar(true);
        bind.chart.getDescription().setEnabled(false);
        bind.chart.setPinchZoom(false);
        bind.chart.setDrawGridBackground(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(bind.chart);
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        XAxis xAxis = bind.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = bind.chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = bind.chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = bind.chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(bind.chart); // For bounds control
        bind.chart.setMarker(mv); // Set the marker to the chart
        setData(4, 50);
    }

    private void setData(int count, float range) {

        float start = 1f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));

            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val, getResources().getDrawable(R.mipmap.star)));
            } else {
                values.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (bind.chart.getData() != null &&
                bind.chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) bind.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            bind.chart.getData().notifyDataChanged();
            bind.chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "The year 2017");

            set1.setDrawIcons(false);

            int startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);

            List<Fill> gradientFills = new ArrayList<>();
            gradientFills.add(new Fill(startColor1, startColor1));
            gradientFills.add(new Fill(startColor2, startColor2));
            gradientFills.add(new Fill(startColor3, startColor3));
            gradientFills.add(new Fill(startColor4, startColor4));
            gradientFills.add(new Fill(startColor5, startColor5));

            set1.setFills(gradientFills);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);

            bind.chart.setData(data);
        }
    }

    private final RectF onValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) {
            return;
        }
        RectF bounds = onValueSelectedRectF;
        bind.chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = bind.chart.getPosition(e, YAxis.AxisDependency.LEFT);
        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());
        Log.i("x-index", "low: " + bind.chart.getLowestVisibleX() + ", high: " + bind.chart.getHighestVisibleX());
        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
    }

}