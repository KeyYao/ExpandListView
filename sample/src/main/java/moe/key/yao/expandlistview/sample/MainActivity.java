package moe.key.yao.expandlistview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        findViewById(R.id.all_can_open_btn).setOnClickListener(this);
        findViewById(R.id.all_item_open_btn).setOnClickListener(this);
        findViewById(R.id.one_open_btn).setOnClickListener(this);
        findViewById(R.id.can_not_click_close_btn).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SimpleActivity.class);
        switch (v.getId()) {
            case R.id.all_can_open_btn:
                intent.putExtra("all_can_open", true);
                intent.putExtra("click_close", true);
                intent.putExtra("all_item_open", false);
                break;

            case R.id.all_item_open_btn:
                intent.putExtra("all_can_open", false);
                intent.putExtra("click_close", true);
                intent.putExtra("all_item_open", true);
                break;

            case R.id.one_open_btn:
                intent.putExtra("all_can_open", false);
                intent.putExtra("click_close", true);
                intent.putExtra("all_item_open", false);
                break;

            case R.id.can_not_click_close_btn:
                intent.putExtra("all_can_open", false);
                intent.putExtra("click_close", false);
                intent.putExtra("all_item_open", false);
                break;
        }
        startActivity(intent);
    }
}
