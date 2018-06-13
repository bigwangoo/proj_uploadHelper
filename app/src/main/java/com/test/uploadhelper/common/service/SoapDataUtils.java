package com.test.uploadhelper.common.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.test.uploadhelper.BuildConfig;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by 46786 on 2018/5/26.
 */
public class SoapDataUtils {

    public static final String METERS = "Meters.dbf";
    public static final String NOTEBOOKGROUP = "NoteBookGroup.dbf";

    private static final String TAG = SoapDataUtils.class.getSimpleName();
    // 1.上传抄表数据
    private static String UpLoadReadMsg = "UpLoadReadMsg";
    // 2.下载抄表数据请求
    private static String GetPreReadMsg = "GetPreReadMsg";

    /**
     * 上传抄表数据
     */
    public static void upLoadReadMsg(Context context, String strReadMsg, HttpResponseListener responseListener) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "doInBackground: " + strReadMsg);
        }

        try {
            SoapObject dataObject = new SoapObject(SoapService.Instance().getNameSpace(), UpLoadReadMsg);
            dataObject.addProperty("strReadMsg", strReadMsg);
            new SoapDataTask(UpLoadReadMsg, responseListener).execute(dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载抄表数据请求
     */
    public static void getPreReadMsg(Context context, String strMeterReaderID, HttpResponseListener responseListener) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "doInBackground: " + strMeterReaderID);
        }

        try {
            SoapObject dataObject = new SoapObject(SoapService.Instance().getNameSpace(), GetPreReadMsg);
            dataObject.addProperty("strMeterReaderID", strMeterReaderID);
            new SoapDataTask(GetPreReadMsg, responseListener).execute(dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class SoapDataTask extends AsyncTask<SoapObject, Void, String> {
        String method = "";
        HttpResponseListener responseListener;

        public SoapDataTask(String method, HttpResponseListener responseListener) {
            this.method = method;
            this.responseListener = responseListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseListener.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(SoapObject... soapObjects) {
            //测试
//            try {
//                Thread.sleep(5 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            return SoapService.Instance().askForResult(soapObjects[0], method);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            responseListener.onPostExecute(s);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            responseListener.onCancelled(s);
        }
    }

    public interface HttpResponseListener {
        void onPreExecute();

        void onPostExecute(String resultJson);

        void onCancelled(String s);
    }
}


