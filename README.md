# AisenForAndroid


## 说明
AisenForAndroid是一个android快速开发框架，内置的orm、ioc、bitmaploader均修改自afinal1.0版本。不同于其他，Aisen专注如何使用这些组件来更快速更健壮的开发app。四层结构：UI层、业务接口层、持久层、网络通讯层。面向敏捷、AOP编程，基于Aisen，让你的工作更倾向于具体的业务逻辑开发。

## ORM(SqliteUtility)
假设你已经熟悉sqlite操作，那么，SqliteUtility的相关api方法就不需要再说太多。同样面向对象，一行代码对数据库进行增删改查操作，但是剔除了一对一或者一对多这些操作，更多的示例代码请查看[SqliteUtility](https://github.com/wangdan/AisenForAndroid/wiki/附、SqliteUtility "SqliteUtility") 。

## IOC(ViewInject)
继承BaseActivity、ABaseFragment等均可以使用ViewInject对属性自动装配，可以给view设置点击事件，像这样
```java
@ViewInject(idStr = "layoutLoadFailed", click = "reload")
View loadFailureLayout;// 加载失败视图
```

或者像ItemView，也可以自动绑定，像这样
```java
public class TimelineItemView extends AbstractItemView<StatusContent> 
											implements OnClickListener {
	@ViewInject(id = R.id.imgPhoto)
	ImageView imgPhoto;
	@ViewInject(id = R.id.txtName)
	TextView txtName;
  ...
}
```

目前只支持click事件，我是个很实在的人，确实还没有遇到其他例如OnLongClick事件需要绑定就没有添加支持。

## BitmapLoader
这个是我的最爱，当初因为Aisen微博加载大量的图片导致OOM让我头疼不已，如今可以使用BitmapLoader加载网络url、sdcard、assets、drawable、ContentProvider等资源。

 * 二级缓存
  * originate：原始图片
  * compression：压缩图片，包括配置了id、尺寸、裁剪等操作后的压缩尺寸
 * LRU算法管理
 * 配置Downloader
  * web、sdcard、assets、drawable、contentprovider或者自定义
 * Displayer
 * DownloadProcess
 * BitmapCompress
  * 尺寸配置，可以自行针对加载的图片进行压缩 

请使用这一行代码加载图片
```java
BitmapLoader.display(BitmapOwner owner, String url, ImageView imageView, ImageConfig ImageConfig)
```

## 依赖工程
 * [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar)
 * [PhotoView](https://github.com/chrisbanes/PhotoView)
 * [ActionBar-PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh)
 * [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh)
 * [DragSortListView](https://github.com/bauerca/drag-sort-listview)
 * [ChangeLog](https://github.com/gabrielemariotti/changeloglib)
 * [DateTimePicker](https://github.com/flavienlaurent/datetimepicker)
 * [Android PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)


## License

Copyright (c) 2014 Jeff Wang

Licensed under the [Apache License, Version 3.0](http://opensource.org/licenses/GPL-3.0)


