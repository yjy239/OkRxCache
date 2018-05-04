package com.yjy.okrxcache_core.Convert;

import com.yjy.okrxcache_core.CacheResult;

import java.io.File;
import java.lang.reflect.Type;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface IConvert {
    byte[] getBytes(CacheResult cacheResult);

    CacheResult setResult(File file, Type returnType);
}
