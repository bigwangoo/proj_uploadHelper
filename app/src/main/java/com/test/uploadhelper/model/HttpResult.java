package com.test.uploadhelper.model;

import android.support.annotation.Keep;

/**
 * Created by wangyd on 2018/5/28.
 */
@Keep
public class HttpResult {

    /**
     * outResult : 1
     * outMsg :  成功
     * totalCounts : 1
     */

    private int outResult;
    private String outMsg;
    private int totalCounts;

    public int getOutResult() {
        return outResult;
    }

    public void setOutResult(int outResult) {
        this.outResult = outResult;
    }

    public String getOutMsg() {
        return outMsg;
    }

    public void setOutMsg(String outMsg) {
        this.outMsg = outMsg;
    }

    public int getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }
}
