package moe.key.yao.expandlistview.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import moe.key.yao.expandlistview.ExpandListView;
import moe.key.yao.expandlistview.OnChildItemClickListener;
import moe.key.yao.expandlistview.OnParentItemClickListener;
import moe.key.yao.expandlistview.sample.adapter.MyListAdapter;
import moe.key.yao.expandlistview.sample.model.Model;

/**
 * Created by Key on 2015/1/23.
 */
public class SimpleActivity extends ActionBarActivity {

    private ExpandListView list;
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        init();
    }

    private void init() {
        list = (ExpandListView) findViewById(R.id.list);

        List<Model> data = new ArrayList<Model>();
        for (int i = 0 ; i < 20 ; i ++) {
            Model m = new Model();
            m.setTitle("Item " + (i + 1));
            List<String> subStr = new ArrayList<String>();
            for (int j = 0 ; j < (new Random().nextInt(5) + 2) ; j ++) {
                subStr.add("Sub Item " + (j + 1));
            }
            m.setSubString(subStr);
            data.add(m);
        }
        adapter = new MyListAdapter(this, data);
        list.setAdapter(adapter);
        list.setExpandDuration(500);
        list.setExpandInterpolator(new DecelerateInterpolator());

        boolean allCanOpen = getIntent().getBooleanExtra("all_can_open", false);
        boolean clickClose = getIntent().getBooleanExtra("click_close", false);
        boolean allItemOpen = getIntent().getBooleanExtra("all_item_open", false);

        list.setAllItemCanOpen(allCanOpen);
        list.setCanClickClose(clickClose);
        list.setOpenAllItem(allItemOpen);

        list.setOnParentItemClickListener(new ListParentOnItemClickListener());
        list.setOnChildItemClickListener(new ListChildOnItemClickListener());


    }

    private class ListParentOnItemClickListener implements OnParentItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
//            Toast.makeText(MainActivity.this, "Item [" + (position + 1) + "]", Toast.LENGTH_SHORT).show();
        }
    }

    private class ListChildOnItemClickListener implements OnChildItemClickListener {

        @Override
        public void onItemClick(View view, int parentPosition, int childPosition) {
//            Toast.makeText(MainActivity.this, "Item [" + (parentPosition + 1) + " , " + (childPosition + 1) + "]", Toast.LENGTH_SHORT).show();
        }
    }

}
