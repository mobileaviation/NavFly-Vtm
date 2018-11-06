package com.mobileaviationtools.nav_fly.Classes;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader extends AsyncTask<String, Integer, String> {
    private Context context;
    private PowerManager.WakeLock mWakeLock;

    private String url;
    public void SetUrl(String url){this.url = url;}

    private String local_dir;
    public void SetLocaDir(String local_dir){this.local_dir = local_dir;}

    private File local_result_file;

    private DownloadInfo downloadInfo;
    public void SetOnDownloadInfo(DownloadInfo downloadInfo){ this.downloadInfo = downloadInfo; }

    public FileDownloader(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.url);
            File urlFile = new File(Uri.parse(this.url).getPath());

            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // Check is the filename is in a header or Url
            String disposition = connection.getHeaderField("Content-Disposition");
            String fileName;
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                    local_result_file = new File(local_dir + fileName);
                }
                else
                    local_result_file = new File(local_dir + urlFile.getName());
            }
            else
            {
                local_result_file = new File(local_dir + urlFile.getName());
            }

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(local_result_file);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            int progress = 0;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                {
                    int p = (int) (total * 100 / fileLength);
                    if (p>progress) {
                        progress = p;
                        publishProgress((int) progress);
                    }
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
//        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        if (downloadInfo != null) downloadInfo.OnProgress(url, local_result_file, progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        if (TextUtils.isEmpty(result))
            if (downloadInfo != null) downloadInfo.OnFinished(url, local_result_file);
            else
            if (downloadInfo != null) downloadInfo.OnError(url, local_result_file, result);
    }

    public interface DownloadInfo
    {
        public void OnProgress(String url, File result_file, Integer progress);
        public void OnError(String url, File result_file, String message);
        public void OnFinished(String url, File result_file);
    }
}
