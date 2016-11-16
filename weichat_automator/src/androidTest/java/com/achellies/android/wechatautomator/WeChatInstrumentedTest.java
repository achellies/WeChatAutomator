package com.achellies.android.wechatautomator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @see <a href="https://developer.android.com/training/testing/start/index.html">Getting Started with Testing</a>
 */
@RunWith(AndroidJUnit4.class)
public class WeChatInstrumentedTest {
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    private static final int LONG_TIMEOUT = 10000;
    private static final int SHORT_TIMEOUT = 1000;

    private static final String WIDGET_EDITTEXT = EditText.class.getCanonicalName();
    private static final String WIDGET_BUTTON = Button.class.getCanonicalName();
    private static final String WIDGET_TEXTVIEW = TextView.class.getCanonicalName();
    private static final String WIDGET_IMAGEVIEW = ImageView.class.getCanonicalName();
    private static final String WIDGET_MM_WEBKIT = "com.tencent.smtt.webkit.WebView";

    private UiDevice mDevice;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        mDevice.wakeUp();

        // Start from the home screen
        mDevice.pressHome();

        if (isWeChatInstall(appContext)) {
            // 启动微信
            startWeChat(appContext);

            // 等待微信显示出来
            mDevice.wait(Until.hasObject(By.pkg(WECHAT_PACKAGE_NAME).depth(0)), LONG_TIMEOUT);

            // 检查是否已经登录了
            if (!isWeChatLogin()) {
                loginWeiChat("username", "password");
            }

            if (isWeChatLogin()) {
                gotoWeChatHomePage();

                performSearch();

                getHistoryMessage();

                readMessage();
            }
        }
    }

    /**
     * 读取文章
     */
    void readMessage() {
        try {
            UiSelector webViewSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_MM_WEBKIT);
            UiObject webView = mDevice.findObject(webViewSelector);

            Rect visibleBounds = webView.getVisibleBounds();
            mDevice.click(visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top + visibleBounds.top);
            webView.click();

            mDevice.wait(Until.findObjects(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_MM_WEBKIT).clickable(true)), LONG_TIMEOUT);

            mDevice.swipe(visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top + visibleBounds.height() / 2, visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top, 10);
            mDevice.swipe(visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top + visibleBounds.height() / 2, visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top, 10);
        } catch (UiObjectNotFoundException ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     * 获取公共账号的历史消息列表
     */
    void getHistoryMessage() {
        UiSelector historyMessageSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_TEXTVIEW).text("查看历史消息");

        try {
            UiObject historyMessage = mDevice.findObject(historyMessageSelector);
            Rect visibleBounds = historyMessage.getVisibleBounds();
            mDevice.click(visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top + visibleBounds.height() / 2);

            mDevice.wait(Until.findObjects(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_MM_WEBKIT).clickable(true)), LONG_TIMEOUT);
        } catch (UiObjectNotFoundException ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     * 触发点击操作，并进入公共号
     */
    void performSearch() {
        UiSelector searchSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_TEXTVIEW).descriptionContains("搜索");

        try {
            UiObject searchBtn = mDevice.findObject(searchSelector);
            Rect visibleBounds = searchBtn.getVisibleBounds();
            mDevice.click(visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top + visibleBounds.height() / 2);

            // 等待微信的搜索界面出来
            mDevice.wait(Until.findObject(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_EDITTEXT)), LONG_TIMEOUT);

            UiSelector inputEditSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_EDITTEXT).text("搜索");
            mDevice.findObject(inputEditSelector).setText(BuildConfig.AccountName);
            mDevice.pressEnter();

            mDevice.wait(Until.findObject(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_MM_WEBKIT)), LONG_TIMEOUT);

            UiSelector webViewSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_MM_WEBKIT);
            UiObject webView = mDevice.findObject(webViewSelector);

            mDevice.wait(Until.findObjects(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_MM_WEBKIT).clickable(true)), LONG_TIMEOUT);

            visibleBounds = webView.getVisibleBounds();

            mDevice.click(visibleBounds.left + visibleBounds.width() / 2, visibleBounds.top + visibleBounds.top);
        } catch (UiObjectNotFoundException ignore) {
            ignore.printStackTrace();
        }

    }

    /**
     * 检查微信是否有登录，如果没有登录则执行登录逻辑
     *
     * @return
     */
    boolean isWeChatLogin() {
        UiSelector loginSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_BUTTON).text("登录");
        UiObject loginBtn = mDevice.findObject(loginSelector);

        boolean isLogin = true;
        try {
            isLogin = (loginBtn != null && !loginBtn.getVisibleBounds().isEmpty()) ? false : true;
        } catch (UiObjectNotFoundException ignore) {
            ignore.printStackTrace();
        }

        return isLogin;
    }

    /**
     * 通过用户名密码登录微信
     *
     * @param userName
     * @param password
     */
    void loginWeiChat(String userName, String password) {
        try {
            UiSelector loginSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_BUTTON).text("登录");
            UiObject loginBtn = mDevice.findObject(loginSelector);
            Rect visibleBound = loginBtn.getVisibleBounds();
            mDevice.click(visibleBound.left + visibleBound.width() / 2, visibleBound.top + visibleBound.height() / 2);

            mDevice.wait(Until.findObject(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_BUTTON).text("使用其他方式登录")), LONG_TIMEOUT);

            UiSelector loginModeSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_BUTTON).text("使用其他方式登录");
            UiObject loginModeBtn = mDevice.findObject(loginModeSelector);
            visibleBound = loginModeBtn.getVisibleBounds();
            mDevice.click(visibleBound.left + visibleBound.width() / 2, visibleBound.top + visibleBound.height() / 2);

            mDevice.wait(Until.findObject(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_EDITTEXT).text("QQ号/微信号/Email")), LONG_TIMEOUT);

            UiSelector userNameEditSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_EDITTEXT).text("QQ号/微信号/Email");

            UiObject userNameEdit = mDevice.findObject(userNameEditSelector);

            userNameEdit.setText(userName);

            // FIXME
            // 密码框是个NAF ("Not Accessibility Friendly")
            // http://stackoverflow.com/questions/25435878/uiautomatorviewer-what-does-naf-stand-for
            // https://stuff.mit.edu/afs/sipb/project/android/docs/tools/testing/testing_ui.html
            // https://stuff.mit.edu/afs/sipb/project/android/docs/guide/topics/ui/accessibility/apps.html
            UiObject passwordLinearLayout = mDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/bai"));
            visibleBound = passwordLinearLayout.getVisibleBounds();
            mDevice.click(visibleBound.left + visibleBound.width() / 2, visibleBound.top + visibleBound.height() / 2);
            passwordLinearLayout.setText(password);

            UiSelector loginBtnSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_BUTTON).text("登录");
            loginBtn = mDevice.findObject(loginBtnSelector);
            visibleBound = loginBtn.getVisibleBounds();
            mDevice.click(visibleBound.left + visibleBound.width() / 2, visibleBound.top + visibleBound.height() / 2);

            mDevice.wait(Until.findObject(By.clazz(WECHAT_PACKAGE_NAME, WIDGET_TEXTVIEW).descContains("搜索")), LONG_TIMEOUT);

        } catch (UiObjectNotFoundException ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     * 跳转到微信首页
     */
    void gotoWeChatHomePage() {
        // 检查ActionBar上是否有返回按钮
        try {
            UiSelector backBtnSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_IMAGEVIEW).descriptionContains("返回").enabled(true);
            UiObject backBtn = mDevice.findObject(backBtnSelector);
            while (backBtn != null && backBtn.exists() && !backBtn.getVisibleBounds().isEmpty()) {
                mDevice.pressBack();

                backBtn = mDevice.findObject(backBtnSelector);
            }

            UiSelector homeTabSelector = new UiSelector().packageName(WECHAT_PACKAGE_NAME).className(WIDGET_TEXTVIEW).text("微信").enabled(true);

            UiObject homeTab = mDevice.findObject(homeTabSelector);
            if (homeTab != null) {
                Rect visibleBound = homeTab.getVisibleBounds();
                mDevice.click(visibleBound.left + visibleBound.width() / 2, visibleBound.top + visibleBound.height() / 2);
            }
        } catch (UiObjectNotFoundException ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     * 检查微信是否安装
     *
     * @param context
     * @return
     */
    boolean isWeChatInstall(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(WECHAT_PACKAGE_NAME);
        if (intent == null) {
            return false;
        }
        return true;
    }

    /**
     * 启动微信
     *
     * @param context
     */
    void startWeChat(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(WECHAT_PACKAGE_NAME);
        if (intent == null) {
            return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}
