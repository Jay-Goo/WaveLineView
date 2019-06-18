# WaveLineView
## 一款内存友好的录音漂亮的波浪动画

# 效果图（实际效果更好）

![image](https://github.com/Jay-Goo/WaveLineView/blob/master/pictures/%E6%95%88%E6%9E%9C.gif)

----------

# Usage
## Step1
```
 allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	dependencies {
	        compile 'com.github.Jay-Goo:WaveLineView:v1.0.3'
	}
```
## Step2

```
<jaygoo.widget.wlv.WaveLineView
        android:id="@+id/waveLineView"
        android:layout_width="match_parent"
        android:layout_height="120dp"
        app:wlvBackgroundColor="@android:color/white"
        app:wlvMoveSpeed="290"
        />
```
## Step3

```
waveLineView.startAnim();

waveLineView.stopAnim();
```

```
  @Override
    protected void onResume() {
        super.onResume();
        waveLineView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        waveLineView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        waveLineView.release();
    }
```

----------
# Attributes
attr | format | description
-------- | ---|---
backgroundColor|color|背景色
wlvLineColor|color|波浪线的颜色
wlvThickLineWidth|dimension|中间粗波浪曲线的宽度
wlvFineLineWidth|dimension|三条细波浪曲线的宽度
wlvMoveSpeed|float|波浪线移动的速度，默认值为290F，方向从左向右，你可以使用负数改变移动方向
wlvSamplingSize|integer|采样率，动画效果越大越精细，默认64
wlvSensibility|integer|灵敏度，范围[1,10]，越大越灵敏，默认值为5

## [原理讲解传送门](https://github.com/Jay-Goo/WaveLineView/blob/master/blog.md)

## 联系我

- Email： 1015121748@qq.com
- QQ Group: 573830030 有时候工作很忙没空看邮件和Issue,大家可以通过QQ群联系我
<div style="text-align: center;">
<img src="https://github.com/Jay-Goo/RangeSeekBar/blob/master/Gif/qq.png" style="margin: 0 auto;" height="250px"/>
</div>

## 一杯咖啡

大家都知道开源是件很辛苦的事情，这个项目也是我工作之余完成的，平时工作很忙，但大家提的需求基本上我都尽量满足，如果这个项目帮助你节省了大量时间，你很喜欢，你可以给我一杯咖啡的鼓励，不在于钱多钱少，关键是你的这份鼓励所带给我的力量~
<div style="text-align: center;">
<img src="https://github.com/Jay-Goo/RangeSeekBar/blob/master/Gif/pay.png" height="200px"/>
</div>

# 致谢
[Bugly—以Tencent OS录音机波形动画为实例](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653577211&idx=1&sn=2619c7df79f675e45e87891b7eb17669&scene=4#wechat_redirect)

[DrkCore—以Tencent OS录音机波形为例](http://blog.csdn.net/drkcore/article/details/51822818)
