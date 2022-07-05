package com.sun.demo2.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.sun.demo2.R;
import com.sun.demo2.fragment.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

public class SupportMapFragmentActivity extends AppCompatActivity {

    protected TencentMap tencentMap;
    protected UiSettings mapUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_map_fragment);
        //创建tencentMap地图对象，可以完成对地图的几乎所有操作
        /**
         * SDK提供了SupportMapFragment这个类来加载地图，这个类的方便之处就在于不用手动管理内存
         */
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_frag);
        tencentMap = supportMapFragment.getMap();
        mapUiSettings = tencentMap.getUiSettings();
        //对地图操作类进行操作
        CameraUpdate cameraSigma = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(39.984066, 116.307548), 15, 0f, 0f));
        //移动地图
        tencentMap.moveCamera(cameraSigma);
    }
}