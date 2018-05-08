package com.yjy.okrxcache_core.Utils;

import android.util.Log;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/05/08
 *     desc   :打印控制器
 *     version: 1.0
 * </pre>
 */

public class LogUtils {

    private static boolean isDebug = false;

    private volatile static LogUtils instance;

    public static LogUtils getInstance(){
        if(instance == null){
            synchronized (LogUtils.class){
                if(instance == null){
                    instance = new LogUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     * @param isDebug
     */
    public void init(boolean isDebug){
        LogUtils.isDebug = isDebug;
    }

    /**
     * Log e打印
     * @param title
     * @param msg
     */
    public void e(String title,Object msg){
        if(LogUtils.isDebug){
            Log.e(title,msg.toString());
        }

    }

    /**
     * Log i 打印
     * @param title
     * @param msg
     */
    public void i(String title,Object msg){
        if(LogUtils.isDebug){
            Log.i(title,msg.toString());
        }
    }


    /**
     * Log d 打印
     * @param title
     * @param msg
     */
    public void d(String title,Object msg){
        if(LogUtils.isDebug){
            Log.d(title,msg.toString());
        }

    }

    /**
     * Log w 打印
     * @param title
     * @param msg
     */
    public void w(String title,Object msg){
        if(LogUtils.isDebug){
            Log.w(title,msg.toString());
        }

    }
}
