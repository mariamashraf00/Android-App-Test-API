package com.example.instabug.fields;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.instabug.R;
import com.example.instabug.utils.Helpers;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.CustomEventHook;
import java.util.Arrays;
import java.util.List;

public class ListItemValue extends
        ListItem<ListItemValue, ListItemValue
                        .MyViewHolder> {
    private String key;
    private String value;

    public ListItemValue(ItemType requestSettingType) {
        super(requestSettingType);
    }


    ListItemValue(ItemType requestSettingType, String key, String value) {
        super(requestSettingType);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int getType() {
        switch (requestSettingType) {
            case header:
                return R.id.request_setting_list_header;
            case parameter:
            default:
                return R.id.request_setting_list_param;
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_item_value;
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view, requestSettingType == ItemType.header);
    }

    @Override
    public void bindView(MyViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.textViewKey.setText(key);
        holder.textViewValue.setText(value);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView textViewKey;
        EditText textViewValue;
        ImageButton imageButtonRemove;

        MyViewHolder(final View view, boolean isHttpHeader) {
            super(view);
            textViewKey = view.findViewById(R.id.textViewKey);
            if (isHttpHeader) {
                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(view.getContext(),
                        android.R.layout.simple_dropdown_item_1line, view.getContext()
                        .getResources().getStringArray(R.array.headers_array));
                textViewKey.setAdapter(autoCompleteAdapter);
            }
            textViewValue = view.findViewById(R.id.textViewValue);
            imageButtonRemove = view.findViewById(R.id.imageButtonRemove);
        }
    }

    public static class RemoveEvent extends ClickEventHook<ListItem> {

        @Override
        public void onClick(View v, int position, FastAdapter<ListItem>
                fastAdapter, ListItem item) {
            ListItemValue kvItem = (ListItemValue) item;
            FastItemAdapter<ListItem> fastItemAdapter =
                    (FastItemAdapter<ListItem>) fastAdapter;
            if (position < 0 || position >= fastItemAdapter.getItemCount()) return;
            if (Helpers.isUnique(fastItemAdapter.getAdapterItems(), kvItem
                    .getRequestSettingType())) {
                if ((kvItem.getKey() != null && kvItem.getKey().length() > 0) || (kvItem.getValue
                        () != null && kvItem.getValue().length() > 0)) {
                    kvItem.setKey(null);
                    kvItem.setValue(null);
                    fastItemAdapter.notifyAdapterItemChanged(position);
                }
                return;
            }
            v.getRootView().clearFocus();
            InputMethodManager keyboard = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
            fastItemAdapter.remove(position);
        }

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof ListItemValue.MyViewHolder) {
                return ((MyViewHolder) viewHolder).imageButtonRemove;
            }
            return null;
        }
    }

    public static class textChangeEvent extends CustomEventHook<ListItem> {

        @Override
        public void attachEvent(View view, final RecyclerView.ViewHolder viewHolder) {
            if (view.getId() == R.id.textViewKey) {
                ((EditText) view).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ListItemValue item = ((ListItemValue) getItem
                                (viewHolder));
                        if (item == null) return;
                        item.setKey(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().indexOf('\n') != -1 || s.toString().indexOf('\r') != -1) {
                            String newString = s.toString().replaceAll("[\r\n]+", "");
                            s.clear();
                            s.append(newString);
                        }
                    }
                });
            } else if (view.getId() == R.id.textViewValue) {
                ((EditText) view).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ListItemValue item = ((ListItemValue) getItem
                                (viewHolder));
                        if (item == null) return;
                        item.setValue(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().indexOf('\n') != -1 || s.toString().indexOf('\r') != -1) {
                            String newString = s.toString().replaceAll("[\r\n]+", " ");
                            s.clear();
                            s.append(newString);
                        }
                    }
                });
            }
        }

        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof ListItemValue.MyViewHolder) {
                return Arrays.asList(((MyViewHolder) viewHolder).textViewKey, (
                        (MyViewHolder) viewHolder).textViewValue);
            }
            return null;
        }
    }
}
