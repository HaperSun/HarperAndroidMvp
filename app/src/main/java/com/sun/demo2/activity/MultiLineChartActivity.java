package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/4/21
 * @note: 多条目折线图
 */
public class MultiLineChartActivity extends BaseMvpActivity {

    private LineChart chart;
    private String[] labels;
    private List<Integer> listX;
    private List<Integer> listY;

    public static void start(Context context) {
        Intent intent = new Intent(context, MultiLineChartActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_multi_line_chart;
    }

    @Override
    public void initIntent() {
        labels = new String[]{"1月", "2月", "3月", "4月", "5月"};
        listX = new ArrayList<>();
        listX.add(10);
        listX.add(5);
        listX.add(15);
        listX.add(4);
        listX.add(24);
        listY = new ArrayList<>();
        listY.add(2);
        listY.add(12);
        listY.add(6);
        listY.add(20);
        listY.add(11);
    }

    @Override
    public void initView() {
        Typeface tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        chart = $(R.id.chart);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
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

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setLabelCount(3);
//        xAxis.setTypeface(tfLight);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
//        xAxis.setValueFormatter((value, axis) -> String.valueOf((int) value));

        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(true);

//        chart.setDrawGridBackground(false);
//        chart.getDescription().setEnabled(false);
//        chart.setDrawBorders(false);
//        chart.setBackgroundColor(Color.WHITE);
//        chart.getAxisLeft().setEnabled(false);
//        chart.getAxisRight().setDrawAxisLine(false);
//        chart.getAxisRight().setDrawGridLines(false);
//        chart.getXAxis().setDrawAxisLine(false);
//        chart.getXAxis().setDrawGridLines(false);
//
//        // enable touch gestures
//        chart.setTouchEnabled(true);
//
//        // enable scaling and dragging
//        chart.setDragEnabled(true);
//        chart.setScaleEnabled(true);
//
//        // if disabled, scaling can be done on x- and y-axis separately
//        chart.setPinchZoom(false);
//
////        seekBarX.setProgress(20);
////        seekBarY.setProgress(100);
//        chart.getDescription().setEnabled(false);
//        chart.setPinchZoom(false);
//        chart.setDrawGridBackground(false);
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
//        YAxis yAxis = chart.getAxisLeft();
//        chart.getAxisLeft().setDrawGridLines(false);
//        chart.getLegend().setEnabled(true);


        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        setChartData(20,100);
    }

    @Override
    public void initData() {

    }

    private final int[] colors = new int[] {
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };

    private void setChartData(int progressX,int progressY){
        chart.resetTracking();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        for (int z = 0; z < 2; z++) {

            ArrayList<Entry> values = new ArrayList<>();
            if (z == 0){
                for (int i = 0; i < listX.size(); i++) {
                    values.add(new Entry(i, (float) listX.get(i)));
                }
            }else {
                for (int i = 0; i < listX.size(); i++) {
                    values.add(new Entry(i, (float) listY.get(i)));
                }
            }

//            for (int i = 0; i < progressX; i++) {
//                double val = (Math.random() * progressY) + 3;
//                values.add(new Entry(i, (float) val));
//            }

            LineDataSet d = new LineDataSet(values, "DataSet " + (z + 1));
            d.setLineWidth(2.5f);
            d.setCircleRadius(4f);

            int color = colors[z % colors.length];
            d.setColor(color);
            d.setCircleColor(color);
            dataSets.add(d);
        }

//        chart.getXAxis().setLabelCount(11);
        // make the first DataSet dashed
//        ((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
//        ((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
//        ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }
}