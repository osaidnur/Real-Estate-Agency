package com.example.a1210733_1211088_courseproject.api;

import android.os.AsyncTask;
import com.example.a1210733_1211088_courseproject.api.HttpManager;

/**
 * AsyncTask for fetching property data from the API
 */
public class PropertyConnectionTask extends AsyncTask<String, Void, String> {

    // Interface for callback when the task is completed
    public interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }

    private OnTaskCompleted listener;

    // Set the listener for task completion
    public void setOnTaskCompleted(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = null;
        if (urls.length > 0) {
            String url = urls[0];
            HttpManager httpManager = new HttpManager();
            result = httpManager.getData(url);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onTaskCompleted(result);
        }
    }
}
