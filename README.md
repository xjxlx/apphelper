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
            implementation 'com.github.xjxlx:apphelper:1.1.0.2.5'
      }

3.    api('com.gitee.xjxlx:apphelper:1.0.1.0.0') {     
            exclude group: 'com.github.bumptech.glide'      
            exclude group: 'com.scwang.smart:refresh-layout-kernel'     
      }

使用步骤：

1：如果使用音乐播放器的时候，必须要加入服务的配置，否则不会播放
        <!-- 音乐播放器的服务 -->
        <service
            android:name="com.android.helper.utils.media.audio.AudioService"
            android:enabled="true"
            android:exported="false" />

2：如果要收集App的异常信息的话，必须要给与应用读写的权限，否则就会导致无法正常的存入日志
        String[] strings = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        RxPermissionsUtil permissionsUtil = new RxPermissionsUtil(mContext, strings);
        permissionsUtil.setAllPermissionListener(new AllPermissionsListener() {
            @Override
            public void onRxPermissions(boolean havePermission, Permission permission) {
                LogUtil.e("是否拥有读写权限：" + havePermission);
            }
        });


3：加入对应的权限

    <!-- SDK卡权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!--前台服务的权限-->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <!-- 网络状态权限 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <!-- 电池优化权限 -->
    <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
    <!--通知栏权限-->
    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />
    <!--设置振动， 需要添加权限-->
    <uses-permission android:name="android.permission.VIBRATE" />



#### 区分平台

1.  github 的地址为：{ 'https://github.com/xjxlx/appHelper' }   
2.  码云的地址为： { 'https://gitee.com/xjxlx/apphelper' } 