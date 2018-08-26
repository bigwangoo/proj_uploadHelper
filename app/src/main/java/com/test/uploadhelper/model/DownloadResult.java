package com.test.uploadhelper.model;

import android.support.annotation.Keep;

/**
 * Created by wangyd on 2018/5/28.
 */
@Keep
public class DownloadResult {

    /**
     * outResult : 1
     * outMsg : 成功
     * MetersDbfName : 0010_20180524_Meters.dbf
     * NoteBookGroupDbfName : 0010_20180524_NoteBookGroupDbfName.dbf
     */

    private int outResult;
    private String outMsg;
    private String MetersDbfName;
    private String NoteBookGroupDbfName;

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

    public String getMetersDbfName() {
        return MetersDbfName;
    }

    public void setMetersDbfName(String MetersDbfName) {
        this.MetersDbfName = MetersDbfName;
    }

    public String getNoteBookGroupDbfName() {
        return NoteBookGroupDbfName;
    }

    public void setNoteBookGroupDbfName(String NoteBookGroupDbfName) {
        this.NoteBookGroupDbfName = NoteBookGroupDbfName;
    }
}
