KJmusic
=======
# **KJ音乐 Android 客户端项目简析** #

*注：本文假设你已经有Android开发环境*

启动Eclipse，点击菜单并导入Android客户端项目，请确保你当前的Android SDK是最新版。<br>
如果编译出错，请修改项目根目录下的 project.properties 文件。<br>
推荐使用Android 4.0 以上版本的SDK,请使用JDK1.6编译：

> target=android-17

********本项目采用 GPL 授权协议:
你拥有的权利:
    以任何目的运行此程序的自由;
    再发行复制件的自由;
    改进此程序，并公开发布改进的自由.
你需要注意:
                如果在发布源于GPL的软件的时候，同时添加强制的条款，以在一定程度上保障其它一些人的权益，那么将无权发布该软件。
********欢迎大家在这个基础上进行改进，并与大家分享。

下面将简单的解析下项目：

## **一、项目的目录结构** ##
> 根目录<br>
> ├ src<br>
> ├ libs<br>
> ├ res<br>
> ├ AndroidManifest.xml<br>
> ├ LICENSE.txt<br>
> ├ proguard.cfg<br>
> └ project.properties<br>

下面是src目录的子目录（未来可能变更）：
	> src
	> ├ net.kymjs.music
	> ├ net.kymjs.music.ui
	> ├ net.kymjs.music.ui.fragment
	> ├ net.kymjs.music.ui.widget
	> ├ net.kymjs.music.adapter
	> ├ net.kymjs.music.utils
	> ├ net.kymjs.music.bean
	> ├ net.kymjs.music.service
	> ├ net.kymjs.music.db
	> └ net.kymjs.music.resolve
	> └ net.kymjs.music.receiver
	
	net.kymjs.music	- APP启动及管理包
	net.kymjs.music.ui - APP界面包
	net.kymjs.music.ui.fragment - APP碎片界面
	net.kymjs.music.ui.widget - APP自定义控件
	net.kymjs.music.adapter - APP适配器包
	net.kymjs.music.util - APP工具包，帮助类
	net.kymjs.music.bean - APP实体类包
	net.kymjs.music.service - APP所需服务
	net.kymjs.music.db - APP数据库相关
	net.kymjs.music.resolve - APP网络数据解析器
	net.kymjs.music.inter - 所需接口包
	net.kymjs.music.receiver - 接收全局广播
	
** 下面介绍一下项目中变量命名规则 **
	




