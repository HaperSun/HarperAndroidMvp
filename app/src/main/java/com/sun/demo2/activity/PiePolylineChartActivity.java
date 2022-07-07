package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityPiePolylineChartBinding;

import java.util.ArrayList;
/**
 * @author: Harper
 * @date: 2022/4/13
 * @note: 带边线的饼状图
 */
public class PiePolylineChartActivity extends BaseMvpActivity<ActivityPiePolylineChartBinding> {

    private Typeface tf;
    protected final String[] parties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, PiePolylineChartActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_pie_polyline_chart;
    }

    @Override
    public void initView() {
        bind.chart.setUsePercentValues(true);
        bind.chart.getDescription().setEnabled(false);
        bind.chart.setExtraOffsets(5, 10, 5, 5);
        bind.chart.setDragDecelerationFrictionCoef(0.95f);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        bind.chart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        bind.chart.setCenterText(generateCenterSpannableText());
        bind.chart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        //控制中心圆的显示
        bind.chart.setDrawHoleEnabled(true);
        bind.chart.setTransparentCircleColor(Color.WHITE);
        //设置Label名称的颜色
        bind.chart.setEntryLabelColor(Color.BLACK);
        bind.chart.setTransparentCircleAlpha(110);
        bind.chart.setHoleRadius(58f);
        bind.chart.setTransparentCircleRadius(61f);
        bind.chart.setDrawCenterText(true);
        bind.chart.setRotationAngle(0);
        bind.chart.setRotationEnabled(true);
        bind.chart.setHighlightPerTapEnabled(true);

        bind.chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = bind.chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    @Override
    public void initData() {
        int count = 4;
        int range = 100;
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entries.add(new PieEntry((float) (Math.random() * range) + range / 5, parties[i % parties.length]));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);
        //v3.1.0需要用有参数的构造方法才能显示百分比 data.setValueFormatter(new PercentFormatter(mViewDataBinding.chart));
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        //设置Label值的颜色
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        bind.chart.setData(data);
        bind.chart.highlightValues(null);
        bind.chart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }
}