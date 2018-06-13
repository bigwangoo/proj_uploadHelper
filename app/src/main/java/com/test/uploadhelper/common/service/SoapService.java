package com.test.uploadhelper.common.service;

import android.util.Log;

import com.test.uploadhelper.BuildConfig;
import com.test.uploadhelper.utils.SharedPrefHelper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by 46786 on 2018/5/27.
 */

public class SoapService {

    private static final String TAG = SoapService.class.getSimpleName();

    // 默认测试接口临时地址
    public static String URL = "http://123.184.42.167:5588/WebServices.asmx";
    // 默认文件下载临时地址目录
    public static String DOWN_URL = "http://123.184.42.167:5588/DownFiles/";

    // 命名空间
    private static final String NAMESPACE = "http://tempuri.org/";
    // SoapAction
    private static final String SOAP_ACTION = "http://tempuri.org/";

    private static SoapService instance;

    private SoapService() {

    }

    public static SoapService Instance() {
        if (instance == null) {
            synchronized (SoapService.class) {
                if (instance == null) {
                    instance = new SoapService();
                }
            }
        }
        return instance;
    }

    public String getNameSpace() {
        return NAMESPACE;
    }

    public String askForResult(SoapObject paramObject, String methodName) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "askForResult: " + methodName);
        }

        SoapObject result = null;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // new MarshalBase64().register(envelope);
        envelope.bodyOut = paramObject;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(paramObject);

        String soapAction = SOAP_ACTION + methodName;
        URL = SharedPrefHelper.getInstance().getServerUrl();
        HttpTransportSE transportSE = new HttpTransportSE(URL, 60 * 60 * 1000);
        try {
            transportSE.debug = true;
            transportSE.call(soapAction, envelope);
            result = (SoapObject) envelope.bodyIn;

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "askForResult: " + result);
            }
        } catch (Exception e) {
            //调试用
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, "askForResult: 抛出异常为= " + sw.getBuffer().toString());
        }
        if (result != null) {
            return result.getProperty(0).toString();
        } else {
            return "";
        }
    }
}
