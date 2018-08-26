package com.test.uploadhelper.model;

import android.support.annotation.Keep;

/**
 * @author wangyd
 * @date 2018/5/23
 * @description description
 */
@Keep
public class NoteBookGroup {

    private String NOTEBOOKID;
    private String GROUPID;
    private String GROUPNAME;
    private String LIMITNUM;
    private String REMARK;
    private String MIYAO;

    public String getNOTEBOOKID() {
        return NOTEBOOKID;
    }

    public void setNOTEBOOKID(String NOTEBOOKID) {
        this.NOTEBOOKID = NOTEBOOKID;
    }

    public String getGROUPID() {
        return GROUPID;
    }

    public void setGROUPID(String GROUPID) {
        this.GROUPID = GROUPID;
    }

    public String getGROUPNAME() {
        return GROUPNAME;
    }

    public void setGROUPNAME(String GROUPNAME) {
        this.GROUPNAME = GROUPNAME;
    }

    public String getLIMITNUM() {
        return LIMITNUM;
    }

    public void setLIMITNUM(String LIMITNUM) {
        this.LIMITNUM = LIMITNUM;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getMIYAO() {
        return MIYAO;
    }

    public void setMIYAO(String MIYAO) {
        this.MIYAO = MIYAO;
    }
}
