package com.lmjssjj.autotextswitcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

import androidx.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : lmjssjj
 * @since :
 */
public class AutoTextSwitcher extends TextSwitcher implements ViewSwitcher.ViewFactory {


    private static final int HANDLER_START_AUTO = 0;
    private static final int HANDLER_STOP_AUTO = 1;
    private static final int DEFAULT_SWITCH_TIME = 3000; // Milliseconds

    private static final int STATE_START = 0;
    private static final int STATE_STOP = 1;

    /**
     * @author lmjssjj
     * @date created date 2023/4/18 13:59
     * @describe 是否处于正在切换
     */
    private int mScrollState = STATE_STOP;

    /**
     * @author lmjssjj
     * @date created date 2023/4/18 13:58
     * @describe 当前切换到的文本脚标
     */
    private int mIndex = -1;
    /**
     * @author lmjssjj
     * @date created date 2023/4/18 13:58
     * @describe 切换的文本列表
     */
    private List<CharSequence> mContentTextDataLists = new ArrayList<>();
    /**
     * @author lmjssjj
     * @date created date 2023/4/18 13:57
     * @describe 每次切换间隔时间
     */
    private long mSwitchTime;
    /**
     * @author lmjssjj
     * @date created date 2023/4/18 13:57
     * @describe 是否自动切换
     */
    private boolean isAutoStart;

    private @LayoutRes int mItemTextViewResourceId;

    private LayoutInflater mLayoutInflater;

    /**
     * @author lmjssjj
     * @date created date 2023/4/18 13:56
     * @describe item 点击时件
     */
    private OnItemClickListener mItemClickListener;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_START_AUTO:
                    if (mContentTextDataLists.size() > 0) {
                        mIndex++;
                        setText(mContentTextDataLists.get(mIndex % mContentTextDataLists.size()));
                    }
                    sendEmptyMessageDelayed(HANDLER_START_AUTO, mSwitchTime);
                    break;
                case HANDLER_STOP_AUTO:
                    removeMessages(HANDLER_START_AUTO);
                    break;
            }
        }
    };

    public AutoTextSwitcher(Context context) {
        this(context, null);
    }

    public AutoTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLayoutInflater = LayoutInflater.from(context);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoTextSwitcher);
        try {
            CharSequence[] array = typedArray.getTextArray(R.styleable.AutoTextSwitcher_contentTextArray);
            if (array != null) {
                mContentTextDataLists.addAll(Arrays.asList(array));
            }
            mSwitchTime = typedArray.getInteger(R.styleable.AutoTextSwitcher_switchTime, DEFAULT_SWITCH_TIME);
            isAutoStart = typedArray.getBoolean(R.styleable.AutoTextSwitcher_autoStart, false);
            mItemTextViewResourceId = typedArray.getResourceId(R.styleable.AutoTextSwitcher_itemTextView, R.layout.switcher_item);
        } finally {
            typedArray.recycle();
        }

        if (getInAnimation() == null || getOutAnimation() == null) {
            // 设置切入动画
            setInAnimation(context, R.anim.slide_in_up);
            // 设置切出动画
            setOutAnimation(context, R.anim.slide_out_up);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mContentTextDataLists.size() > 1 && isAutoStart) {
            start();
        }
    }

    /**
     * set Data list.
     *
     * @param titles
     */
    public void setTextList(ArrayList<CharSequence> titles) {
        mContentTextDataLists.clear();
        mContentTextDataLists.addAll(titles);
        mIndex = -1;
    }

    public boolean isAutoStart() {
        return isAutoStart;
    }

    public void setAutoStart(boolean isAutoStart) {
        this.isAutoStart = isAutoStart;
    }

    public void setSwitchTime(int switchTime) {
        this.mSwitchTime = switchTime;
    }

    /**
     * start auto scroll
     */
    public void start() {
        setFactory(this);
        mScrollState = STATE_START;
        mHandler.sendEmptyMessage(HANDLER_START_AUTO);
    }

    /**
     * stop auto scroll
     */
    public void stop() {
        mScrollState = STATE_STOP;
        mHandler.sendEmptyMessage(HANDLER_STOP_AUTO);
    }

    public boolean isStart() {
        return mScrollState == STATE_START;
    }

    @Override
    public View makeView() {
        View item = mLayoutInflater.inflate(mItemTextViewResourceId, this, false);
        item.setClickable(true);
        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null && mContentTextDataLists.size() > 0 && mIndex != -1) {
                    mItemClickListener.onItemClick(mIndex % mContentTextDataLists.size());
                }
            }
        });
        return item;
    }

    /**
     * set item onclick listener
     *
     * @param itemClickListener listener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * callback
         *
         * @param position position
         */
        void onItemClick(int position);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
