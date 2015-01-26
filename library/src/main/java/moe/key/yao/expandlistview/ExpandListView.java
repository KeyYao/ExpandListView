package moe.key.yao.expandlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Key on 2015/1/22.
 */
public class ExpandListView extends ListView {

    private long mDuration;
    private Interpolator mInterpolator;
    private OnParentItemClickListener mParentItemClickListener;
    private OnChildItemClickListener mChildItemClickListener;
    private boolean mAllItemCanOpen = true;
    private boolean mCanClickClose = true;
    private boolean mOpenAllItem = false;

    // ExpandAnimation use field
    private boolean mRuningAnimation = false;
    private int beforePosition = -1;

    public ExpandListView(Context context) {
        super(context);
    }

    public ExpandListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        this.setOnItemClickListener(new ExpandListViewOnItemClickListener());
        getExpandAdapter().setChildClickListener(new ExpandListViewChildOnClickListener());
        if (mOpenAllItem) {
            getExpandAdapter().openAllItem();
        }
        setSelector(R.color.expandlistview_selector);
    }

    /**
     * 获取ExpandListAdapter
     * @return
     */
    public BaseExpandListAdapter getExpandAdapter() {
        if (getAdapter() != null) {
            if (getAdapter() instanceof BaseExpandListAdapter) {
                return (BaseExpandListAdapter) getAdapter();
            }
        }
        return null;
    }

    /**
     * 设置展开动画的时间
     * @param duration
     */
    public void setExpandDuration(long duration) {
        this.mDuration = duration;
    }

    /**
     * 设置展开动画的时间
     * @param duration
     */
    public void setExpandDuration(int duration) {
        setExpandDuration((long) duration);
    }

    /**
     * 设置展开动画的Interpolator
     * @param i
     */
    public void setExpandInterpolator(Interpolator i) {
        this.mInterpolator = i;
    }

    /**
     * 设置父项的点击事件Listener
     * @param l
     */
    public void setOnParentItemClickListener(OnParentItemClickListener l) {
        this.mParentItemClickListener = l;
    }

    /**
     * 设置子项的点击事件Listener
     * @param l
     */
    public void setOnChildItemClickListener(OnChildItemClickListener l) {
        this.mChildItemClickListener = l;
    }

    /**
     * 设置所有的Item是否可以同时展开。 <br>
     * false的话只允许展开一个Item，之前已经展开的Item会关闭。
     * @param isAllItemCanOpen
     */
    public void setAllItemCanOpen(boolean isAllItemCanOpen) {
        this.mAllItemCanOpen = isAllItemCanOpen;
        if (isAllItemCanOpen) {
            this.mCanClickClose = true;
        }
    }

    /**
     * 设置是否支持点击后关闭Item
     * @param isCanClickClose
     */
    public void setCanClickClose(boolean isCanClickClose) {
        this.mCanClickClose = isCanClickClose;
    }

    /**
     * 设置默认展开所有Item
     * @param isOpenAllItem
     */
    public void setOpenAllItem(boolean isOpenAllItem) {
        this.mOpenAllItem = isOpenAllItem;
        if (isOpenAllItem) {
            if (getExpandAdapter() != null) {
                getExpandAdapter().openAllItem();
            }
        }
    }

    private void runOpenExpandAnimation(View view, final int index, final int oldIndex) {
        ExpandAnimation animation = new ExpandAnimation(view, mDuration);
        if (mInterpolator != null) {
            animation.setInterpolator(mInterpolator);
        }
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mRuningAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!mAllItemCanOpen) {
                    if (oldIndex != -1 && oldIndex != index && getExpandAdapter().isParentOpening(oldIndex)) {
                        getExpandAdapter().updatePositionSet(index, oldIndex);
                    } else {
                        getExpandAdapter().updatePositionSet(index);
                    }
                } else {
                    getExpandAdapter().updatePositionSet(index);
                }
                mRuningAnimation = false;
            }
        });
        if (!mAllItemCanOpen) {
            if (oldIndex != -1 && oldIndex != index && oldIndex >= getFirstVisiblePosition() && oldIndex <= getLastVisiblePosition()) {
                runCloseExpandAnimation(((View) getExpandAdapter().getItem(oldIndex)).findViewById(R.id.expandlistview_children_layout), oldIndex, false);
            }
        }
        view.startAnimation(animation);
    }

    private void runCloseExpandAnimation(View view, final int index, boolean flag) {
        if (getExpandAdapter().isParentOpening(index)) {
            ExpandAnimation animation = new ExpandAnimation(view, mDuration);
            if (mInterpolator != null) {
                animation.setInterpolator(mInterpolator);
            }
            if (flag) {
                animation.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        mRuningAnimation = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getExpandAdapter().updatePositionSet(index);
                        mRuningAnimation = false;
                    }
                });
            }
            view.startAnimation(animation);
        }
    }

    private class ExpandListViewOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mRuningAnimation) {
                if (getExpandAdapter().isCanExpand(position)) { // 判断当前Item是否有子项可以展开

                    // 获得子项所在的Layout
                    View childrenLayout = view.findViewById(R.id.expandlistview_children_layout);

                    if (!getExpandAdapter().isParentOpening(position)) { // 如果当前Item是关闭状态，则动画展开

                        runOpenExpandAnimation(childrenLayout, position, beforePosition);
                        beforePosition = position;

                    } else { // 如果当前Item是打开状态

                        if (mCanClickClose) { // 如果允许点击关闭，则动画关闭当前Item
                            runCloseExpandAnimation(childrenLayout, position, true);
                            beforePosition = position;
                        }

                    }
                } else {
                    if (beforePosition != - 1 && getExpandAdapter().isCanExpand(beforePosition) && !mAllItemCanOpen) { // 如果只允许一个Item打开，则关闭之前所打开的Item
                        runCloseExpandAnimation(((View) getExpandAdapter().getItem(beforePosition)).findViewById(R.id.expandlistview_children_layout), beforePosition, true);
                        beforePosition = position;
                    }
                }
            }

            // 传递父Item的点击事件
            if (mParentItemClickListener != null) {
                ViewGroup parentView = (ViewGroup) view.findViewById(R.id.expandlistview_parent_layout);
                int parentPosition = (Integer) parentView.getTag(R.id.expandlistview_parent_position_tag);
                mParentItemClickListener.onItemClick(parentView.getChildAt(0), parentPosition);
            }
        }
    }

    private class ExpandListViewChildOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // 传递子Item的点击事件
            if (mChildItemClickListener != null) {
                int parentPosition = (Integer) v.getTag(R.id.expandlistview_parent_position_tag);
                int childPosition = (Integer) v.getTag(R.id.expandlistview_child_position_tag);
                mChildItemClickListener.onItemClick(v, parentPosition, childPosition);
            }
        }
    }

}
