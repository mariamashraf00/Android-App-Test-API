package com.example.instabug;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.instabug.fields.ListItem;
import com.example.instabug.fields.ListItemTitle;
import com.example.instabug.fields.ListItemValue;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.example.instabug.utils.StringPair;
import com.example.instabug.utils.Helpers;
import com.example.instabug.view.DynamicHeaders;
import java.util.ArrayList;



public class MainFragment extends Fragment {
    FastItemAdapter<ListItem> adapter;
    DynamicHeaders dynamicHeaders;
    Spinner spinnerMethod;
    Spinner spinnerHttp;
    EditText editTextUrl;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        spinnerMethod = view.findViewById(R.id.spinnerMethods);
        spinnerHttp = view.findViewById(R.id.spinnerHttp);
        editTextUrl = view.findViewById(R.id.editTextUrl);
        final Button buttonSend = view.findViewById(R.id.buttonSend);
        editTextUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    buttonSend.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith("http://")) {
                    spinnerHttp.setSelection(0);
                    s.delete(0, 7);
                } else if (s.toString().startsWith("https://")) {
                    spinnerHttp.setSelection(1);
                    s.delete(0, 8);
                }
            }
        });
        dynamicHeaders = view.findViewById(R.id.stickyHeader);
        adapter = new FastItemAdapter<>();
        adapter.setHasStableIds(true);
        adapter.withEventHook(new ListItemTitle.AddEvent()).withEventHook(new
                ListItemValue.RemoveEvent()).withEventHook(new
                ListItemValue.textChangeEvent());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if (savedInstanceState == null) {
            adapter.add(new ListItemTitle(ListItem.ItemType.header), new
                    ListItemValue(ListItem.ItemType.header), new
                    ListItemTitle(ListItem.ItemType.parameter), new
                    ListItemValue(ListItem.ItemType.parameter), new
                    ListItemTitle(ListItem.ItemType.body), new
                    ListItemValue(ListItem.ItemType.body));
        }
        dynamicHeaders.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListItem.ItemType type = dynamicHeaders.getType();
                adapter.add(Helpers.lastIndexOf(adapter.getAdapterItems(), type) + 1, new ListItemValue(type));
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ResponseActivity.class);
                intent.putExtra("method", spinnerMethod.getSelectedItem().toString());
                intent.putExtra("http", spinnerHttp.getSelectedItem().toString());
                intent.putExtra("url", editTextUrl.getText().toString());
                intent.putExtra("header", buildPairs(ListItem.ItemType.header));
                intent.putExtra("parameter",buildPairs(ListItem.ItemType.parameter));
                intent.putExtra("body", buildPairs(ListItem.ItemType.body));
                startActivity(intent);
            }
        });

        return view;
    }

    public ArrayList<StringPair> buildPairs(ListItem.ItemType type) {
        ArrayList<StringPair> pairs = new ArrayList<>();
        for (ListItem iItem : adapter.getAdapterItems()) {
            ListItemValue item;
            if (iItem instanceof ListItemValue && (item = (ListItemValue)
                    iItem).getRequestSettingType() == type) {
                String key;
                if ((key = item.getKey()) != null && !key.isEmpty()) {
                    String value = item.getValue();
                    pairs.add(new StringPair(key, value == null ? "" : value));
                }
            }
        }
        return pairs;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveAdapter(outState);
    }

    private void saveAdapter(Bundle outState) {
        outState = adapter.saveInstanceState(outState);
        outState.putSerializable("dynamicHeaders", dynamicHeaders.getType());
    }

}
