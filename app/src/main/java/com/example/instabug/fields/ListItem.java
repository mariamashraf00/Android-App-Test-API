package com.example.instabug.fields;

import androidx.recyclerview.widget.RecyclerView;
import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;


public abstract class ListItem<Item extends IItem & IClickable, VH extends
        RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements Serializable {
    ItemType requestSettingType;

    ListItem(ItemType requestSettingType) {
        this.requestSettingType = requestSettingType;
    }

    public ItemType getRequestSettingType() {
        return requestSettingType;
    }

    public enum ItemType {
        header, parameter, body
    }
}
