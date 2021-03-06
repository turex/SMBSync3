package com.sentaroh.android.SMBSync3;
/*
The MIT License (MIT)
Copyright (c) 2020 Sentaroh

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.sentaroh.android.Utilities3.Dialog.CommonDialog;
import com.sentaroh.android.Utilities3.NotifyEvent;
import com.sentaroh.android.Utilities3.ThemeUtil;

public class ActivityPasswordSettings extends AppCompatActivity {
    private GlobalParameters mGp=null;
    private Activity mActivity=null;
    private CommonUtilities mUtil = null;

    private LinearLayout mPreferenceView=null;

    private TextView mAppPswdMsg=null;
    private Button mButtonCreate=null;
    private Button mButtonChange=null;
    private Button mButtonRemove=null;
    private Button mButtonClose=null;

    private String mCreatedPassword="";

    private CheckedTextView mCtvSettingTimeOut=null;
    private CheckedTextView mCtvSettingAppStartup=null;
    private CheckedTextView mCtvSettingUseEditTask=null;
    private CheckedTextView mCtvSettingUseExportTask=null;
    private CheckedTextView mCtvSettingInitSmbAccount=null;

    private CheckedTextView mCtvSettingInitZipPassword=null;
    private CheckedTextView mCtvSettingHideShowPasswordButton=null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(new GlobalParameters().setNewLocale(base, false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = ActivityPasswordSettings.this;
        mGp= GlobalWorkArea.getGlobalParameter(mActivity);
        setTheme(mGp.applicationTheme);
        if (mGp.themeColorList == null) {
            mGp.themeColorList = ThemeUtil.getThemeColorList(mActivity);
        }

        //Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preference_application_password_dlg);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mUtil = new CommonUtilities(mActivity, "AppPswd", mGp, getSupportFragmentManager());

        setResult(RESULT_OK);

        mPreferenceView=(LinearLayout)findViewById(R.id.preference_application_password_dlg_view);

        mAppPswdMsg=(TextView)findViewById(R.id.preference_application_password_password_status);

        mButtonCreate=(Button)findViewById(R.id.preference_application_password_create_button);
        mButtonChange=(Button)findViewById(R.id.preference_application_password_change_button);
        mButtonRemove=(Button)findViewById(R.id.preference_application_password_remove_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        mCtvSettingTimeOut=(CheckedTextView)findViewById(R.id.preference_application_password_setting_time_out);
        mCtvSettingAppStartup=(CheckedTextView)findViewById(R.id.preference_application_password_setting_use_app_startup);
        mCtvSettingUseEditTask=(CheckedTextView)findViewById(R.id.preference_application_password_setting_use_edit_task);
        mCtvSettingUseExportTask=(CheckedTextView)findViewById(R.id.preference_application_password_setting_use_export_task);
        mCtvSettingInitSmbAccount=(CheckedTextView)findViewById(R.id.preference_application_password_setting_init_smb_account_password);

        mCtvSettingInitZipPassword=(CheckedTextView)findViewById(R.id.preference_application_password_setting_init_zip_password);
        mCtvSettingHideShowPasswordButton=(CheckedTextView)findViewById(R.id.preference_application_password_setting_init_hide_show_password);

        mPreferenceView.setVisibility(LinearLayout.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NotifyEvent ntfy_auth=new NotifyEvent(mActivity);
        ntfy_auth.setListener(new NotifyEvent.NotifyEventListener() {
            @Override
            public void positiveResponse(Context context, Object[] objects) {
                mPreferenceView.setVisibility(LinearLayout.VISIBLE);
                setAppPswdStatus();
                setPasswordButtonListener();
                setProtectItemButtonListener();
            }

            @Override
            public void negativeResponse(Context context, Object[] objects) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        ApplicationPassword.authentication(mGp, mActivity, getSupportFragmentManager(), mUtil,
                true, ntfy_auth, ApplicationPassword.APPLICATION_PASSWORD_RESOURCE_INVOKE_SECURITY_SETTINGS);

    }

    private void setProtectItemButtonListener() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mCtvSettingTimeOut.setChecked(prefs.getBoolean(mActivity.getString(R.string.settings_security_use_auth_timeout), true));
        mCtvSettingTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(isChecked);
                prefs.edit().putBoolean(mActivity.getString(R.string.settings_security_use_auth_timeout), isChecked).commit();
            }
        });
        mCtvSettingAppStartup.setChecked(prefs.getBoolean(mActivity.getString(R.string.settings_security_application_password_use_app_startup), false));
        mCtvSettingAppStartup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(isChecked);
                prefs.edit().putBoolean(mActivity.getString(R.string.settings_security_application_password_use_app_startup), isChecked).commit();
//                if (isChecked) {
//                    mCtvSettingUseEditTask.setEnabled(false);
//                    mCtvSettingUseExportTask.setEnabled(false);
//                } else {
//                    mCtvSettingUseEditTask.setEnabled(true);
//                    mCtvSettingUseExportTask.setEnabled(true);
//                }
            }
        });
        mCtvSettingUseEditTask.setChecked(prefs.getBoolean(mActivity.getString(R.string.settings_security_application_password_use_edit_task), false));
        mCtvSettingUseEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(isChecked);
                prefs.edit().putBoolean(mActivity.getString(R.string.settings_security_application_password_use_edit_task), isChecked).commit();
            }
        });
        mCtvSettingUseExportTask.setChecked(prefs.getBoolean(mActivity.getString(R.string.settings_security_application_password_use_export_task), false));
        mCtvSettingUseExportTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(isChecked);
                prefs.edit().putBoolean(mActivity.getString(R.string.settings_security_application_password_use_export_task), isChecked).commit();
            }
        });
        mCtvSettingInitSmbAccount.setChecked(prefs.getBoolean(mActivity.getString(R.string.settings_security_init_smb_account_password), false));
        mCtvSettingInitSmbAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(isChecked);
                prefs.edit().putBoolean(mActivity.getString(R.string.settings_security_init_smb_account_password), isChecked).commit();
            }
        });

        mCtvSettingInitZipPassword.setChecked(prefs.getBoolean(mActivity.getString(R.string.settings_security_init_zip_passowrd), false));
        mCtvSettingInitZipPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(isChecked);
                prefs.edit().putBoolean(mActivity.getString(R.string.settings_security_init_zip_passowrd), isChecked).commit();
            }
        });
        mCtvSettingHideShowPasswordButton.setChecked(prefs.getBoolean(mActivity.getString(R.string.settings_security_hide_show_passowrd), false));
        mCtvSettingHideShowPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !((CheckedTextView) v).isChecked();
                ((CheckedTextView) v).setChecked(isChecked);
                prefs.edit().putBoolean(mActivity.getString(R.string.settings_security_hide_show_passowrd), isChecked).commit();
            }
        });

    }

    private void setPasswordButtonListener() {
        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotifyEvent ntfy_create=new NotifyEvent(mActivity);
                ntfy_create.setListener(new NotifyEvent.NotifyEventListener() {
                    @Override
                    public void positiveResponse(Context context, Object[] objects) {
                        String new_hv=(String)objects[0];
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
                        saveApplicationPasswordHashValue(prefs, new_hv);
                        setAppPswdStatus();
                    }

                    @Override
                    public void negativeResponse(Context context, Object[] objects) {
                        setAppPswdStatus();
                    }
                });
                ApplicationPassword.createPassword(mGp, mActivity, getSupportFragmentManager(), mUtil,
                        mActivity.getString(R.string.settings_security_application_password_desc_create), ntfy_create);
            }
        });

        mButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotifyEvent ntfy_verify=new NotifyEvent(mActivity);
                ntfy_verify.setListener(new NotifyEvent.NotifyEventListener() {
                    @Override
                    public void positiveResponse(Context context, Object[] objects) {
                        NotifyEvent ntfy_create=new NotifyEvent(mActivity);
                        ntfy_create.setListener(new NotifyEvent.NotifyEventListener() {
                            @Override
                            public void positiveResponse(Context context, Object[] objects) {
                                String new_hv=(String)objects[0];
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
                                saveApplicationPasswordHashValue(prefs, new_hv);
                            }

                            @Override
                            public void negativeResponse(Context context, Object[] objects) {
                                setAppPswdStatus();
                            }
                        });
                        ApplicationPassword.createPassword(mGp, mActivity, getSupportFragmentManager(), mUtil,
                                mActivity.getString(R.string.settings_security_application_password_desc_change), ntfy_create);
                    }

                    @Override
                    public void negativeResponse(Context context, Object[] objects) {}
                });
                ntfy_verify.notifyToListener(true, null);
            }
        });

        mButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotifyEvent ntfy_verify=new NotifyEvent(mActivity);
                ntfy_verify.setListener(new NotifyEvent.NotifyEventListener() {
                    @Override
                    public void positiveResponse(Context context, Object[] objects) {

                        NotifyEvent ntfy_confirm=new NotifyEvent(mActivity);
                        ntfy_confirm.setListener(new NotifyEvent.NotifyEventListener() {
                            @Override
                            public void positiveResponse(Context context, Object[] objects) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
                                saveApplicationPasswordHashValue(prefs, "");
                                setAppPswdStatus();
                            }

                            @Override
                            public void negativeResponse(Context context, Object[] objects) {}
                        });
                        mUtil.showCommonDialog(true, "W",
                                mActivity.getString(R.string.settings_security_application_password_confirm_remove), "", ntfy_confirm);

                    }

                    @Override
                    public void negativeResponse(Context context, Object[] objects) {}
                });
                ntfy_verify.notifyToListener(true, null);
            }
        });

    }

    public void saveApplicationPasswordHashValue(SharedPreferences prefs, String hv) {
        ApplicationPassword.savePasswordHashValue(mGp, prefs,  hv) ;
    }

    private void setAppPswdStatus() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String hv= ApplicationPassword.getPasswordHashValue(prefs);

        if (hv.equals("")) {
            mAppPswdMsg.setText(mActivity.getString(R.string.settings_security_application_password_not_created));
            CommonDialog.setViewEnabled(mActivity, mButtonCreate, true);
            CommonDialog.setViewEnabled(mActivity, mButtonChange, false);
            CommonDialog.setViewEnabled(mActivity, mButtonRemove, false);

            CommonDialog.setViewEnabled(mActivity, mCtvSettingTimeOut, false);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingAppStartup, false);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingUseEditTask, false);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingUseExportTask, false);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingInitSmbAccount, false);

            CommonDialog.setViewEnabled(mActivity, mCtvSettingInitZipPassword, false);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingHideShowPasswordButton, false);
        } else {
            mAppPswdMsg.setText(mActivity.getString(R.string.settings_security_application_password_created));
            CommonDialog.setViewEnabled(mActivity, mButtonCreate, false);
            CommonDialog.setViewEnabled(mActivity, mButtonChange, true);
            CommonDialog.setViewEnabled(mActivity, mButtonRemove, true);

            CommonDialog.setViewEnabled(mActivity, mCtvSettingTimeOut, true);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingAppStartup, true);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingUseEditTask, true);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingUseExportTask, true);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingInitSmbAccount, true);

            CommonDialog.setViewEnabled(mActivity, mCtvSettingInitZipPassword, true);
            CommonDialog.setViewEnabled(mActivity, mCtvSettingHideShowPasswordButton, true);
        }
    }

}