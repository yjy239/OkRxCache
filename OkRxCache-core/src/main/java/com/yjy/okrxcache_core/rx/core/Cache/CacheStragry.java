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
    //优先缓存显示,之后会显示网络
    ALL(true),
    ONLYDISK(true),
    ONLYMEMORY(true),
    ONLYNETWORK(true),
    //优先显示缓存，找到了就不找网络
    FIRSTCACHE(true);


    private boolean getOutdata;

    CacheStragry(boolean getOutdata){
        this.getOutdata = getOutdata;
    }

    public boolean isOutDate(){
        return getOutdata;
    }

    public void setOutdata(boolean outdata){
        this.getOutdata = outdata;
    }


}
