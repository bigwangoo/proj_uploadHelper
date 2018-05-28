package com.test.uploadhelper.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test.uploadhelper.R;

import java.util.List;

/**
 * Created by wangyd on 2018/5/28.
 */
public class FileDirListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public FileDirListAdapter(@Nullable List<String> data) {
        super(R.layout.item_file_row, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvName, item)
                .setImageResource(R.id.ivDir, R.drawable.ic_file_dir);
    }
}
