package com.example.instabug.utils;
import android.text.TextUtils;

import com.example.instabug.fields.ListItem;
import com.example.instabug.fields.ListItemValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class Helpers {

    public static String combineUrl(String url, List<StringPair> parameters) {
        if (parameters == null || parameters.size() == 0) return url;
        List<String> list = new ArrayList<>();
        for (StringPair keyAndValue : parameters) {
            list.add(keyAndValue.getFirst() + "=" + keyAndValue.getSecond());
        }
        if (url.indexOf('?') > -1) return url + '&' + TextUtils.join("&", list);
        else return url + '?' + TextUtils.join("&", list);
    }
    public static boolean isUnique(List<ListItem> data, ListItem.ItemType
            type) {
        boolean flag = false;
        for (ListItem iItem : data) {
            if (flag)
                return !(iItem instanceof ListItemValue) || iItem
                        .getRequestSettingType() != type;
            if (iItem instanceof ListItemValue && iItem.getRequestSettingType() == type)
                flag = true;
        }
        return flag;
    }

    public static int lastIndexOf(List<ListItem> data, ListItem.ItemType
            type) {
        for (int i = data.size() - 1; i >= 0; i--) {
            ListItem iItem = data.get(i);
            if (iItem instanceof ListItemValue && iItem.getRequestSettingType() == type)
                return i;
        }
        return -1;
    }

    public static List<CharSequence> SplitString(String text) {
        List<CharSequence> result = new ArrayList<>();
        if (text == null) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
                if (sb.length() > 1024) {
                    sb.setLength(sb.length() - 1);
                    result.add(sb.toString());
                    sb.setLength(0);
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
                result.add(sb.toString());
            }
        } catch (IOException ignored) {
        }
        return result;
    }

    public static String buildHeaders(ArrayList<StringPair> headers)
    {
        StringBuilder str= new StringBuilder();
        for(int i=0; i<headers.size();i++)
        {
            StringPair item=headers.get(i);
            str.append(item.getFirst()).append(": ").append(item.getSecond()).append("/n");
        }
        return str.toString();
    }

}
