package moe.key.yao.expandlistview.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import moe.key.yao.expandlistview.BaseExpandListAdapter;
import moe.key.yao.expandlistview.sample.R;
import moe.key.yao.expandlistview.sample.model.Model;

/**
 * Created by Key on 2015/1/22.
 */
public class MyListAdapter extends BaseExpandListAdapter {

    private List<Model> mData;
    private ParentViewHolder pHolder;
    private ChildViewHolder cHolder;

    public MyListAdapter(Context context, List<Model> data) {
        super(context);
        this.mData = data;
    }

    @Override
    public int getParentCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getChildCount(int position) {
        List<String> sub = mData.get(position).getSubString();
        return sub != null ? sub.size() : 0;
    }

    @Override
    public boolean isCanExpand(int position) {
        if (mData.get(position).getSubString() == null) {
            return false;
        }
        if (mData.get(position).getSubString().size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public View getParentView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            pHolder = new ParentViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_parent, parent, false);
            pHolder.title = (TextView) convertView.findViewById(R.id.title);
            pHolder.arrow = (ImageView) convertView.findViewById(R.id.arrow_image);
            convertView.setTag(pHolder);
        } else {
            pHolder = (ParentViewHolder) convertView.getTag();
        }

        pHolder.title.setText(mData.get(position).getTitle());

        if (isCanExpand(position)) {
            pHolder.arrow.setVisibility(View.VISIBLE);
        } else {
            pHolder.arrow.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int parentPosition, int childPosition, View convertView, ViewGroup parent) {
        if (convertView == null) {
            cHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_child, parent, false);
            cHolder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(cHolder);
        } else {
            cHolder = (ChildViewHolder) convertView.getTag();
        }

        cHolder.title.setText(mData.get(parentPosition).getSubString().get(childPosition));

        return convertView;
    }

    private static class ParentViewHolder {
        TextView title;
        ImageView arrow;
    }

    private static class ChildViewHolder {
        TextView title;
    }

}
