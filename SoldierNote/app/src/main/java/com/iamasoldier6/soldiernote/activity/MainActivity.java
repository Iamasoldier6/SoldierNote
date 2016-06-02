package com.iamasoldier6.soldiernote.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.iamasoldier6.soldiernote.R;
import com.iamasoldier6.soldiernote.adapter.NoteAdapter;
import com.iamasoldier6.soldiernote.database.NoteProvider;
import com.iamasoldier6.soldiernote.view.DeleteView;
import com.iamasoldier6.soldiernote.view.NewNoteView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by Iamasoldier6 on 6/1/16.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    ListView listView;
    public Cursor cursor;
    public NoteAdapter noteAdapter;
    public boolean isLongPress = false;

    public DeleteView deleteView;
    public NewNoteView newNoteView;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Button mPicButton;
    private Button mNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setLogo(R.drawable.add);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nv_menu);

        mPicButton = (Button) findViewById(R.id.button_picture);
        mNoteButton = (Button) findViewById(R.id.button_note);
        mPicButton.setOnClickListener(this);
        mNoteButton.setOnClickListener(this);

        /**
         * 标题文字须在 setSupportActionBar 之前
         */
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false); // 隐藏标题

        setupDrawerContent(mNavigationView);

        /**
         * 设置状态栏背景色
         */
        if (android.os.Build.VERSION.SDK_INT > 18) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个颜色给系统栏
            tintManager.setTintColor(Color.parseColor("#33CC33"));
        }

        deleteView = (DeleteView) findViewById(R.id.deleteView);
        deleteView.setContext(this); //使DeleteView获得活动引用
        newNoteView = (NewNoteView) findViewById(R.id.newNoteView);
    }

    /**
     * 加载便签列表
     */
    @Override
    protected void onStart() {
        super.onStart();
        listView = (ListView) findViewById(R.id.listView_note);
        cursor = getContentResolver().query(NoteProvider.CONTENT_URI, null, null, null, null);
        noteAdapter = new NoteAdapter(this, cursor);
        listView.setAdapter(noteAdapter);
        listView.setVerticalScrollBarEnabled(false); //进度条隐藏

        //点击列表项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "点击了 id=" + id);
                if (isLongPress) {
                    noteAdapter.setCheckStyle(position);
                } else {
                    Intent intent = new Intent(getApplication(), EditActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });

        //长按列表项
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "长按");
                if (!isLongPress) {
                    isLongPress = true;
                    //时间不可见,选择框可见
                    noteAdapter.setPressState(true);
                    //添加按钮不可点击，删除按钮可见
                    newNoteView.setEnabled(false);
                    deleteView.setVisibility(View.VISIBLE);
                    new Thread() {
                        @Override
                        public void run() {
                            deleteView.setY(deleteView.getHeight() + deleteView.getY());
                            while (deleteView.getY() > listView.getY() + listView.getHeight()) {
                                Log.d("debug", "删除按钮慢慢出现");
                                deleteView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteView.setY(deleteView.getY() - 20);
                                    }
                                });
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (deleteView.getY() + deleteView.getHeight() < newNoteView.getY() + newNoteView.getHeight()) {
                                deleteView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteView.setY(deleteView.getY() + newNoteView.getY() + newNoteView.getHeight() - deleteView.getY() - deleteView.getHeight());
                                    }
                                });
                            }
                        }
                    }.start();
                }
                return false;
            }
        });
    }

    /**
     * 返回键的监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isLongPress) {
                noteAdapter.setPressState(false);
                isLongPress = false; //退出长按模式

                new Thread() {
                    @Override
                    public void run() {
                        while (deleteView.getY() <= newNoteView.getY() + newNoteView.getHeight()) {
                            newNoteView.post(new Runnable() {
                                @Override
                                public void run() {
                                    deleteView.setY(deleteView.getY() + 20);
                                }
                            });
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_picture:
                Intent intentPic = new Intent(MainActivity.this, PictureActivity.class);
                startActivity(intentPic);
                break;
            case R.id.button_note:
                Intent intentNote = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intentNote);
                break;
            default:
                break;
        }
    }

}
