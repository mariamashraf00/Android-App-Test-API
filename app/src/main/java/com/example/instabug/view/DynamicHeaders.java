package com.example.instabug.view;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.instabug.fields.ListItem;
import com.example.instabug.R;


public class DynamicHeaders extends ConstraintLayout {
    TextView title;
    ImageButton button;

    ListItem.ItemType type = ListItem.ItemType.header;

    public DynamicHeaders(Context context) {
        super(context);
        init();
    }

    public DynamicHeaders(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicHeaders(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        inflate(getContext(), R.layout.list_item_title, this);
        title = findViewById(R.id.textViewTitle);
        button = findViewById(R.id.imageButtonAdd);
    }

    public void setButtonOnClickListener(OnClickListener onClickListener) {
        button.setOnClickListener(onClickListener);
    }

    public ListItem.ItemType getType() {
        return type;
    }

    public void setType(ListItem.ItemType type) {
        if (this.type == type || type == null) return;
        this.type = type;
        switch (type) {
            case header:
                title.setText(R.string.main_view_headers);
                button.setVisibility(VISIBLE);
                break;
            case parameter:
                title.setText(R.string.main_view_params);
                button.setVisibility(VISIBLE);
                break;
            case body:
                title.setText(R.string.main_view_body);
                button.setVisibility(INVISIBLE);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
