package com.test.uploadhelper.common.service;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
    public static String upLoadReadMsg(Context context, String strReadMsg) {
        Log.e(TAG, "doInBackground: " + strReadMsg);

        String result = "";
        try {
            SoapObject dataObject = new SoapObject(SoapService.Instance().getNameSpace(), UpLoadReadMsg);
            dataObject.addProperty("strReadMsg", strReadMsg);
            result = new SoapDataTask(UpLoadReadMsg).execute(dataObject).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(context, "网络请求出错", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    /**
     * 下载抄表数据请求
     */
    public static String getPreReadMsg(Context context, String strMeterReaderID) {
        Log.e(TAG, "doInBackground: " + strMeterReaderID);

        String result = "";
        try {
            SoapObject dataObject = new SoapObject(SoapService.Instance().getNameSpace(), GetPreReadMsg);
            dataObject.addProperty("strMeterReaderID", strMeterReaderID);
            result = new SoapDataTask(GetPreReadMsg).execute(dataObject).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(context, "网络请求出错", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    static class SoapDataTask extends AsyncTask<SoapObject, Void, String> {
        String method = "";

        public SoapDataTask(String method) {
            this.method = method;
        }

        @Override
        protected String doInBackground(SoapObject... soapObjects) {
            return SoapService.Instance().askForResult(soapObjects[0], method);
        }
    }
}


