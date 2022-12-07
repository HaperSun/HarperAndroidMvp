##################################参考：https://www.jianshu.com/p/f9438603e096 ######################
######################################自定义混淆方案适用于大部分的项目####################################
#指定压缩级别，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers
#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#把混淆类中的方法名也混淆了
-useuniqueclassmembernames
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
#保持泛型
-keepattributes Signature
# 保留Serializable序列化的类不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
 # 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}
#反射
-keepattributes Signature
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }
-keep public class com.google.gson.**{*;}
-keep class com.google.gson.examples.android.model.** { *; }
-dontwarn com.google.gson.**
-dontwarn com.google.gson.examples.android.model.**
#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Fragment
#基本组件
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep class **.R$* { *; }
# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn com.sun.demo2.**
-keep class com.sun.demo2.** {*; }
#QQ混淆
-keep class com.tencent.** { *; }
-dontwarn com.tencent.**
#微信
-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.tencent.wxop.** { *; }
-keep class com.tencent.mm.sdk.** { *; }
#okhttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-keep class okhttp3.** { *;}
-keep class okio.** { *;}
-dontwarn sun.security.**
-keep class sun.security.** { *;}
-dontwarn okio.**
-dontwarn okhttp3.**
#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn org.robovm.**
-keep class org.robovm.** { *; }
# RxJava RxAndroid
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
# Gson
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
#greendao
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
#-keep class **$Properties报错，替换成-keep class **$Properties{*;}后ok了，why???
-keep class **$Properties{*;}
#native
-keepclassmembers class * {
    native <methods>;
}
#eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.** { *; }
#rxjava
-dontwarn rx.**
-keep class rx.** { *; }
# webView
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}

#baidu
-keep class com.baidu.** { *; }
-dontwarn com.baidu.**

-dontwarn android.net.**
-keep class android.net.** { *; }



