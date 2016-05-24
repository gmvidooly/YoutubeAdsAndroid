package com.example.gulshan.youtubewebview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String VIDEO_ID_URL = "";
    private static final String VID_PREFX = "Video_Ids_";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ConnectionClass connectionClass;
    SharedPreferences sharedpreferences;

    private WebView mWebView;
    private boolean isScriptRunning = false;
    private int indexProcessed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        connectionClass = new ConnectionClass();

//        initWebView();
    }

    //    public void connectToDatabase(View v) {
//        GetVideoIds gvi = new GetVideoIds();
//        gvi.execute("");
//    }
//
    @Override
    protected void onResume() {
        super.onResume();
        isScriptRunning = false;
        try {
            WebView.class.getMethod("onResume").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String JsonIds = sharedpreferences.getString(VID_PREFX + MyUtils.getCurrentDateAsFormat("dd-MM-yyyy"), "");
        if(!JsonIds.isEmpty()) {
            try {
                JSONObject jsnobject = new JSONObject(JsonIds);
                JSONArray jsonArray = jsnobject.getJSONArray("ids");
                indexProcessed = Integer.parseInt(sharedpreferences.getString("indexProcessed","0"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        if(isScriptRunning) {
            isScriptRunning = false;
            saveDataToSharedPreference("indexProcessed", String.valueOf(indexProcessed));
        }
        try {
            WebView.class.getMethod("onPause").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE))
            {
                mWebView.setWebContentsDebuggingEnabled(true);
            }
        }
//        mWebView.setWebViewClient(new MyWebViewClient());
//        mWebView.setWebChromeClient(new MyWebChromeClient());


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                mWebView.loadUrl("javascript:window.HtmlViewer.showHTML" +
//                        "('<body>'+document.getElementsByTagName('body')[0].getElementsByTagName('div')[0].getElementsByTagName('div')[0].getElementsByTagName('div')[0].getElementsByTagName('div')[0].innerHTML+'</body>');");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        try {
                            mWebView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                                    "(document.getElementsByTagName('body')[0].getElementsByTagName('div')[0].getElementsByTagName('div')[0].getElementsByTagName('div')[0].getElementsByTagName('div')[0].getElementsByTagName('div')[0].getElementsByTagName('video')[0].innerHTML);");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 10000);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //Required functionality here
                Log.d("GKM", "JS ALERT m= " + message + " result " + result);
                return super.onJsAlert(view, url, message, result);

            }
        });
//        mWebView.loadData(html, "text/html", null);

        mWebView.loadUrl("https://m.youtube.com/watch?v=3ZKzByjI7uM");
    }


    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @android.webkit.JavascriptInterface
        public void showHTML(String html) {
//            new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(html)
//                    .setPositiveButton(android.R.string.ok, null).setCancelable(true).create().show();
            Log.d("GKM", "Html content is " + html);
            appendLog(html, ctx);
        }


    }

    public void appendLog(String data, Context ctx) {
        if (data == null || data.length() < 11) {
            Log.d(TAG, "No Ads");
            return;
        }
        String path = Environment.getExternalStorageDirectory() + "/Youtubelog.txt";
        Log.d("GKM", "Path is " + path);
        for (int i = 0; i < data.length() - 3; i++) {
            if (data.charAt(i) == ';' && data.charAt(i + 1) == 'v' && data.charAt(i + 2) == '=') {
                try {
                    data = data.substring(i + 3, i + 13);
                    new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(data)
                            .setPositiveButton(android.R.string.ok, null).setCancelable(true).create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "No Ads Length");
                }
                Log.d(TAG, "Ad Vid is " + data);

                break;
            }
        }
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:alert(getSubject())");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("GKM ", message);
            result.confirm();
            return true;
        }
    }


    public class getVideoIdsData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(VIDEO_ID_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            saveDataToSharedPreference(VID_PREFX + MyUtils.getCurrentDateAsFormat("dd-MM-yyyy"), result);
        }

    }

    private void saveDataToSharedPreference(String key, String result) {
        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
        prefsEditor.putString(key, result);
        prefsEditor.commit();
    }


    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
