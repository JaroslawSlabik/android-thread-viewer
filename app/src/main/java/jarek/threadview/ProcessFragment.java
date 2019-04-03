package jarek.threadview;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

public class ProcessFragment extends Fragment {
    interface ProcessCallbacks {
        void onPreExecute();

        void onProgressUpdate(int percent);

        void onCancelled();

        void onPostExecute();
    }

    private ProcessCallbacks mCallbacks;
    private BackgroundProcess process;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (ProcessCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void start(Integer... params)
    {
        process = new BackgroundProcess();
        process.execute(params);
    }

    public class BackgroundProcess extends AsyncTask<Integer, Integer, Void> {
        private Integer ID_LOOPS = 0;
        private Integer ID_DELAY_ONE_LOOPS_MS = 1;

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress)
        {
            if (mCallbacks != null)
            {
                mCallbacks.onProgressUpdate(progress[0]);
            }
        }

        @Override
        protected Void doInBackground(Integer... params)
        {
            for (int i = 0; i < params[ID_LOOPS]; i++)
            {
                publishProgress(i);
                SystemClock.sleep(params[ID_DELAY_ONE_LOOPS_MS]);
            }
            return null;
        }
    }


}