-keep class com.google.** {
    <fields>;
    <methods>;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#umeng
-dontwarn com.umeng.**
-keep class com.umeng.**{*;}
-keep class u.aly.**{*;}
-keep class com.google.**{*;}

#httpclient (org.apache.http.legacy.jar)
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-dontwarn org.apache.http.protocol.**
-keep class android.net.compatibility.**{*;}
#-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.org.**{*;}
-keep class org.apache.harmony.**{*;}

#lib-wheel
-dontwarn kankan.wheel.**
-keep class kankan.wheel.**{*;}

#PhotoPicker
-dontwarn me.iwf.photopicker.**
-keep class me.iwf.photopicker.**{*;}

#nineoldandroids
-dontwarn com.nineoldandroids.*
-keep class com.nineoldandroids.** { *;}

#weixin
-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.**{*;}

###############################常用第三方混淆配置（已按字母排序，请按需放开！！！）##########################
## AndroidEventBus
#-keep class org.simple.** { *; }
#-keep interface org.simple.** { *; }
#-keepclassmembers class * {
#    @org.simple.eventbus.Subscriber <methods>;
#}
#
#
## 百度地图（jar包换成自己的版本，记得签名要匹配）
#-libraryjars libs/baidumapapi_v2_1_3.jar
#-keep class com.baidu.** {*;}
#-keep class vi.com.** {*;}
#-keep class com.sinovoice.** {*;}
#-keep class pvi.com.** {*;}
#-dontwarn com.baidu.**
#-dontwarn vi.com.**
#-dontwarn pvi.com.**
#
#
## BRVAH
#-keep class com.chad.library.adapter.** { *; }
#-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
#-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
#-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
#    <init>(android.view.View);
#}
#
#
## Bugly
#-dontwarn com.tencent.bugly.**
#-keep class com.tencent.bugly.** {*;}
#
#
## ButterKnife
#-keep public class * implements butterknife.Unbinder {
#    public <init>(**, android.view.View);
#}
#-keep class butterknife.*
#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
#}
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#}
#
#
## Dagger2
#-dontwarn com.google.errorprone.annotations.*
#
#
## EventBus
#-keepattributes *Annotation*
#-keepclassmembers class ** {
#    @org.greenrobot.eventbus.Subscribe <methods>;
#}
#-keep enum org.greenrobot.eventbus.ThreadMode { *; }
#
#
## Facebook
#-keep class com.facebook.** {*;}
#-keep interface com.facebook.** {*;}
#-keep enum com.facebook.** {*;}
#
#
## FastJson
#-dontwarn com.alibaba.fastjson.**
#-keep class com.alibaba.fastjson.** { *; }
#-keepattributes Signature
#-keepattributes *Annotation*
#
#
## Fresco
#-keep class com.facebook.fresco.** {*;}
#-keep interface com.facebook.fresco.** {*;}
#-keep enum com.facebook.fresco.** {*;}
#
#
## 高德相关依赖
## 集合包:3D地图3.3.2 导航1.8.0 定位2.5.0
#-dontwarn com.amap.api.**
#-dontwarn com.autonavi.**
#-keep class com.amap.api.**{*;}
#-keep class com.autonavi.**{*;}
## 地图服务
#-dontwarn com.amap.api.services.**
#-keep class com.map.api.services.** {*;}
## 3D地图
#-dontwarn com.amap.api.mapcore.**
#-dontwarn com.amap.api.maps.**
#-dontwarn com.autonavi.amap.mapcore.**
#-keep class com.amap.api.mapcore.**{*;}
#-keep class com.amap.api.maps.**{*;}
#-keep class com.autonavi.amap.mapcore.**{*;}
## 定位
#-dontwarn com.amap.api.location.**
#-dontwarn com.aps.**
#-keep class com.amap.api.location.**{*;}
#-keep class com.aps.**{*;}
## 导航
#-dontwarn com.amap.api.navi.**
#-dontwarn com.autonavi.**
#-keep class com.amap.api.navi.** {*;}
#-keep class com.autonavi.** {*;}
#
#
## Glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#
#
#### greenDAO 3
#-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
#public static java.lang.String TABLENAME;
#        94  }
#-keep class **$Properties
#
## If you do not use SQLCipher:
#-dontwarn org.greenrobot.greendao.database.**
## If you do not use RxJava:
#-dontwarn rx.**
#
#
## Gson
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
## 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
## 将下面替换成自己的实体类
##-keep class com.example.bean.** { *; }
#
#
## Jackson
#-dontwarn org.codehaus.jackson.**
#-dontwarn com.fasterxml.jackson.databind.**
#-keep class org.codehaus.jackson.** { *;}
#-keep class com.fasterxml.jackson.** { *; }
#
#
## 极光推送
#-dontoptimize
#-dontpreverify
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }
#
#
## OkHttp
#-dontwarn okio.**
#-dontwarn okhttp3.**
#-dontwarn javax.annotation.Nullable
#-dontwarn javax.annotation.ParametersAreNonnullByDefault
#
#
## Okio
#-dontwarn com.squareup.**
#-dontwarn okio.**
#-keep public class org.codehaus.* { *; }
#-keep public class java.nio.* { *; }
#
#
## OrmLite
#-keepattributes *DatabaseField*
#-keepattributes *DatabaseTable*
#-keepattributes *SerializedName*
#-keep class com.j256.**
#-keepclassmembers class com.j256.** { *; }
#-keep enum com.j256.**
#-keepclassmembers enum com.j256.** { *; }
#-keep interface com.j256.**
#-keepclassmembers interface com.j256.** { *; }
#
#
## Realm
#-keep class io.realm.annotations.RealmModule
#-keep @io.realm.annotations.RealmModule class *
#-keep class io.realm.internal.Keep
#-keep @io.realm.internal.Keep class * { *; }
#-dontwarn javax.**
#-dontwarn io.realm.**
#
#
## Retrofit
#-keep class retrofit2.** { *; }
#-dontwarn retrofit2.**
#-keepattributes Signature
#-keepattributes Exceptions
#-dontwarn okio.**
#-dontwarn javax.annotation.**
#
#
## Retrolambda
#-dontwarn java.lang.invoke.*
#
#
## RxJava RxAndroid
#-dontwarn sun.misc.**
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
#    long producerIndex;
#    long consumerIndex;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode producerNode;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode consumerNode;
#}
#-dontnote rx.internal.util.PlatformDependent
#
#
## Universal-Image-Loader-v1.9.5
#-libraryjars libs/universal-image-loader-1.9.5-SNAPSHOT-with-sources.jar
#-dontwarn com.nostra13.universalimageloader.**
#-keep class com.nostra13.universalimageloader.** { *; }
#
#
## 微信支付
#-dontwarn com.tencent.mm.**
#-dontwarn com.tencent.wxop.stat.**
#-keep class com.tencent.mm.** {*;}
#-keep class com.tencent.wxop.stat.**{*;}
#
#
## 信鸽
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep class com.tencent.android.tpush.**  {* ;}
#-keep class com.tencent.mid.**  {* ;}
#-keepattributes *Annotation*
#
#
## 新浪微博
#-keep class com.sina.weibo.sdk.* { *; }
#-keep class android.support.v4.* { *; }
#-keep class com.tencent.* { *; }
#-keep class com.baidu.* { *; }
#-keep class lombok.ast.ecj.* { *; }
#-dontwarn android.support.v4.**
#-dontwarn com.tencent.**s
#-dontwarn com.baidu.**
#
#
## XLog
#-keep class com.tencent.mars.** { *; }
#-keepclassmembers class com.tencent.mars.** { *; }
#-dontwarn com.tencent.mars.**
#
#
## 讯飞语音
#-dontwarn com.iflytek.**
#-keep class com.iflytek.** {*;}
#
#
## xUtils3.0
#-keepattributes Signature,Annotation
#-keep public class org.xutils.** {
#public protected *;
#}
#-keep public interface org.xutils.** {
#public protected *;
#}
#-keepclassmembers class * extends org.xutils.** {
#public protected *;
#}
#-keepclassmembers @org.xutils.db.annotation.* class * {;}
#-keepclassmembers @org.xutils.http.annotation. class * {*;}
#-keepclassmembers class * {
#@org.xutils.view.annotation.Event ;
#}
#
#
## 银联
#-dontwarn com.unionpay.**
#-keep class com.unionpay.** { *; }
#
#
## 友盟统计分析
#-keepclassmembers class * { public <init>(org.json.JSONObject); }
#-keepclassmembers enum com.umeng.analytics.** {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#
## 友盟自动更新
#-keepclassmembers class * { public <init>(org.json.JSONObject); }
#-keep public class cn.irains.parking.cloud.pub.R$*{ public static final int *; }
#-keep public class * extends com.umeng.**
#-keep class com.umeng.** { *; }
#
#
## 支付宝钱包
#-dontwarn com.alipay.**
#-dontwarn HttpUtils.HttpFetcher
#-dontwarn com.ta.utdid2.**
#-dontwarn com.ut.device.**
#-keep class com.alipay.android.app.IAlixPay{*;}
#-keep class com.alipay.android.app.IAlixPay$Stub{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
#-keep class com.alipay.sdk.app.PayTask{ public *;}
#-keep class com.alipay.sdk.app.AuthTask{ public *;}
#-keep class com.alipay.mobilesecuritysdk.*
#-keep class com.ut.*