package moe.key.yao.expandlistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Key on 2015/1/22.
 */
public abstract class BaseExpandListAdapter extends BaseAdapter {

    private Context mContext;
    private BaseViewHolder mHolder;
    private Set<Integer> mPositionSet;
    private View.OnClickListener mChildClickListener = null;

    private Map<Integer, View> viewMap;

    public BaseExpandListAdapter(Context context) {
        this.mContext = context;
        mPositionSet = new HashSet<Integer>();
        viewMap = new HashMap<Integer, View>();
    }

    @Override
    public int getCount() {
        return getParentCount();
    }

    @Override
    public Object getItem(int position) {
        return viewMap != null ? viewMap.get(position) : position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            mHolder = new BaseViewHolder();

            // 创建baseLayout
            mHolder.baseLayout = new LinearLayout(getContext());
            AbsListView.LayoutParams baseLP = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            mHolder.baseLayout.setOrientation(LinearLayout.VERTICAL);
            mHolder.baseLayout.setLayoutParams(baseLP);

            // 创建parenLayout
            mHolder.parentLayout = new LinearLayout(getContext());
            AbsListView.LayoutParams parentLP = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            mHolder.parentLayout.setOrientation(LinearLayout.VERTICAL);
            mHolder.parentLayout.setLayoutParams(parentLP);
            mHolder.parentLayout.setId(R.id.expandlistview_parent_layout);

            // 创建parentView
            mHolder.parentView = getParentView(position, null, mHolder.baseLayout);
            mHolder.parentLayout.addView(mHolder.parentView);

            // 创建childrenLayout
            mHolder.childrenLayout = new LinearLayout(getContext());
            mHolder.childrenLayout.setId(R.id.expandlistview_children_layout);
            AbsListView.LayoutParams childLP = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            mHolder.childrenLayout.setOrientation(LinearLayout.VERTICAL);
            mHolder.childrenLayout.setLayoutParams(childLP);

            if (isCanExpand(position)) {
                // 创建childView
                int childCount = getChildCount(position);
                BaseChildViewHolder childHolder = new BaseChildViewHolder();
                childHolder.childrens = new ArrayList<View>();
                for (int i = 0 ; i < childCount ; i ++) {
                    View childView = getChildView(position, i, null, mHolder.childrenLayout);
                    childHolder.childrens.add(childView);

                    childView.setTag(R.id.expandlistview_parent_position_tag, position);
                    childView.setTag(R.id.expandlistview_child_position_tag, i);
                    childView.setOnClickListener(mChildClickListener);

                    mHolder.childrenLayout.addView(childView);
                }
                mHolder.childrenLayout.setTag(R.id.expandlistview_children_layout_holder_tag, childHolder);
            } else {

                // 当Item不可展开的时候，设置children的个数为0
                BaseChildViewHolder childHolder = new BaseChildViewHolder();
                childHolder.childrens = new ArrayList<View>();
                mHolder.childrenLayout.setTag(R.id.expandlistview_children_layout_holder_tag, childHolder);

                // 隐藏childrenLayout
                mHolder.childrenLayout.setVisibility(View.GONE);
            }

            // add view to BaseLayout
            mHolder.baseLayout.addView(mHolder.parentLayout);
            mHolder.baseLayout.addView(mHolder.childrenLayout);
            convertView = mHolder.baseLayout;
            convertView.setTag(mHolder);
        } else {

            // 重用convertView
            mHolder = (BaseViewHolder) convertView.getTag();

            // 重新getParentView，设置parentView的值
            mHolder.parentView = getParentView(position, mHolder.parentView, mHolder.parentLayout);

            if (isCanExpand(position)) {
                // 处理ChildView
                final int childCount = getChildCount(position);
                BaseChildViewHolder childHolder = (BaseChildViewHolder) mHolder.childrenLayout.getTag(R.id.expandlistview_children_layout_holder_tag);
                final int existChildCount = childHolder.childrens.size();

                if (existChildCount > childCount) {
                    for (int i = 0 ; i < existChildCount ; i ++) {
                        // 当前存在的子项比所需要的子项多，则隐藏多余的子项
                        if (i >= childCount) {
                            childHolder.childrens.get(i).setVisibility(View.GONE);
                            continue;
                        }

                        // 重新getChildView，设置childView的值
                        childHolder.childrens.get(i).setVisibility(View.VISIBLE);
                        View childView = childHolder.childrens.get(i);
                        childView.setVisibility(View.VISIBLE);
                        childView = getChildView(position, i, childView, mHolder.childrenLayout);
                        childView.setTag(R.id.expandlistview_parent_position_tag, position);
                        childView.setTag(R.id.expandlistview_child_position_tag, i);
                        childView.setOnClickListener(mChildClickListener);

                    }
                } else {
                    for (int i = 0 ; i < childCount ; i ++) {
                        // 当前存在的子项比所需要的子项少，则创建缺少的子项
                        if (i >= existChildCount) {
                            View childView = getChildView(position, i, null, mHolder.childrenLayout);
                            childHolder.childrens.add(childView);
                            mHolder.childrenLayout.addView(childView);
                            continue;
                        }

                        // 重新getChildView，设置childView的值
                        childHolder.childrens.get(i).setVisibility(View.VISIBLE);
                        View childView = childHolder.childrens.get(i);
                        childView.setVisibility(View.VISIBLE);
                        childView = getChildView(position, i, childView, mHolder.childrenLayout);
                        childView.setTag(R.id.expandlistview_parent_position_tag, position);
                        childView.setTag(R.id.expandlistview_child_position_tag, i);
                        childView.setOnClickListener(mChildClickListener);
                    }

                }
                mHolder.childrenLayout.setTag(R.id.expandlistview_children_layout_holder_tag, childHolder);
                mHolder.childrenLayout.setVisibility(View.VISIBLE);
            } else {
                mHolder.childrenLayout.setVisibility(View.GONE);
            }

        }

