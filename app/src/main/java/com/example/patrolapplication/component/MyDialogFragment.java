package com.example.patrolapplication.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.patrolapplication.network.DataController;
import com.example.patrolapplication.network.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;

public class MyDialogFragment extends DialogFragment {
    private static final String TAG = "## MyDialogFragment ## ";

    private String[] items;
    private boolean[] checkItems;
    private MyDialogListener listener;

    public MyDialogFragment(String[] items, MyDialogListener listener) {
        this.items = items;
        this.listener = listener;
        checkItems = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            checkItems[i] = false;
        }
    }

    public interface MyDialogListener{
        void onConfirmClick(boolean[] normalList);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("检查条目");
        builder.setMultiChoiceItems(items, checkItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkItems[which] = isChecked;
                    }
                });
        builder.setPositiveButton("确认无误", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onConfirmClick(checkItems);
            }
        });
        return builder.create();
    }
}
