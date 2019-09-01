package jaygoo.widget.wlv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.0.0
 * 创建日期：2017/7/21
 * 描    述: 绘制波浪曲线
 * ================================================
 */
public class WaveLineView extends RenderView {

    private final static int DEFAULT_SAMPLING_SIZE = 64;
    private final static float DEFAULT_OFFSET_SPEED = 250F;
    private final static int DEFAULT_SENSIBILITY = 5;

    //采样点的数量，越高越精细，但是高于一定限度肉眼很难分辨，越高绘制效率越低
    private int samplingSize;

    //控制向右偏移速度，越小偏移速度越快
    private float offsetSpeed;
    //平滑改变的音量值
    private float volume = 0;

    //用户设置的音量，[0,100]
    private int targetVolume = 50;

    //每次平滑改变的音量单元
    private float perVolume;

    //灵敏度，越大越灵敏[1,10]
    private int sensibility;

    //背景色
    private int backGroundColor = Color.WHITE;

    //波浪线颜色
    private int lineColor;
    //粗线宽度
    private int thickLineWidth;
    //细线宽度
    private int fineLineWidth;

    private final Paint paint = new Paint();

    {
        //防抖动
        paint.setDither(true);
        //抗锯齿，降低分辨率，提高绘制效率
        paint.setAntiAlias(true);
    }

    private List<Path> paths = new ArrayList<>();

    {
        for (int i = 0; i < 4; i++) {
            paths.add(new Path());
        }
    }

    //不同函数曲线系数
    private float[] pathFuncs = {
            0.6f, 0.35f, 0.1f, -0.1f
    };

    //采样点X坐标
    private float[] samplingX;
    //采样点位置映射到[-2,2]之间
    private float[] mapX;
    //画布宽高
    private int width, height;
    //画布中心的高度
    private int centerHeight;
    //振幅
    private float amplitude;
    //存储衰变系数
    private SparseArray<Double> recessionFuncs = new SparseArray<>();
    //连线动画结束标记
    private boolean isPrepareLineAnimEnd = false;
    //连线动画位移
    private int lineAnimX = 0;
    //渐入动画结束标记
    private boolean isPrepareAlphaAnimEnd = false;
    //渐入动画百分比值[0,1f]
    private float prepareAlpha = 0f;
    //是否开启准备动画
    private boolean isOpenPrepareAnim = false;

    private boolean isTransparentMode = false;

    public WaveLineView(Context context) {
        this(context, null);
    }

