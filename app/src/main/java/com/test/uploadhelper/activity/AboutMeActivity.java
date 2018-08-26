package com.test.uploadhelper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.uploadhelper.R;

/**
 * Created by wangyd on 2018/6/13.
 */
public class AboutMeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("使用说明");

        TextView tv1 = (TextView) findViewById(R.id.tv_1);
        TextView tv2 = (TextView) findViewById(R.id.tv_2);
        TextView tv3 = (TextView) findViewById(R.id.tv_3);
        TextView tv4 = (TextView) findViewById(R.id.tv_4);
        TextView tv5 = (TextView) findViewById(R.id.tv_5);
        TextView tv6 = (TextView) findViewById(R.id.tv_6);
        TextView tv7 = (TextView) findViewById(R.id.tv_7);
        ImageView iv7 = (ImageView) findViewById(R.id.iv_7);
        TextView tv8 = (TextView) findViewById(R.id.tv_8);

        tv1.setText("1、本软件是数据传输用，请勿滥用");
        tv2.setText("2、使用时请授予软件读取存储卡权限");
        tv3.setText("3、使用时请先编辑配置，设置相应参数");
        tv4.setText("4、上传前请使用预览功能，校验数据是否正确");
        tv5.setText("5、上传后数据后文件生成可能需要一定时间，请耐心等待");
        tv6.setText("6、使用过程中有任何问题或建议请及时向管理员反馈");
        tv7.setText("7、部分手机没有SD卡目录请参考下面设置");
        Glide.with(this).load(R.drawable.tip_helper).into(iv7);
        tv8.setText("8、友情提示微信、QQ传配置文件时,默认保存地址\n" +
                "QQ：存储设备>Tencent>QQfile_recv\n" +
                "微信：存储设备>Tencent>MicroMsg>Download\n");
    }

}
