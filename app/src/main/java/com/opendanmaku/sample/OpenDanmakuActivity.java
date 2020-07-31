package com.opendanmaku.sample;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.opendanmaku.IDanmakuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/7/31 16:24
 */
public class OpenDanmakuActivity extends AppCompatActivity implements View.OnClickListener {
    private DanmakuView mDanmakuView;
    private Button switcherBtn;
    private Button sendBtn;
    private EditText textEditText;
    private VideoView videoView;
    private static final String TAG = "OpenDanmakuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_danmaku);
        //实例化控件
        mDanmakuView = (DanmakuView) findViewById(R.id.danmakuView);
        switcherBtn = (Button) findViewById(R.id.switcher);
        sendBtn = (Button) findViewById(R.id.send);
        textEditText = (EditText) findViewById(R.id.text);
        videoView = (VideoView) findViewById(R.id.videoView);
        setVideoView();
        //添加弹幕集合数据
        List<IDanmakuItem> list = initItems();
        //把数据进行随机排列
        Collections.shuffle(list);
        //添加到弹幕控件上
        mDanmakuView.addItem(list, true);
        switcherBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
    }

    private void setVideoView() {
        //设置准备好的监听
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, "onCompletion: ");
                videoView.start();//开始播放
            }
        });
        // 设置播放完成
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e(TAG, "onPrepared: ");
                videoView.start();//重新开始播放
            }
        });
        //设置播放出错
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "onError: ");
                Toast.makeText(OpenDanmakuActivity.this, "您的手机不支持播放该视频", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //设置播放地址
        videoView.setVideoURI(Uri.parse("http://it.nangongyibin.com:8080/resource/cc.mp4"));
//        videoView.setVideoPath("http://it.nangongyibin.com:8080/resource/cc.mp4");
        //设置控制面板
        videoView.setMediaController(new MediaController(OpenDanmakuActivity.this));
    }

    /**
     * @return 初始化弹幕数据
     */
    private List<IDanmakuItem> initItems() {
        List<IDanmakuItem> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            IDanmakuItem item = new DanmakuItem(this, i + " : plain text danmuku", mDanmakuView.getWidth());
            list.add(item);
        }
        String msg = " : text with image   ";
        for (int i = 0; i < 100; i++) {
            ImageSpan imageSpan = new ImageSpan(this, R.drawable.em);
            SpannableString spannableString = new SpannableString(i + msg);
            spannableString.setSpan(imageSpan, spannableString.length() - 2, spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            IDanmakuItem item = new DanmakuItem(this, spannableString, mDanmakuView.getWidth(), 0, 0, 0, 1.5f);
            list.add(item);
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmakuView.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //隐藏
        mDanmakuView.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除
        mDanmakuView.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switcher:
                if (mDanmakuView.isPaused()) {
                    switcherBtn.setText(R.string.hide);
                    mDanmakuView.show();
                } else {
                    switcherBtn.setText(R.string.show);
                    mDanmakuView.hide();
                }
                break;
            case R.id.send:
                String input = textEditText.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(OpenDanmakuActivity.this, R.string.empty_prompt, Toast.LENGTH_SHORT).show();
                } else {
                    IDanmakuItem item = new DanmakuItem(this, new SpannableString(input), mDanmakuView.getWidth(), 0, R.color.my_item_color, 0, 1);
//                    IDanmakuItem item = new DanmakuItem(this, input, mDanmakuView.getWidth());
//                    item.setTextColor(getResources().getColor(R.color.my_item_color));
//                    item.setTextSize(14);
//                    item.setTextColor(textColor);
                    mDanmakuView.addItemToHead(item);
                }
                textEditText.setText("");
                break;
        }
    }
}
