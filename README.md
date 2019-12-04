# PullToRefresh

#### 实现思路

采用自定义viewGroup方式，自己内部拦截和处理事件，实现上啦下拉回弹效果，下拉刷新。

#### 优点

- 应用灵活，自己可灵活配置RecyclerView的属性。
- 外部可自定义刷新头。刷新头实现PullCallBack接口，可实现状态变化监听
- 外部设置RefreshListener接口监听，可得到下拉刷新的回调
- 也可以不配置刷新头，会有顶部和底部的过度拉伸回弹效果

#### 待拓展

- 下拉加载未实现
- 动态配置一些属性参数，未考虑周全


#### 用法

```
    <com.okay.library.PullToRefresh
        android:id="@+id/pullLayout"
        android:background="@color/colorAccent"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/recycleView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </com.okay.library.PullToRefresh>
```

