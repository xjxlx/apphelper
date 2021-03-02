# apphelper
app开发小助手


#   allprojects {
	    repositories {
			maven { url 'https://jitpack.io' }
		}
    }

#	dependencies {
	   implementation 'com.github.xjxlx:apphelper:v1.0.0.0.0'
    }


#   api('com.github.xjxlx:apphelper:v1.0.0.0.1') {
        exclude group: 'com.github.bumptech.glide'
        exclude group: 'com.scwang.smart:refresh-layout-kernel'
    }