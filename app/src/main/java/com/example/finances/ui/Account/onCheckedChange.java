package com.example.finances.ui.Account;

import android.content.ComponentCallbacks;
import android.widget.CompoundButton;

public interface onCheckedChange extends ComponentCallbacks {
    void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
}
