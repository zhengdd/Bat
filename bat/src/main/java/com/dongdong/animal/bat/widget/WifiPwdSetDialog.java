package com.dongdong.animal.bat.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.dongdong.animal.bat.R;
import com.dongdong.animal.bat.viewmodel.exts.Act2;

public class WifiPwdSetDialog extends DialogFragment {

    private Context context;
    private TextView mTvSSID;
    private EditText mEtPwd;
    private TextView mTvCancel;
    private TextView mTvSubmit;
    private CheckBox mCbShowPwd;

    private String wifiSSID = "";

    private Act2<String, String> listener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加这句话去掉自带的标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setGravity(Gravity.CENTER);

        View view = inflater.inflate(R.layout.dialog_wifi_pwd_input, container);

        mTvSSID = (TextView) view.findViewById(R.id.tv_dialog_wifi_name);
        mTvSSID.setText(wifiSSID);
        mEtPwd = (EditText) view.findViewById(R.id.et_dialog_wifi_pwd);
        mTvCancel = (TextView) view.findViewById(R.id.tv_dialog_wifi_cancel);
        mTvSubmit = (TextView) view.findViewById(R.id.tv_dialog_wifi_submit);
        mCbShowPwd = (CheckBox) view.findViewById(R.id.cb_dialog_wifi_show_pwd);

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() >= 8) {
                    mTvSubmit.setEnabled(true);
                } else {
                    mTvSubmit.setEnabled(false);
                }
            }
        });

        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtPwd.setInputType(InputType.TYPE_NULL);
                } else {
                    mEtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        mTvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !TextUtils.isEmpty(mEtPwd.getText().toString().trim())) {
                    listener.run(wifiSSID, mEtPwd.getText().toString().trim());
                }
                dismiss();
            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.context = activity.getBaseContext();
        }
    }

    public void setSSID(String ssid) {
        this.wifiSSID = ssid;
    }

    public void setListener(Act2<String, String> listener) {
        this.listener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}

