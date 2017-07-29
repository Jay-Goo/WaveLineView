package jaygoo.wavelineview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jaygoo.widget.wlv.WaveLineView;

public class MainActivity extends AppCompatActivity {

    private WaveLineView waveLineView;
    private WaveMp3Recorder waveMp3Recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        waveLineView = (WaveLineView) findViewById(R.id.waveLineView);
        waveMp3Recorder = (WaveMp3Recorder) findViewById(R.id.waveLineView);
        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!waveMp3Recorder.isRecording()){
                    waveMp3Recorder.startRecord();
                }
            }
        });

        findViewById(R.id.stopBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waveMp3Recorder.isRecording()){
                    waveMp3Recorder.stopRecord(true);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//      waveLineView.onResume(true);
        waveMp3Recorder.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//      waveLineView.onPause();
        waveMp3Recorder.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        waveLineView.release();
//        waveMp3Recorder.release();
    }
}
