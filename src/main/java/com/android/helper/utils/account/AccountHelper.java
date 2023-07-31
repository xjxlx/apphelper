package com.android.helper.utils.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.common.utils.LogWriteUtil;
import com.android.helper.common.CommonConstants;

/**
 * 添加账号的辅助工具类 使用规则： 1：必须添加和检测权限：
 * <p>
 * <uses-permission android:name="android.permission.GET_ACCOUNTS"
 * android:maxSdkVersion="22" /><!--获取账号信息的权限-->
 * <uses-permission android:name="android.permission.AUTHENTICATE_ACCOUNTS"
 * android:maxSdkVersion="22" /><!--添加账号的权限-->
 * </p>
 * 2：必须注册清单文件，且添加resource的资源文件，详细可见类中的描述
 * <p>
 * <service android:name=".test.app.account.AccountService" android:enabled=
 * "true" android:exported="true">
 * <intent-filter>
 * <action android:name="android.accounts.AccountAuthenticator" />
 * </intent-filter>
 * <meta-data android:name="android.accounts.AccountAuthenticator"
 * android:resource="@xml/account_authenticator" />
 * </service>
 * </p>
 * 3:注册provider,用来进程之间通信,此处的authorities字段必须保持和上下文一直
 * <p>
 * <provider android:name=".test.app.account.SyncProvider" android:authorities=
 * "com.android.app.account.authority" />
 * </P>
 * 4：注册服务，用来接收账号拉货的同步数据
 * <p>
 * <service android:name=".test.app.account.SyncService" android:enabled="true"
 * android:exported="true"> <intent-filter>
 * <action android:name="android.content.SyncAdapter" /> </intent-filter>
 * <p>
 * <meta-data android:name="android.content.SyncAdapter" android:resource=
 * "@xml/sync_adapter" /> </service>
 * </P>
 * 5：新建广播类，用来接收账号拉活的通知，必须实现过滤：com.android.app.lifecycle 标记，
 * 且必须建立广播类：com.android.account.LifecycleReceiver
 */
public class AccountHelper {

    private final LogWriteUtil logWriteUtil = new LogWriteUtil(CommonConstants.FILE_LIFECYCLE_NAME + ".txt");
    private String ACCOUNT_TYPE;
    private String ACCOUNT_NAME;
    private String ACCOUNT_PASSWORD;
    private String ACCOUNT_AUTHORITY;

    public AccountHelper() {
    }

    private static AccountHelper mAccountHelper;

    public static AccountHelper getInstance() {
        if (mAccountHelper == null) {
            mAccountHelper = new AccountHelper();
        }
        return mAccountHelper;
    }

    public AccountHelper addAccountType(String accountType) {
        this.ACCOUNT_TYPE = accountType;
        return mAccountHelper;
    }

    public AccountHelper addAccountName(String accountName) {
        this.ACCOUNT_NAME = accountName;
        return mAccountHelper;
    }

    public AccountHelper addAccountAuthority(String accountAuthority) {
        this.ACCOUNT_AUTHORITY = accountAuthority;
        return mAccountHelper;
    }

    public AccountHelper addAccountPassword(String accountPassword) {
        this.ACCOUNT_PASSWORD = accountPassword;
        return mAccountHelper;
    }

    /**
     * 添加账户
     */

    public AccountHelper addAccount(Context context) {
        try {
            if (TextUtils.isEmpty(ACCOUNT_TYPE)) {
                logWriteUtil.write("添加账户的类型为空，停止后续的操作");

                throw new NullPointerException("添加账户的类型为空");
            }
            if (TextUtils.isEmpty(ACCOUNT_NAME)) {
                logWriteUtil.write("添加账户的名字为空，停止后续的操作");
                throw new NullPointerException("添加账户的名字为空");
            }

            if (TextUtils.isEmpty(ACCOUNT_PASSWORD)) {
                logWriteUtil.write("添加账户的密码为空，停止后续的操作");
                throw new NullPointerException("添加账户的密码为空");
            }

            AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                logWriteUtil.write("已经拥有账号信息,就不去再次添加新的账户");

            } else {
                logWriteUtil.write("没有账号信息，去=添加新的账户");
                Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
                accountManager.addAccountExplicitly(account, ACCOUNT_PASSWORD, new Bundle());
                logWriteUtil.write("账号添加成功！");
            }
        } catch (Exception e) {
            logWriteUtil.write("账号添加数据异常!");
        }
        return mAccountHelper;
    }

    public AccountHelper autoSync() {
        try {
            if (TextUtils.isEmpty(ACCOUNT_AUTHORITY)) {
                logWriteUtil.write("添加账户的Authority为空，停止了后续的操作");
                throw new NullPointerException("添加账户的Authority为空");
            }
            // 进程间通信
            Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
            // 是否同步
            ContentResolver.setIsSyncable(account, ACCOUNT_AUTHORITY, 1);
            // 是否自动同步
            ContentResolver.setSyncAutomatically(account, ACCOUNT_AUTHORITY, true);
            // 同步到队列中去
            ContentResolver.addPeriodicSync(account, ACCOUNT_AUTHORITY, new Bundle(), 1);
            logWriteUtil.write("账号同步数据成功！");
        } catch (Exception e) {
            logWriteUtil.write("账号同步数据异常：" + e.getMessage());
        }
        return mAccountHelper;
    }

}
