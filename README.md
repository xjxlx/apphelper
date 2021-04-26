#### 介绍
{**App开发的小工具**
里面集成了网络、常用工具等组件，为了快速开发测试而生成，项目发布到了码云和github两个平台，可以对应的选择使用。}

#### 使用说明
1.    allprojects{      
            repositories {      
                ...
                maven { url 'https://jitpack.io' }      
            }       
      }

2.    dependencies {        
	        implementation 'com.gitee.xjxlx:apphelper:1.0.1.0.0'
      }

3.    api('com.gitee.xjxlx:apphelper:1.0.1.0.0') {     
            exclude group: 'com.github.bumptech.glide'      
            exclude group: 'com.scwang.smart:refresh-layout-kernel'     
      }

4.
<!-- 音乐播放器的服务 -->
        <service
            android:name="com.android.helper.utils.media.audio.AudioService"
            android:enabled="true"
            android:exported="false" />

5：加入权限
<!-- 电池优化权限 -->
    <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
    <!--通知栏权限-->
    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />


#### 区分平台

1.  github 的地址为：{ 'https://github.com/xjxlx/appHelper' }   
2.  码云的地址为： { 'https://gitee.com/xjxlx/apphelper' } 