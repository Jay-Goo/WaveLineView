package jaygoo.wavelineview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jaygoo.widget.wlv.WaveLineView;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：
 * 创建日期：2017/7/31
 * 描    述:
 * ================================================
 */
public class LeakTestActivity extends Activity{
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

        findViewById(R.id.leakTestBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LeakTestActivity.this,MainActivity.class));
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
