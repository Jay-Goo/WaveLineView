package jaygoo.wavelineview;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;


/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：
 * 创建日期：2017/7/28
 * 描    述:
 * ================================================
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
