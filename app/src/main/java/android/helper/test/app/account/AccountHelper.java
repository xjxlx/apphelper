package android.helper.test.app.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.helper.R;
import android.os.Bundle;

import com.android.helper.utils.LogUtil;

public class AccountHelper {
    private static final String ACCOUNT_TYPE = "android.helper";
    private static final String ACCOUNT_NAME = "xjxliuxing";

    /**
     * 添加账户
     */
    public static void addAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0) {
            LogUtil.e("有数据，就不去再次添加新的账户");
            return;
        }
        Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, "19900713op", new Bundle());
    }

    public static void autoSync(Context context) {
        String authority = context.getResources().getString(R.string.account_authority);
        // 进程间通信
        Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        // 是否同步
        ContentResolver.setIsSyncable(account, authority, 1);
        // 是否自动同步
        ContentResolver.setSyncAutomatically(account, authority, true);
        // 同步到队列中去
        ContentResolver.addPeriodicSync(account, authority, new Bundle(), 1);
    }

}