    public WaveLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.WaveLineView);
        backGroundColor = t.getColor(R.styleable.WaveLineView_wlvBackgroundColor, Color.WHITE);
        samplingSize = t.getInt(R.styleable.WaveLineView_wlvSamplingSize, DEFAULT_SAMPLING_SIZE);
        lineColor = t.getColor(R.styleable.WaveLineView_wlvLineColor, Color.parseColor("#2ED184"));
        thickLineWidth = (int) t.getDimension(R.styleable.WaveLineView_wlvThickLineWidth, 6);
        fineLineWidth = (int) t.getDimension(R.styleable.WaveLineView_wlvFineLineWidth, 2);
        offsetSpeed = t.getFloat(R.styleable.WaveLineView_wlvMoveSpeed, DEFAULT_OFFSET_SPEED);
        sensibility = t.getInt(R.styleable.WaveLineView_wlvSensibility, DEFAULT_SENSIBILITY);
        isTransparentMode = backGroundColor == Color.TRANSPARENT;
        t.recycle();
        checkVolumeValue();
        checkSensibilityValue();
        //将RenderView放到最顶层
        setZOrderOnTop(true);
        if (getHolder() != null) {
            //使窗口支持透明度
            getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
    }

    @Override
    protected void doDrawBackground(Canvas canvas) {
        //绘制背景
        if (isTransparentMode) {
            //启用CLEAR模式，所绘制内容不会提交到画布上。
            canvas.drawColor(backGroundColor, PorterDuff.Mode.CLEAR);
        } else {
            canvas.drawColor(backGroundColor);
        }
    }

    private boolean isParametersNull() {
        if (null == samplingX || null == mapX || null == pathFuncs) {
            return true;
        }
        return false;
    }

    @Override
    protected void onRender(Canvas canvas, long millisPassed) {
        float offset = millisPassed / offsetSpeed;

        if (isParametersNull()) {
            initDraw(canvas);
        }

        if (lineAnim(canvas)) {
            resetPaths();
            softerChangeVolume();

            //波形函数的值
            float curY;
            for (int i = 0; i <= samplingSize; i++) {
                //双重判断确保必要参数正常
                if (isParametersNull()) {
                    initDraw(canvas);
                    if (isParametersNull()) {
                        return;
                    }
                }
                float x = samplingX[i];
                curY = (float) (amplitude * calcValue(mapX[i], offset));
                for (int n = 0; n < paths.size(); n++) {
                    //四条线分别乘以不同的函数系数
                    float realY = curY * pathFuncs[n] * volume * 0.01f;
                    paths.get(n).lineTo(x, centerHeight + realY);
                }
            }

            //连线至终点
            for (int i = 0; i < paths.size(); i++) {
                paths.get(i).moveTo(width, centerHeight);
            }

            //绘制曲线
            for (int n = 0; n < paths.size(); n++) {
                if (n == 0) {
                    paint.setStrokeWidth(thickLineWidth);
                    paint.setAlpha((int) (255 * alphaInAnim()));
                } else {
                    paint.setStrokeWidth(fineLineWidth);
                    paint.setAlpha((int) (100 * alphaInAnim()));
                }
                canvas.drawPath(paths.get(n), paint);
            }
        }

    }

    //检查音量是否合法
    private void checkVolumeValue() {
        if (targetVolume > 100) targetVolume = 100;
    }

    //检查灵敏度值是否合法
    private void checkSensibilityValue() {
        if (sensibility > 10) sensibility = 10;
        if (sensibility < 1) sensibility = 1;
    }

    /**
     * 使曲线振幅有较大改变时动画过渡自然
     */
    private void softerChangeVolume() {
        //这里减去perVolume是为了防止volume频繁在targetVolume上下抖动
        if (volume < targetVolume - perVolume) {
            volume += perVolume;
        } else if (volume > targetVolume + perVolume) {
            if (volume < perVolume * 2) {
                volume = perVolume * 2;
            } else {
                volume -= perVolume;
            }
        } else {
            volume = targetVolume;
        }

    }

    /**
     * 渐入动画
     *
     * @return progress of animation
     */
    private float alphaInAnim() {
        if (!isOpenPrepareAnim) return 1;
        if (prepareAlpha < 1f) {
            prepareAlpha += 0.02f;
        } else {
            prepareAlpha = 1;
        }
        return prepareAlpha;
    }

    /**
     * 连线动画
     *
     * @param canvas
     * @return whether animation is end
     */
    private boolean lineAnim(Canvas canvas) {
        if (isPrepareLineAnimEnd || !isOpenPrepareAnim) return true;
        paths.get(0).moveTo(0, centerHeight);
        paths.get(1).moveTo(width, centerHeight);

        for (int i = 1; i <= samplingSize; i++) {
            float x = 1f * i * lineAnimX / samplingSize;
            paths.get(0).lineTo(x, centerHeight);
            paths.get(1).lineTo(width - x, centerHeight);
        }

        paths.get(0).moveTo(width / 2f, centerHeight);
        paths.get(1).moveTo(width / 2f, centerHeight);

        lineAnimX += width / 60;
        canvas.drawPath(paths.get(0), paint);
        canvas.drawPath(paths.get(1), paint);

        if (lineAnimX > width / 2) {
            isPrepareLineAnimEnd = true;
            return true;
        }
        return false;
    }

    /**
     * 重置path
     */
    private void resetPaths() {
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).rewind();
            paths.get(i).moveTo(0, centerHeight);
        }
    }

    //初始化参数
    private void initParameters() {
        lineAnimX = 0;
        prepareAlpha = 0f;
        isPrepareLineAnimEnd = false;
        isPrepareAlphaAnimEnd = false;
        samplingX = null;
    }

    @Override
    public void startAnim() {
        initParameters();
        super.startAnim();
    }

    @Override
    public void stopAnim() {
        super.stopAnim();
        clearDraw();
    }

    //清空画布所有内容
    public void clearDraw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas(null);
            canvas.drawColor(backGroundColor);
            resetPaths();
            for (int i = 0; i < paths.size(); i++) {
                canvas.drawPath(paths.get(i), paint);
            }
        } catch (Exception e) {
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    //初始化绘制参数
    private void initDraw(Canvas canvas) {
        width = canvas.getWidth();
        height = canvas.getHeight();

        if (width == 0 || height == 0 || samplingSize == 0) return;

        centerHeight = height >> 1;
        //振幅为高度的1/4
        amplitude = height / 3.0f;

        //适合View的理论最大音量值，和音量不属于同一概念
        perVolume = sensibility * 0.35f;

        //初始化采样点及映射
        //这里因为包括起点和终点，所以需要+1
        samplingX = new float[samplingSize + 1];
        mapX = new float[samplingSize + 1];
        //确定采样点之间的间距
        float gap = width / (float) samplingSize;
        //采样点的位置
        float x;
        for (int i = 0; i <= samplingSize; i++) {
            x = i * gap;
            samplingX[i] = x;
            //将采样点映射到[-2，2]
            mapX[i] = (x / (float) width) * 4 - 2;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor);
        paint.setStrokeWidth(thickLineWidth);
    }

    /**
     * 计算波形函数中x对应的y值
     * <p>
     * 使用稀疏矩阵进行暂存计算好的衰减系数值，下次使用时直接查找，减少计算量
     *
     * @param mapX   换算到[-2,2]之间的x值
     * @param offset 偏移量
     * @return [-1, 1]
     */
    private double calcValue(float mapX, float offset) {
        int keyX = (int) (mapX * 1000);
        offset %= 2;
        double sinFunc = Math.sin(Math.PI * mapX - offset * Math.PI);
        double recessionFunc;
        if (recessionFuncs.indexOfKey(keyX) >= 0) {
            recessionFunc = recessionFuncs.get(keyX);
        } else {
            recessionFunc = 4 / (4 + Math.pow(mapX, 4));
            recessionFuncs.put(keyX, recessionFunc);
        }
        return sinFunc * recessionFunc;
    }

    /**
     * the wave line animation move speed from left to right
     * you can use negative number to make the animation from right to left
     * the default value is 290F,the smaller, the faster
     *
     * @param moveSpeed
     */
    public void setMoveSpeed(float moveSpeed) {
        this.offsetSpeed = moveSpeed;
    }


    /**
     * User set volume, [0,100]
     *
     * @param volume
     */
    public void setVolume(int volume) {
        if (Math.abs(targetVolume - volume) > perVolume) {
            this.targetVolume = volume;
            checkVolumeValue();
        }
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
        this.isTransparentMode = (backGroundColor == Color.TRANSPARENT);
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * Sensitivity, the bigger the more sensitive [1,10]
     * the default value is 5
     *
     * @param sensibility
     */
    public void setSensibility(int sensibility) {
        this.sensibility = sensibility;
        checkSensibilityValue();
    }
}
