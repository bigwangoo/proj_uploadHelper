package com.test.uploadhelper.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test.uploadhelper.R;

import java.util.List;
import java.util.Map;

/**
 * @author wangyd
 * @date 2018/5/23
 * @description description
 */
public class MeterListAdapter extends BaseQuickAdapter<Map<String, Object>, BaseViewHolder> {

    public MeterListAdapter(@Nullable List<Map<String, Object>> data) {
        super(R.layout.item_list_meter, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Map<String, Object> item) {
        String meterid = "";
        String usercode = "";
        String groupid = "";
        String cbzt = "";
        String readnum = "";
        try {
            meterid = item.get("METERID").toString();
            usercode = item.get("USERCODE").toString();
            groupid = item.get("GROUPID").toString();
            cbzt = item.get("CBZT").toString();
            readnum = item.get("READNUM").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.setText(R.id.tvItem0, meterid)
                .setText(R.id.tvItem1, usercode)
                .setText(R.id.tvItem2, groupid)
                .setText(R.id.tvItem3, cbzt)
                .setText(R.id.tvItem4, readnum);
    }
}
