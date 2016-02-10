package com.fei_ke.chiphellclient.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateResponse;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.io.File;

/**
 * 更新界面
 * Created by fei on 16/2/9.
 */
@EActivity
public class UpdateActivity extends Activity {
    @Extra
    UpdateResponse updateResponse;

    public static Intent getStartIntent(Context context, UpdateResponse updateResponset) {
        Intent intent = UpdateActivity_.intent(context)
                .updateResponse(updateResponset)
                .get();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUpdateDialog();
    }

    private void showUpdateDialog() {
        StringBuilder sb = new StringBuilder();
        final File file = UmengUpdateAgent.downloadedFile(getApplication(), updateResponse);
        sb.append("最新版本：");
        sb.append(updateResponse.version);
        sb.append("\n");
        if (file == null) {
            sb.append("文件大小：");
            sb.append(Formatter.formatFileSize(this, Long.valueOf(updateResponse.target_size)));
        } else {
            sb.append("最新版本已下载，请安装！");
        }
        sb.append("\n\n");
        sb.append("更新内容");
        sb.append("\n");
        sb.append(updateResponse.updateLog);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("发现新版本")
                .setMessage(sb.toString())
                .setPositiveButton(file == null ? "下载" : "安装", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (file == null) {
                            UmengUpdateAgent.startDownload(UpdateActivity.this, updateResponse);
                        } else {
                            UmengUpdateAgent.startInstall(getApplication(), file);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
