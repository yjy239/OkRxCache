package com.yjy.okrxcache_core.rx.core;



import android.util.Log;

import com.yjy.okrxcache_base.AutoCache;
import com.yjy.okrxcache_base.LifeCache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HTTP;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   : data which  the Cache Mode of every method or class
 *     version: 1.0
 * </pre>
 * relativeurl+param's annonation+param+returntype this is key
 */

public class CacheMethod {
    private long mLifeTime;
    private boolean mFromNet;
    private Method mMethod;
    static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    private String mRelativeUrl;
    private String mParamters="";
    private String mParamterAnnations="";
    private String mReturnType = "";
    private String mObjectString="";


    public CacheMethod(Builder builder){
        this.mLifeTime = builder.mLifeTime;
        this.mFromNet = builder.mFromNet;
        this.mMethod = builder.mMethod;
        this.mRelativeUrl = builder.mRelativeUrl;
        this.mParamters = builder.mParamters;
        this.mParamterAnnations = builder.mParamterAnnations;
        this.mReturnType = builder.mReturnType;
        this.mObjectString = builder.mObjectString;
        Log.e("mObjectString",mObjectString);
    }

    public Method getMethod() {
        return mMethod;
    }

    public String getKey(){
        return mRelativeUrl+mReturnType+mParamterAnnations+mParamters+mObjectString;
    }

    public long getLifeTime(){
        return mLifeTime;
    }

    public boolean isNetContronller(){
        return mFromNet;
    }

    public void process(Object[] objects){
        if(objects!=null&&objects.length>0){
            for(Object o : objects){
                mObjectString = mObjectString+o.toString();
            }
        }
    }

    public static class Builder{
        private long mLifeTime;
        private Method mMethod;
        private boolean mFromNet;
        private String mRelativeUrl;
        private Set<String> mRelativeUrlParamNames;
        private String mHttpMethod;;
        private String mParamters="";
        private String mParamterAnnations="";
        private String mReturnType = "";
        private Object[] mParamterObjects;
        private String mObjectString="";
        private AutoCache mAutoCache;

        public Builder(AutoCache cache,Method method,Object[] objects){
            this.mMethod = method;
            this.mParamterObjects = objects;
            this.mAutoCache = cache;
        }

        public CacheMethod build(){
            LifeCache life = mMethod.getAnnotation(LifeCache.class);

            if(life != null){
                long duaration = life.duaration();
                TimeUnit unit = life.unit();
                mLifeTime = unit.toSeconds(duaration);
                mFromNet = life.setFromNet();
            }else if(mAutoCache != null&&mAutoCache.open()){
                long duaration = mAutoCache.duaration();
                mLifeTime = mAutoCache.unit().toSeconds(duaration);
                mFromNet = mAutoCache.setFromNet();
            }

            parseAnntioans(mMethod);
            return new CacheMethod(this);
        }

        private void parseAnntioans(Method method){
            for (Annotation annotation : method.getAnnotations()) {
                parseMethodAnnotation(annotation);
            }

            //获取参数的annonation
            for(Annotation[] annotations : mMethod.getParameterAnnotations()){
                for(Annotation paramAnnation : annotations){
                    if(paramAnnation.annotationType() != null){
//                        Log.e("split",split.toString());
                        String[] split = paramAnnation.annotationType().toString().split("[.]");
                        mParamterAnnations = mParamterAnnations+split[split.length-1];
                    }

                }
            }

            for(Type type : mMethod.getGenericParameterTypes()){
                String[] split = type.toString().split("[.]");
                mParamters = mParamters + split[split.length-1].toString();
            }

            mReturnType = mMethod.getGenericReturnType().toString();

            if(mParamterObjects!=null&&mParamterObjects.length>0){
                for(Object o : mParamterObjects){
                    mObjectString = mObjectString+o.toString();
                }

            }

            
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof DELETE) {
                parseHttpMethodAndPath("DELETE", ((DELETE) annotation).value(), false);
            } else if (annotation instanceof GET) {
                parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
            } else if (annotation instanceof HEAD) {
                parseHttpMethodAndPath("HEAD", ((HEAD) annotation).value(), false);
            } else if (annotation instanceof PATCH) {
                parseHttpMethodAndPath("PATCH", ((PATCH) annotation).value(), true);
            } else if (annotation instanceof POST) {
                parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
            } else if (annotation instanceof PUT) {
                parseHttpMethodAndPath("PUT", ((PUT) annotation).value(), true);
            } else if (annotation instanceof OPTIONS) {
                parseHttpMethodAndPath("OPTIONS", ((OPTIONS) annotation).value(), false);
            } else if (annotation instanceof HTTP) {
                HTTP http = (HTTP) annotation;
                parseHttpMethodAndPath(http.method(), http.path(), http.hasBody());
            }
        }


        private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
            if (this.mHttpMethod != null) {
                return;
            }
            this.mHttpMethod = httpMethod;

            if (value.isEmpty()) {
                return;
            }

            // Get the relative URL path and existing query string, if present.
            int question = value.indexOf('?');
            if (question != -1 && question < value.length() - 1) {
                // Ensure the query string does not have any named parameters.
                String queryParams = value.substring(question + 1);
                Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
                if (queryParamMatcher.find()) {
                    return;
                }
            }

            this.mRelativeUrl = value;
            this.mRelativeUrlParamNames = parsePathParameters(value);
        }

        /**
         * Gets the set of unique path parameters used in the given URI. If a parameter is used twice
         * in the URI, it will only show up once in the set.
         */
        static Set<String> parsePathParameters(String path) {
            Matcher m = PARAM_URL_REGEX.matcher(path);
            Set<String> patterns = new LinkedHashSet<>();
            while (m.find()) {
                patterns.add(m.group(1));
            }
            return patterns;
        }


    }
}
