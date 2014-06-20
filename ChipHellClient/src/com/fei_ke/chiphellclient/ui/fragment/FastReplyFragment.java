
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.fei_ke.chiphellclient.ChhAplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.customviews.FloatLabelLayout;
import com.fei_ke.chiphellclient.utils.SmileyPickerUtility;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_fast_reply)
public class FastReplyFragment extends BaseFragment implements OnClickListener {
    @ViewById(R.id.editText_fast_reply)
    EditText editTextFastReply;

    @ViewById(R.id.button_reply_send)
    View btnReplySend;

    @ViewById(R.id.layout_float_label)
    FloatLabelLayout floatLabelLayout;

    // 当前需要回复的版块
    Plate mPlate;

    // 当前需要回帖的贴子
    Thread mThread;

    public static FastReplyFragment getInstance(){
        return FastReplyFragment_.builder().build();
    }
    
    public void setPlateAndThread(Plate plate, Thread thread) {
        this.mPlate = plate;
        this.mThread = thread;
        SmileyPickerUtility.showKeyBoard(editTextFastReply);
        String hint = "回复: " + thread.getTitle();
        editTextFastReply.setHint(hint);
        floatLabelLayout.getLabel().setText(hint);
    }

    @Override
    protected void onAfterViews() {
        btnReplySend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_reply_send:
                reply();
                break;
            default:
                break;
        }
    }

    void reply() {
        String message = editTextFastReply.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), "不能为空", 0).show();
            return;
        }
        ChhApi api = new ChhApi();
        api.reply(mPlate.getFid(), mThread.getTid(), ChhAplication.getInstance().getFormHash(), message, new ApiCallBack<String>() {
            ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), result + "", 0).show();
                }
            }

            @Override
            public void onStart() {
                dialog.setMessage("发送中……");
                dialog.show();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "发送失败", 0).show();
                }
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing()) {
                    dialog.cancel();
                    editTextFastReply.setText("");
                }
            }
        });
    }
}
