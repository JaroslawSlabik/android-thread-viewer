package jarek.threadview;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;

public class BackgroundService extends IntentService
{
    public static final String SERVICE_NAME = "BackgroundService";
    public static final String PROGRESS_NAME = "BackgroundService_progress";
    public static final String NUMBER_LOOPS_NAME = "BackgroundService_loops";
    public static final String DELAY_NAME = "BackgroundService_delay";

    public BackgroundService()
    {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent broadcastIntent;
        for(int i = 0; i < intent.getExtras().getInt(NUMBER_LOOPS_NAME); i++)
        {
            broadcastIntent = new Intent();
            broadcastIntent.setAction(SERVICE_NAME);
            broadcastIntent.putExtra(PROGRESS_NAME, i);
            sendBroadcast(broadcastIntent);
            SystemClock.sleep(intent.getExtras().getInt(DELAY_NAME));
        }

        broadcastIntent = new Intent();
        broadcastIntent.setAction(SERVICE_NAME);
        broadcastIntent.putExtra(PROGRESS_NAME, 0);
        sendBroadcast(broadcastIntent);
    }
}