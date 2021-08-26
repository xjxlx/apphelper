package com.android.helper.utils.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.android.helper.utils.LogUtil;

/**
 * 添加账号服务实现类,用来添加账号使用，此服务模式实现就行，必须在清单文件中去注册
 * <service
 * android:name=".test.app.account.AccountService"
 * android:enabled="true"
 * android:exported="true">
 * <intent-filter>
 * <action android:name="android.accounts.AccountAuthenticator" />
 * </intent-filter>
 * <p>
 * <meta-data
 * android:name="android.accounts.AccountAuthenticator"
 * android:resource="@xml/account_authenticator" />
 * </service>
 */
public class AccountService extends Service {

    private AccountAuthenticator myAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        if (myAccount == null) {
            myAccount = new AccountAuthenticator(getBaseContext());
        }
    }

    public AccountService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myAccount.getIBinder();
    }

    static class AccountAuthenticator extends AbstractAccountAuthenticator {
        private final Context mContext;

        public AccountAuthenticator(Context context) {
            super(context);
            this.mContext = context;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            return null;
        }
    }
}