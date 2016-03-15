package me.hedgehog.myandroid;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity {

    // 过滤我们要得到的Activity；action是一个字符串,代表某一种特定的动作
    public static String action = "me.hedgehog.myandroid.TEST";
    String basepath;
//    ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mainListView= (ListView) findViewById(R.id.main_listview);


        Intent i = this.getIntent();// 得到启动意图；Intent用于封装启动意图，同时还是组件之间通信的重要媒介
        basepath = i.getStringExtra("basepath");// 获取启动意图中的basepath参数值
        if (basepath == null) {
            basepath = "";
        }

        String[] from = new String[]{"title"};// 数据从哪里来，list中存放的map中的key所对应的值

        int[] to = new int[]{android.R.id.text1};// 数据显示到哪里，将list中存放的map中的key所对应的值显示到布局文件中相应的id代表的控件上

        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                android.R.layout.simple_list_item_1, from, to);// 就是一个数据适配器，将数据和视图进行绑定

        this.setListAdapter(adapter);
    }

    public List getData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        // 通过上下文对象获得PackageManager，PackageManager用于获取应用程序(包)的信息
        PackageManager pm = this.getPackageManager();

        // 通过action创建的intent获取对应的activity信息
        Intent intent = new Intent();
        intent.setAction(action);
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

        Map<String, Boolean> has = new HashMap<String, Boolean>();// 保存已经存入的title
        for (ResolveInfo info : infos) {
            String label = info.activityInfo.loadLabel(pm).toString();// 获取label信息   chapter01/1.hello world chapter01/2.test
            String[] labelPath = label.split("/");// 分解label信息

            String title = "";
            if (basepath.length() == 0) {// 判断是第一次启动
                title = labelPath[0];
                if (has.get(title) == null) {
                    Map<String, Object> m = new HashMap<String, Object>();
                    m.put("title", title);
                    has.put(title, true);
                    Intent i = new Intent();
                    i.putExtra("basepath", title);
                    i.setClass(this, MainActivity.class);
                    m.put("intent", i);
                    data.add(m);
                }
            } else if (label.startsWith(basepath)) {//传入的basepath路径与label匹配
                title = labelPath[1];

                Map<String, Object> m = new HashMap<String, Object>();
                m.put("title", title);

                // 设置要启动的activity的信息
                Intent i = new Intent();
                //i.setClassName("me.hedgehog.myandroid","me.hedgehog.myandroid.test01.HelloWorldActivity");
                i.setClassName(info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name);
                m.put("intent", i);
                data.add(m);
            }

        }

        // 对数据进行排序方便查看
        Collections.sort(data, new Comparator<Map>() {
            public int compare(Map m1, Map m2) {
                return Collator.getInstance().compare(m2.get("title"),
                        m1.get("title"));
            }
        });

        return data;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // 得到点击的listview子项对应的map
        Map m = (Map) l.getItemAtPosition(position);
        // 以map中的intent启动activity
        Intent intent = (Intent) m.get("intent");
        this.startActivity(intent);// 启动intent所指定的Activity
    }

}
