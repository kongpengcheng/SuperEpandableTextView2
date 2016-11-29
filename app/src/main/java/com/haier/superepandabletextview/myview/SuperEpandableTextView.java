package com.haier.superepandabletextview.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haier.superepandabletextview.R;

/**
 * Created by Harry.Kong on 2016/11/26.
 */

public class SuperEpandableTextView extends LinearLayout {
    //允许显示最大行数
    int maxExpandLines = 0;
    //动画执行时间
    int duration = 0;
    //是否发生过文字变动
    boolean isChange = false;
    //文本框真实高度
    int realTextViewHeigt = 0;
    //默认处于收起状态
    boolean isCollapsed = true;
    //收起时候的整体高度
    int collapsedHeight = 0;
    //剩余点击按钮的高度
    int lastHeight = 0;
    //是否正在执行动画
    TextView id_source_textview;
    TextView id_expand_textview;
    boolean isAnimate = false;
    OnExpandStateChangeListener listener;

    public void setListener(OnExpandStateChangeListener listener) {
        this.listener = listener;
    }
    public SuperEpandableTextView(Context context) {
        super(context);
    }

    public SuperEpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SuperEpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SuperExpandableTextViewAttr);
        maxExpandLines = array.getInteger(R.styleable.SuperExpandableTextViewAttr_maxExpandLines, 3);
        duration = array.getInteger(R.styleable.SuperExpandableTextViewAttr_duration, 500);
        array.recycle();
    }

    //当加载完成xml后，就会执行那个方法。
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        id_source_textview = (TextView) findViewById(R.id.id_source_textview);
        id_expand_textview = (TextView) findViewById(R.id.id_expand_textview);
        id_expand_textview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SuperAnimation animation;

                isCollapsed=!isCollapsed;
                if (isCollapsed) {
                    id_expand_textview.setText("查看更多");
                    if (listener!=null) {
                        listener.onExpandStateChanged(true);
                    }
                    animation=new SuperAnimation(getHeight(), collapsedHeight);
                }
                else {
                    id_expand_textview.setText("收起");
                    if (listener!=null) {
                        listener.onExpandStateChanged(false);
                    }
                    animation=new SuperAnimation(getHeight(), realTextViewHeigt+lastHeight);
                }
                //只是将view移动到了目标位置，但是view绑定的点击事件还在原来位置，导致点击时会先闪一下
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimate=true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        clearAnimation();
                        isAnimate=false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                clearAnimation();
                startAnimation(animation);

            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //执行动画的过程中屏蔽事件
        return isAnimate;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果隐藏控件或者textview的值没有发生改变，那么不进行测量
        if (getVisibility()==GONE || !isChange) {
            return;
        }
        isChange=false;

        //初始化默认状态，即正常显示文本
        id_expand_textview.setVisibility(GONE);
        id_source_textview.setMaxLines(Integer.MAX_VALUE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //如果本身没有达到收起展开的限定要求，则不进行处理
        if (id_source_textview.getLineCount()<=maxExpandLines) {
            return;
        }

        //初始化高度赋值，为后续动画事件准备数据
        realTextViewHeigt=getRealTextViewHeight(id_source_textview);

        //如果处于收缩状态，则设置最多显示行数
        if (isCollapsed) {
            id_source_textview.setLines(maxExpandLines);
        }
        id_expand_textview.setVisibility(VISIBLE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isCollapsed) {
            id_source_textview.post(new Runnable() {
                @Override
                public void run() {
                    lastHeight=getHeight()-id_source_textview.getHeight();
                    collapsedHeight=getMeasuredHeight();
                }
            });
        }
    }

    public void setText(String text) {
        isChange = true;
        id_source_textview.setText(text);
    }

    public void setText(String text, boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
        if (isCollapsed) {
            id_expand_textview.setText("查看更多");
        } else {
            id_expand_textview.setText("收起");
        }
        clearAnimation();
        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }
    /**
     * 获取开始真实高度
     * @param textView
     * @return
     */
    private int getRealTextViewHeight(TextView textView) {
        //此处根据文本行数获取文本的高度
        int textHeight=textView.getLayout().getLineTop(textView.getLineCount());
        return textHeight+textView.getCompoundPaddingBottom()+textView.getCompoundPaddingTop();
    }

    /**
     * 此处用到了估值器
     */
    public int getValue(int startValue, int endValue, float Time) {
        int value = (int) ((endValue - startValue) * Time + startValue);
        return value;

    }

    private class SuperAnimation extends Animation {
        int startValue = 0;
        int endValue = 0;

        public SuperAnimation(int startValue, int endValue) {
            setDuration(duration);
            this.startValue = startValue;
            this.endValue = endValue;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            int height = getValue(startValue, endValue, interpolatedTime);
            id_source_textview.setMaxHeight(height - lastHeight);
            SuperEpandableTextView.this.getLayoutParams().height = height;
            SuperEpandableTextView.this.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public interface OnExpandStateChangeListener {
        void onExpandStateChanged(boolean isExpanded);
    }
}