        mHolder.parentLayout.setTag(R.id.expandlistview_parent_position_tag, position);

        // expand animation value
        int widthSpec = View.MeasureSpec.makeMeasureSpec(
                (int) (getContext().getResources().getDisplayMetrics().widthPixels - 10
                        * getContext().getResources().getDisplayMetrics().density),
                View.MeasureSpec.EXACTLY);
        mHolder.childrenLayout.measure(widthSpec, 0);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mHolder.childrenLayout.getLayoutParams();
        if (mPositionSet.contains(position)) {
            lp.bottomMargin = 0;
            mHolder.childrenLayout.setVisibility(View.VISIBLE);
        } else {
            lp.bottomMargin = - mHolder.childrenLayout.getMeasuredHeight();
            mHolder.childrenLayout.setVisibility(View.GONE);
        }

        viewMap.put(position, convertView);
        return convertView;
    }

    private static class BaseViewHolder {
        LinearLayout baseLayout;
        LinearLayout parentLayout;
        View parentView;
        LinearLayout childrenLayout;
    }

    private static class BaseChildViewHolder {
        List<View> childrens;
    }

    /**
     * 获取当前Context
     * @return
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 当前父项是否为打开状态
     * @param position
     * @return
     */
    public boolean isParentOpening(int position) {
        return mPositionSet.contains(position);
    }

    public void setChildClickListener(View.OnClickListener l) {
        this.mChildClickListener = l;
    }

    /**
     * 设置默认打开所有Item
     */
    public void openAllItem() {
        for (int i = 0 ; i < getParentCount() ; i ++) {
            if (!mPositionSet.contains(i)) {
                mPositionSet.add(i);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 當允許打開多個Item的時候使用該方法更新Position
     * @param position
     */
    public void updatePositionSet(int position) {
        if (!mPositionSet.contains(position)) {
            mPositionSet.add(position);
        } else {
            mPositionSet.remove(position);
        }
        notifyDataSetChanged();
    }

    /**
     * 當只允許打開一個Item的時候使用該方法更新Position
     * @param position
     * @param oldPosition
     */
    public void updatePositionSet(int position, int oldPosition) {
        if (!mPositionSet.contains(position)) {
            mPositionSet.add(position);
        } else {
            mPositionSet.remove(position);
        }
        if (!mPositionSet.contains(oldPosition)) {
            mPositionSet.add(oldPosition);
        } else {
            mPositionSet.remove(oldPosition);
        }
        notifyDataSetChanged();
    }

    /**
     * 返回父项个数
     *
     * return the parent count
     */
    public abstract int getParentCount();

    /**
     * 根据父项position返回子项个数
     *
     * @param position parent position
     */
    public abstract int getChildCount(int position);

    /**
     * 返回当前父项是否可以展开 <br>
     *
     * @param position the parent item position
     */
    public abstract boolean isCanExpand(int position);

    /**
     * 返回父项的View，原理同BaseAdapter.getView(..)
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getParentView(int position, View convertView, ViewGroup parent);

    /**
     * 返回子项的View，原理同BaseAdapter.getView(..)
     * @param parentPosition
     * @param childPosition
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getChildView(int parentPosition, int childPosition, View convertView, ViewGroup parent);

}
