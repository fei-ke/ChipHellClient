
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.ProgressDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fei_ke.chiphellclient.ChhApplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.constant.SmileTable;
import com.fei_ke.chiphellclient.ui.fragment.SmileFragment.OnSmileChoose;
import com.fei_ke.chiphellclient.utils.SmileyPickerUtility;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.droidsonroids.gif.GifDrawable;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EFragment(R.layout.fragment_fast_reply)
public class FastReplyFragment extends BaseFragment implements OnClickListener {
    @ViewById(R.id.editText_fast_reply)
    EditText editTextFastReply;

    @ViewById(R.id.button_reply_send)
    View btnReplySend;

    // @ViewById(R.id.layout_float_label)
    // FloatLabelLayout floatLabelLayout;

    @ViewById(R.id.textView_hint)
    TextView textViewHint;

    @ViewById(R.id.layout_smile)
    View layoutSmile;

    @ViewById(R.id.button_smile)
    View viewSmile;

    // 当前需要回复的版块
    Plate mPlate;

    // 当前需要回帖的贴子
    Thread mThread;

    // 当前引用的回复
    PrepareQuoteReply mQuoteReply;

    SmileFragment mSmileFragment;

    private OnReplySuccess onReplySuccess;

    public static FastReplyFragment getInstance() {
        return FastReplyFragment_.builder().build();
    }

    public void setPlateAndThread(Plate plate, Thread thread) {
        this.mQuoteReply = null;
        this.mPlate = plate;
        this.mThread = thread;
        SmileyPickerUtility.showKeyBoard(editTextFastReply);
        String hint = "回复: " + thread.getTitle();

        setHint(hint);
    }

    public void setPrepareQuoteReply(PrepareQuoteReply quoteReply) {
        this.mQuoteReply = quoteReply;
        setHint(quoteReply.getQuoteBody());
    }

    void setHint(String hint) {
        SmileyPickerUtility.showKeyBoard(editTextFastReply);
        // editTextFastReply.setHint(Html.fromHtml(hint));
        textViewHint.setText(Html.fromHtml(hint));
    }

    @Override
    protected void onAfterViews() {
        btnReplySend.setOnClickListener(this);
        viewSmile.setOnClickListener(this);

        mSmileFragment = SmileFragment.getInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.layout_smile, mSmileFragment).commit();
        mSmileFragment.setmOnSmileChoose(new OnSmileChoose() {

            @Override
            public void onSmileChoose(Entry<String, String> smile) {
                int currentPosition = editTextFastReply.getSelectionStart();// 得到当前光标位置
                editTextFastReply.getText().insert(currentPosition, smile.getKey());// 插入表情
                setFaceText(editTextFastReply, editTextFastReply.getText().toString());
                editTextFastReply.setSelection(currentPosition + smile.getKey().length());

            }
        });

    }

    private void setFaceText(TextView textView, String text) {
        SpannableString spanStr = parseString(text);
        textView.setText(spanStr);
    }

    private void setFace(SpannableStringBuilder spb, String smileName, int length) {
        String path = SmileTable.get(smileName);
        try {
            int height = (int) editTextFastReply.getTextSize() * 2;
            GifDrawable drawable = new GifDrawable(ChhApplication.getInstance().getAssets(), path);
            // Drawable drawable = Drawable.createFromStream(getResources().getAssets().open(path), smileName);
            drawable.setBounds(0, 0, height, height);
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            SpannableString spanStr = new SpannableString(smileName);
            spanStr.setSpan(imageSpan, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spb.append(spanStr);
        } catch (IOException e) {
            e.printStackTrace();
            spb.append(smileName);
        }

    }

    private SpannableString parseString(String inputStr) {
        SpannableStringBuilder spb = new SpannableStringBuilder();
        Pattern mPattern = Pattern.compile("\\[{1}.{2}\\]{1}");
        Matcher mMatcher = mPattern.matcher(inputStr);
        String tempStr = inputStr;

        while (mMatcher.find()) {
            int start = mMatcher.start();
            int end = mMatcher.end();
            spb.append(tempStr.substring(0, start));
            String faceName = mMatcher.group();
            setFace(spb, faceName, end - start);
            tempStr = tempStr.substring(end, tempStr.length());
            /**
             * 更新查找的字符串
             */
            mMatcher.reset(tempStr);
        }
        spb.append(tempStr);
        return new SpannableString(spb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_reply_send:
                if (mQuoteReply != null) {
                    quoteReply();
                } else {
                    reply();

                }
            case R.id.button_smile:
                if (layoutSmile.getVisibility() == View.VISIBLE) {
                    SmileyPickerUtility.showKeyBoard(editTextFastReply);
                    layoutSmile.setVisibility(View.GONE);
                } else {
                    int height = SmileyPickerUtility.getKeyboardHeight(getActivity());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutSmile.getLayoutParams();
                    params.height = height;
                    layoutSmile.setVisibility(View.VISIBLE);
                    SmileyPickerUtility.hideSoftInput(editTextFastReply);
                }
                break;
            default:
                break;
        }
    }

    private void quoteReply() {
        String message = editTextFastReply.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), "不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mQuoteReply.setMessage(message);

        ChhApi api = new ChhApi();
        api.quotrReply(mQuoteReply, new ReplyApiCallBack());
    }

    void reply() {
        String message = editTextFastReply.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), "不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        ChhApi api = new ChhApi();
        api.reply(mPlate.getFid(), mThread.getTid(), ChhApplication.getInstance().getFormHash(), message, new ReplyApiCallBack());
    }

    public void show() {
        SmileyPickerUtility.showKeyBoard(editTextFastReply);
    }

    public void hide() {
        SmileyPickerUtility.hideSoftInput(editTextFastReply);
        if (layoutSmile.getVisibility() == View.VISIBLE) {
            layoutSmile.setVisibility(View.GONE);
        }
    }

    public OnReplySuccess getOnReplySuccess() {
        return onReplySuccess;
    }

    public void setOnReplySuccess(OnReplySuccess onReplySuccess) {
        this.onReplySuccess = onReplySuccess;
    }

    private class ReplyApiCallBack extends ApiCallBack<List<Post>> {
        ProgressDialog dialog;

        @Override
        public void onSuccess(List<Post> result) {
            if (getActivity() != null && result != null) {
                Toast.makeText(getActivity(), "回复成功", Toast.LENGTH_SHORT).show();
                SmileyPickerUtility.hideSoftInput(editTextFastReply);
                hide();
                if (onReplySuccess != null) {
                    onReplySuccess.onSuccess(result);
                }
            }
        }

        @Override
        public void onStart() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("发送中……");
            dialog.show();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            error.printStackTrace();
            if (getActivity() != null) {
                Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish() {
            if (dialog.isShowing()) {
                dialog.cancel();
                editTextFastReply.setText("");
            }
        }
    }

    public static interface OnReplySuccess {
        void onSuccess(List<Post> posts);
    }
}
