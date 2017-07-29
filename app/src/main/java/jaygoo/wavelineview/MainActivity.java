package jaygoo.wavelineview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jaygoo.widget.wlv.WaveLineView;

public class MainActivity extends AppCompatActivity {

    private WaveLineView waveLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveLineView = (WaveLineView) findViewById(R.id.waveLineView);

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveLineView.startAnim();
            }
        });

        findViewById(R.id.stopBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveLineView.stopAnim();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
      waveLineView.onResume(true);
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
}
