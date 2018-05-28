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
public class GroupListAdapter extends BaseQuickAdapter<Map<String, Object>, BaseViewHolder> {

    public GroupListAdapter(@Nullable List<Map<String, Object>> data) {
        super(R.layout.item_list_group, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Map<String, Object> item) {
        String notebookid = "";
        String groupid = "";
        String groupname = "";
        String limitnum = "";
        try {
            notebookid = item.get("NOTEBOOKID").toString();
            groupid = item.get("GROUPID").toString();
            groupname = item.get("GROUPNAME").toString();
            limitnum = item.get("LIMITNUM").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        helper.setText(R.id.tvItem0, notebookid)
                .setText(R.id.tvItem1, groupid)
                .setText(R.id.tvItem2, groupname)
                .setText(R.id.tvItem3, limitnum);
    }
}
