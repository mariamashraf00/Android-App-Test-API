package com.example.instabug.fields;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.instabug.R;
import com.example.instabug.utils.Helpers;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.List;

public class ListItemTitle extends
        ListItem<ListItemTitle, ListItemTitle
                        .MyViewHolder> {

    public ListItemTitle(ItemType requestSettingType) {
        super(requestSettingType);
    }

    @Override
    public int getType() {
        return R.id.request_setting_list_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_item_title;
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view);
    }

    @Override
    public void bindView(MyViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        switch (requestSettingType) {
            case header:
                holder.textViewTitle.setText(R.string.main_view_headers);
                holder.imageButtonAdd.setVisibility(View.VISIBLE);
                break;
            case parameter:
                holder.textViewTitle.setText(R.string.main_view_params);
                holder.imageButtonAdd.setVisibility(View.VISIBLE);
                break;
            case body:
                holder.textViewTitle.setText(R.string.main_view_body);
                holder.imageButtonAdd.setVisibility(View.VISIBLE);
                break;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageButton imageButtonAdd;

        MyViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            imageButtonAdd = view.findViewById(R.id.imageButtonAdd);
        }
    }

    public static class AddEvent extends ClickEventHook<ListItem> {

        @Override
        public void onClick(View v, int position, FastAdapter<ListItem>
                fastAdapter, ListItem item) {
            ItemType type = item.getRequestSettingType();
            FastItemAdapter<ListItem> fastItemAdapter =
                    (FastItemAdapter<ListItem>) fastAdapter;
            fastItemAdapter.add(Helpers.lastIndexOf(fastItemAdapter
                    .getAdapterItems(), type) + 1, new ListItemValue(type));
        }

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof ListItemTitle.MyViewHolder) {
                return ((MyViewHolder) viewHolder).imageButtonAdd;
            }
            return null;
        }
    }
}