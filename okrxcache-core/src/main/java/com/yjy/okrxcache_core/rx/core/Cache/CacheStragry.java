package com.yjy.okrxcache_core.rx.core.Cache;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/27
 *     desc   :缓存策略，是否强制获取过期数据
 *     version: 1.0
 * </pre>
 */

public enum  CacheStragry {
    //缓存策略
//    public static final int ALL = 0;
//    public static final int ONLYDISK = 1;
//    public static final int ONLYMEMORY = 2;
//    public static final int ONLYNETWORK = 3;
//    public static final int FIRSTCACHE = 4;
    ALL(true),
    ONLYDISK(true),
    ONLYMEMORY(true),
    ONLYNETWORK(true),
    FIRSTCACHE(true);


    private boolean getOutdata;

    CacheStragry(boolean getOutdata){
        this.getOutdata = getOutdata;
    }

    public boolean isOutData(){
        return getOutdata;
    }

    public void setOutdata(boolean outdata){
        this.getOutdata = outdata;
    }


}
