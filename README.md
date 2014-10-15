# AisenForAndroid


## 说明
AisenForAndroid是一个android快速开发框架，内置的orm、ioc、bitmaploader均修改自afinal1.0版本。不同于其他，Aisen专注如何使用这些组件来更快速更健壮的开发app。四层结构：UI层、业务接口层、持久层、网络通讯层。面向敏捷、AOP编程，基于Aisen，让你的工作更倾向于具体的业务逻辑、UI特效开发。

## 我能做什么

 * 使用ViewInject绑定view
  * fragment可以
  * activity可以
  * adapter的itemview可以
  * 任何地方
 * BitmapLoader，一行代码加载图片
  * 可以加载web、sdcard、assets、drawable、contentprovider、任何地方
  * 可以裁剪图片、圆角、压缩、任意处理
  * 二级缓存
  * 下载进度回调，开始、错误、进度、结束等事件
  * 其他配置：Displayer、LRU算法
 * ORM
  * 一行代码针对object的sqlite增删改查操作
 * 业务接口规范：[BizLogic](https://github.com/wangdan/AisenForAndroid/wiki/二、BizLogic)
  * 看这里[SinaSDK](https://github.com/wangdan/AisenWeiBo/blob/master/AisenWeiBo/sdk/org/sina/android/SinaSDK.java)，再看这里[OSCSdk](https://github.com/wangdan/AisenForOSC/blob/master/AisenForOSC/sdk/org/aisen/osc/sdk/OSCSdk.java)
 * 网络通讯协议规范：[HttpUtlitiy](https://github.com/wangdan/AisenForAndroid/wiki/三、HttpUtility)
  * http、https
  * soap
  * socket
  * 等
 * 接口缓存管理
  * 将接口数据保存为文件
  * 将接口数据保存到db
  * 将接口数据保存到任何你想保存的地方
 * 异步线程：[WorkTask](https://github.com/wangdan/AisenForAndroid/wiki/附、WorkTask)

## 我还能做什么

 * Loading视图、Failure视图、Empty视图、Empty视图、Content视图这5中基本视图的切换管理，你什么java代码都不需要写，只需要在layout的xml文件中按照自己的需求绘制5种视图的ui部分，就能
  * 加载数据时显示Loading界面
  * 加载失败显示Failure界面，将faild的提示信息绑定到ui显示，如果有ReloadBtn点击自动刷新
  * 加载空数据显示Empty界面，将empty的提示信息绑定到ui显示，如果有ReloadBtn点击自动刷新
  * 加载成功后显示Content界面
 * 只需实现3个方法，你就做完了一个刷新列表界面（支持三种刷新控件：示例一、示例二、示例三）。它有这些功能：
  * 支持ListView、GridView
  * 上拉刷新、自动加载更多
  * 接口分页加载
  * 自动保存、刷新缓存数据
  * 缓存数据时效过期自动刷新列表
  * 保存阅读位置
  * 资源重用
 * app运行时切换网络通讯协议
  * 某些项目，根据网络环境切换网络通讯协议，网段内实现p2p的wifi socket协议通讯，网段外与云服务器的http+soap+https协议通讯，或者与硬件交互的bluetooth socket协议通讯等。不管什么协议之间的切换，只需要一行代码动态配置即可
 * 事件传递机制
  * 没有任何耦合，任何异常信息以Exception的形式上报至UI层。UI层只处理异常信息的UI反馈，业务接口层上报业务异常信息(登录失败、表单错误等业务相关)，网络通讯层上报通讯异常信息(无网络、连接超时等)
 * 4层结构，层与层之间低耦合，恪守单一职责原则，面向AOP
  * UI层，处理ui上的特效、事件、基本的业务逻辑处理
  * SDK层(业务接口层)，规范所有与服务端通讯的接口定义，包括定义方法的入参、出参、异常
  * HttpUtility层(网络通讯层)，实现app所支持的网络通讯协议
  * CacheUtility层(持久层)，针对业务接口的数据持久管理


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

请使用这一行代码加载图片
```java
BitmapLoader.display(BitmapOwner owner, String url, ImageView imageView, ImageConfig ImageConfig)
```

更多详细请查看[BitmapLoader](https://github.com/wangdan/AisenForAndroid/wiki/附、BitmapLoader "BitmapLoader")

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


