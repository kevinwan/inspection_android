package com.gongpingjia.gpjdetector.kZViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * �Զ�����Ի�����RelativeLayout, ������IOS�Ļ���ɾ��ҳ��Ч������Ҫʹ��
 * �˹��ܵ�ʱ����Ҫ����Activity�Ķ��㲼������ΪSildingFinishLayout�� Ȼ����Ҫ����setTouchView()������������Ҫ������View
 * 
 * @author xiaanming
 * 
 * @blog http://blog.csdn.net/xiaanming
 * 
 */
public class SildingFinishLayout extends RelativeLayout implements OnTouchListener
{
    /**
     * SildingFinishLayout���ֵĸ�����
     */
    private ViewGroup mParentView;
    
    /**
     * ���?���߼���View
     */
    private View touchView;
    
    /**
     * ��������С����
     */
    private int mTouchSlop;


    
    /**
     * ���µ��X���
     */
    private int downX;
    
    /**
     * ���µ��Y���
     */
    private int downY;
    
    /**
     * ��ʱ�洢X���
     */
    private int tempX;
    
    /**
     * ������
     */
    private Scroller mScroller;
    
    /**
     * SildingFinishLayout�Ŀ��
     */
    private int viewWidth;
    
    /**
     * ��¼�Ƿ����ڻ���
     */
    private boolean isSilding;
    
    private OnSildingFinishListener onSildingFinishListener;
    
    private boolean isFinish;
    
    public SildingFinishLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    
    public SildingFinishLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed)
        {
            // ��ȡSildingFinishLayout���ڲ��ֵĸ�����
            mParentView = (ViewGroup)this.getParent();
            scrollOrigin();
            viewWidth = this.getWidth();
        }
    }
    
    /**
     * ����OnSildingFinishListener, ��onSildingFinish()������finish Activity
     * 
     * @param onSildingFinishListener
     */
    public void setOnSildingFinishListener(OnSildingFinishListener onSildingFinishListener)
    {
        this.onSildingFinishListener = onSildingFinishListener;
    }
    
    /**
     * ����Touch��View
     * 
     * @param touchView
     */
    public void setTouchView(View touchView)
    {
        this.touchView = touchView;
        touchView.setOnTouchListener(this);
    }
    
    public View getTouchView()
    {
        return touchView;
    }
    
    public void changeParentView(ViewGroup v)
    {
        
        if (v != null)
        {
            this.mParentView = v;
        }
        else
        {
            this.mParentView = (ViewGroup)this.getParent();
        }
    }
    
    /**
     * ����������
     */
    private void scrollRight()
    {
        final int delta = (viewWidth + mParentView.getScrollX());
        // ����startScroll����������һЩ�����Ĳ���������computeScroll()�����е���scrollTo������item
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0, Math.abs(delta));
        postInvalidate();
    }
    
    /**
     * ��������ʼλ��
     */
    public void scrollOrigin()
    {
        int delta = mParentView.getScrollX();
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0, Math.abs(delta));
        postInvalidate();
    }
    
    /**
     * touch��View�Ƿ���AbsListView�� ����ListView, GridView��������
     * 
     * @return
     */
    private boolean isTouchOnAbsListView()
    {
        return touchView instanceof AbsListView ? true : false;
    }
    
    /**
     * touch��view�Ƿ���ScrollView����������
     * 
     * @return
     */
    private boolean isTouchOnScrollView()
    {
        return touchView instanceof ScrollView ? true : false;
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                downX = tempX = (int)event.getRawX();
                downY = (int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int)event.getRawX();
                int deltaX = tempX - moveX;
                tempX = moveX;
                if (Math.abs(moveX - downX) > mTouchSlop && Math.abs((int) event.getRawY() - downY) < mTouchSlop)
                {
                    isSilding = true;
                    
                    // ��touchView��AbsListView��
                    // ����ָ������ȡ��item�ĵ���¼�����Ȼ���ǻ���Ҳ������item����¼��ķ���
                    if (isTouchOnAbsListView())
                    {
                        MotionEvent cancelEvent = MotionEvent.obtain(event);
                        cancelEvent.setAction(MotionEvent.ACTION_CANCEL
                            | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                        v.onTouchEvent(cancelEvent);
                    }
                    
                }
                
                if (moveX - downX >= 0 && isSilding)
                {
                    mParentView.scrollBy(deltaX, 0);
                    
                    // �����ڻ��������ListView ScrollView���Լ��Ļ����¼�
                    if (isTouchOnScrollView() || isTouchOnAbsListView())
                    {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isSilding = false;
                if (mParentView.getScrollX() <= -200 / 2)
                {
                    isFinish = true;
                    onSildingFinishListener.onSildingStart();
                    scrollRight();
                }
                else
                {
                    scrollOrigin();
                    isFinish = false;
                }
                break;
        }
        
        // ����touch��view��AbsListView����ScrollView ���Ǵ����������Լ����߼�֮��
        // �ٽ���AbsListView, ScrollView�Լ��������Լ����߼�
        if (isTouchOnScrollView() || isTouchOnAbsListView())
        {
            return v.onTouchEvent(event);
        }
        
        // ��������ֱ�ӷ���true
        return true;
    }
    
    @Override
    public void computeScroll()
    {
        // ����startScroll��ʱ��scroller.computeScrollOffset()����true��
        if (mScroller.computeScrollOffset())
        {
            mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            
            if (mScroller.isFinished())
            {
                
                if (onSildingFinishListener != null && isFinish)
                {
                    onSildingFinishListener.onSildingFinish();
                }
            }
        }
    }
    
    public interface OnSildingFinishListener
    {
        public void onSildingFinish();
        public void onSildingStart();
    }
    
}
