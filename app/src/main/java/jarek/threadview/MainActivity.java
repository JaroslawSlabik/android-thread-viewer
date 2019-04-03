package jarek.threadview;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends Activity implements ProcessFragment.ProcessCallbacks
{
    public class BackgroundServiceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context contex, Intent intent)
        {
            if(intent.getAction().equals(BackgroundService.SERVICE_NAME))
            {
                m_progres_bar.setProgress(intent.getExtras().getInt(BackgroundService.PROGRESS_NAME));
            }
        }
    }

    //1) za pomocą AsyncTask oraz
    //2) za pomocą IntentService.

    private static final String PROCESS_FRAGMENT_NAME = "process_fragment";
    private static final int NUMBER_LOOPS = 20;
    private static final int DELAY_ONE_LOOP_MS = 1000;

    Button m_button_s1;
    Button m_button_s2;
    Button m_button_s3;
    ProcessFragment m_process_fragment;
    ProgressBar m_progres_bar;
    BackgroundServiceReceiver m_services_receive;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        m_process_fragment = (ProcessFragment)fm.findFragmentByTag(PROCESS_FRAGMENT_NAME);

        if (m_process_fragment == null) {
            m_process_fragment = new ProcessFragment();
            fm.beginTransaction().add(m_process_fragment, PROCESS_FRAGMENT_NAME).commit();
        }

        m_button_s1 = (Button)findViewById(R.id.m_button_s1);
        m_button_s2 = (Button)findViewById(R.id.m_button_s2);
        m_button_s3 = (Button)findViewById(R.id.m_button_s3);
        m_progres_bar = (ProgressBar)findViewById(R.id.m_progress_bar);

        m_services_receive = new BackgroundServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.SERVICE_NAME);
        registerReceiver(m_services_receive, filter);
    }

    public void button_s1_clicked(View view)
    {
        m_progres_bar.setProgress(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < NUMBER_LOOPS; i++)
                {
                    m_progres_bar.post(new Runnable() {
                        @Override
                        public void run() {
                            m_progres_bar.setProgress(m_progres_bar.getProgress() + 1);
                        }
                    });
                    SystemClock.sleep(DELAY_ONE_LOOP_MS);
                }
                m_progres_bar.setProgress(0);
            }
        }).start();
    }

    public void button_s2_clicked(View view)
    {
        m_process_fragment.start(NUMBER_LOOPS, DELAY_ONE_LOOP_MS);
    }

    public void button_s3_clicked(View view)
    {
        Intent delayIntent = new Intent();
        delayIntent.setClass(this, BackgroundService.class);
        delayIntent.putExtra(BackgroundService.DELAY_NAME, DELAY_ONE_LOOP_MS);
        delayIntent.putExtra(BackgroundService.NUMBER_LOOPS_NAME, NUMBER_LOOPS);
        startService(delayIntent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(m_services_receive, new IntentFilter(BackgroundService.SERVICE_NAME));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(m_services_receive);
    }

    @Override
    public void onPreExecute()
    {
        m_progres_bar.setProgress(0);
    }

    @Override
    public void onProgressUpdate(int percent)
    {
        m_progres_bar.setProgress(percent);
    }

    @Override
    public void onCancelled()
    {
        m_progres_bar.setProgress(0);
    }

    @Override
    public void onPostExecute()
    {
        m_progres_bar.setProgress(0);
    }
}
