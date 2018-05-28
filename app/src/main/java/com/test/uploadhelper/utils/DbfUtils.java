package com.test.uploadhelper.utils;

import android.util.Log;

import com.test.uploadhelper.common.jdbf.core.DbfMetadata;
import com.test.uploadhelper.common.jdbf.core.DbfRecord;
import com.test.uploadhelper.common.jdbf.reader.DbfReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangyd
 * @date 2018/5/23
 * @description description
 */
public class DbfUtils {

    private static final String TAG = DbfUtils.class.getSimpleName();

    private static String NoteBookGroup = "NoteBookGroup.dbf";
    private static String Meters = "Meters.dbf";

    public static List<Map<String, Object>> getNoteBookGroup() {
        String groupPath = SharedPrefHelper.getInstance().getNoteBookGroupPath();
        return DBF2List(groupPath);
    }

    public static List<Map<String, Object>> getMeters() {
        String groupPath = SharedPrefHelper.getInstance().getMetersPath();
        return DBF2List(groupPath);
    }

    public static List<Map<String, Object>> DBF2List(String filePath) {
        List<Map<String, Object>> data = new ArrayList<>();
        try {
            InputStream dbf = new FileInputStream(new File(filePath));
            Charset stringCharset = Charset.forName("GBK");

            DbfReader reader = new DbfReader(dbf);
            // DbfMetadata meta = reader.getMetadata();

            DbfRecord rec;
            while ((rec = reader.read()) != null) {
                rec.setStringCharset(stringCharset);
                Map<String, Object> map = rec.toMap();
                data.add(map);

                //Log.e(TAG, "DBF2List: " + rec.toMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
