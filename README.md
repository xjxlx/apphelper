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

使用步骤：

1：如果使用音乐播放器的时候，必须要加入服务的配置，否则不会播放
        <!-- 音乐播放器的服务 -->
        <service
            android:name="com.android.helper.utils.media.audio.AudioService"
            android:enabled="true"
            android:exported="false" />

2：如果要收集App的异常信息的话，必须要给予应用读写的权限，否则就会导致无法正常的存入日志
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

 
# appphlper 使用步骤

# 第一步：把项目代码更新到最新，这一步不是必须，具体要看现有的代码中有没有apphelper这个库的对象，如果没有就需要拉一下最新的代码，获取apphelper的对象。

# 第二步：在 terminal 中执行两个命令 
##       1:初始化子模块 [ git  submodule init ] 
##       2:更新子模块 [ git  submodule update ]

# 第三步：如果在第二步的时候报错了，那么接下来在terminal 执行下面的命令
##       1:进入apphelper库中[ cd apphelper ]
##       2：切换到主分支 [ git checkout master ]

# 第四步：重新Build 一下，点击[Sync Project with Gradle files] 按钮一下，或者点击 路径 [File ---> Sync Project with Gradle files]  

# PullListRvUtil 和 RvUtil 的工具类替换为 RefreshUtil  +  RecycleViewFrameWork 联合使用，具体的使用方式，请查看工具类说明
## PullListRvUtil 和 RvUtil 的占位图 替换 为 EmptyPlaceholder 使用，具体的使用方式，请查看工具类说明


使用变更：
    1：android.enableJetifier=true
    必须要在 gradle.properties中加入这个权限，否则有些老的android.support.xxx的功能将会无法使用。