package com.example.instabug;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.example.instabug.utils.Helpers;
import androidx.fragment.app.Fragment;
public class ResponseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response, container, false);
        ListView listView = view.findViewById(R.id.listView);
        ResponseActivity activity = ((ResponseActivity) getActivity());
        if (activity != null) {
            listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.response_textview, Helpers.SplitString(activity.responseBody)));
            registerForContextMenu(listView);
        }
        return view;
    }


}
