1. adb devices查看有哪些设备，然后通过 adb -s来指定用那台设备
2. add push ./Genymotion-ARM-Translation.zip /mnt/sdcard/ 把ARM转换包push到手机上
3. adb shell sh /system/bin/flash-archive.sh /mnt/sdcard/Genymotion-ARM-Translation.zip 安装这个转换包，然后重启模拟器
4. 安装微信 adb install ./weixin6330android920.apk
5. 安装控制微信的程序adb install ./weichat_automator-debug-androidTest.apk
6. 切换到手机的语言环境到中文系统
    1. adb shell set prop persist.sys.language zh
    2. adb shell stop
    3. adb shell start
    4. add shell sleep 5
7. 通过控制程序登录微信
8. 通过控制程序进行制定公共号文章的读取adb -s 760BBKT227G6 shell am instrument -w -r   -e debug false -e class com.achellies.android.wechatautomator.WeChatInstrumentedTest com.achellies.android.wechatautomator.test/android.support.test.runner.AndroidJUnitRunner

adb push /Users/achellies/github/WeChatAutomator/weichat_automator/build/outputs/apk/weichat_automator-debug-androidTest.apk /data/local/tmp/com.achellies.android.wechatautomator.test
$ adb shell pm install -r "/data/local/tmp/com.achellies.android.wechatautomator.test"
     pkg: /data/local/tmp/com.achellies.android.wechatautomator.test