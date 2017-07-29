package jaygoo.wavelineview;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;

import com.czt.mp3recorder.MP3Recorder;

import java.io.File;

import jaygoo.widget.wlv.WaveLineView;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：
 * 创建日期：2017/7/18
 * 描    述:
 * ================================================
 */
public class WaveMp3Recorder extends WaveLineView {
    private String RECORD_FILE_DIR = Environment.getExternalStorageDirectory()+"/";
    private String recordFileName = "WaveLineViewTest.mp3";
    private MP3Recorder mp3Recorder;
    private long maxRecordTime = 1000 * 60 * 60 * 24;
    private long recordTime = 0;
    private int UPDATE_TIME = 200;
    private OnRecordStateChangeListener mOnRecordStateChangeListener;
    private Handler mHandler = new Handler();

    public WaveMp3Recorder(Context context) {
        this(context,null);
    }

    public WaveMp3Recorder(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaveMp3Recorder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRecorder();
    }

    private void updateRecordingUI(){
        if (mp3Recorder != null) {
            setVolume(100 * mp3Recorder.getVolume() / mp3Recorder.getMaxVolume());
        }
    }

    private void updateStopRecordUI(){
        stopAnim();
    }

    private void updateStartRecordUI(){
        startAnim();
    }

    /**
     * 录音更新进度条
     */
    private Runnable mRecordProgressTask = new Runnable() {
        public void run() {

            //录音时间超出最大时间，自动停止
            if (recordTime > maxRecordTime){
                stopRecord(true);
            }else {
                updateRecordingUI();
                if (mHandler != null) {
                    mHandler.postDelayed(mRecordProgressTask, UPDATE_TIME);
                }
            }
        }
    };

    public void initRecorder(){
        File recordFile = new File(RECORD_FILE_DIR, recordFileName);
        mp3Recorder = new MP3Recorder(recordFile);
        mp3Recorder.setDefaultLameMp3BitRate(96);
    }

    public void startRecording(){
        try {
            mp3Recorder.start();
            if (mHandler != null) {
                mHandler.post(mRecordProgressTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //停止录音按钮状态
    public void stopRecord(boolean isFromUser){
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        try {
            recordTime = 0;
            updateStopRecordUI();
            if (mp3Recorder != null) {
                mp3Recorder.stop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        if (mOnRecordStateChangeListener != null){
            mOnRecordStateChangeListener.onStopRecord(getRecordFile(),isFromUser);
        }
    }

    public void stopRecord(){
        stopRecord(false);
    }

    //开始录音
    public void startRecord(){
        updateStartRecordUI();
        try {
            mp3Recorder.start();
            if (mHandler != null) {
                mHandler.post(mRecordProgressTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mOnRecordStateChangeListener != null){
            mOnRecordStateChangeListener.onStartRecord();
        }
    }

    //是否在录音
    public boolean isRecording(){
        if (mp3Recorder != null){
            return mp3Recorder.isRecording();
        }
        return false;
    }

    //设置录音的最大时间
    public void setMaxRecordTime(long millis){
        maxRecordTime = millis;
    }

    //获取录音文件
    public File getRecordFile(){
        return new File(RECORD_FILE_DIR, recordFileName);
    }

    public String getRecordFilePath(){
        return RECORD_FILE_DIR + "/"+recordFileName;
    }

    public MP3Recorder getMp3Recorder(){
        return mp3Recorder;
    }


    //录音状态监听
    public interface OnRecordStateChangeListener{
        void onStartRecord();
        void onStopRecord(File recordFile, boolean isFromUser);
    }

    public void setOnRecordStateChangeListener(OnRecordStateChangeListener listener){
        mOnRecordStateChangeListener = listener;
    }

}
