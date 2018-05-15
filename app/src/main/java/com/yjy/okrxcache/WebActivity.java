package com.yjy.okrxcache;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.OkRxCache;


import java.io.IOException;

import okhttp3.*;
import okhttp3.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/05/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class WebActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_webview);
        webView = (WebView)findViewById(R.id.web);

        Logger.addLogAdapter(new AndroidLogAdapter());

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Logger.e("getUrl: "+url);
                OkRxCache.with(WebActivity.this)
                        .setStragry(CacheStragry.NOMEMORY)
                        .isDebug(true)
                        .request(url, null)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<CacheResult>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.e("Throwable: "+e);
                            }

                            @Override
                            public void onNext(CacheResult cacheResult) {
//                                try {
                                    Logger.e("result: "+cacheResult.getFromCache()+" "+cacheResult.getData());
//                                }catch (IOException e){
//                                    e.printStackTrace();
//                                }

                            }
                        });
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }
        });

        webView.loadUrl("https://www.baidu.com/");

    }
}
