package com.yjy.okrxcache_core.Cache.MMCache;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/05/18
 *     desc   : 基于mmap的File文件管理
 *     version: 1.0
 * </pre>
 */
public class MMemoryFile {
    static {
        System.loadLibrary("mmcache");
    }
    private String mPath;
    private long mPtr;
    private long length;

    public MMemoryFile(String path){
        mPath = path;
        mPtr = native_init(mPath);
    }


    public void unmmap(){
        native_unmmap(mPtr);
    }

    public boolean isUnmmap() {
        return mPtr == 0;
    }

    public int getLength(){
        return native_getLength(mPtr);
    }

    public void close(){

        if(!isUnmmap()){
            unmmap();
        }

        mPtr = 0;
    }

    public void writeBytes(byte[] buffer,boolean reset)
            throws IOException {
        if (isUnmmap()) {
            throw new IOException("Can't write to isUnmmap memory file.");
        }

        native_write(mPtr, buffer,reset);
    }

    public byte[] readBytes()
            throws IOException {
        if (isUnmmap()) {
            throw new IOException("Can't read from isUnmmap memory file.");
        }

        return native_read(mPtr);
    }

    public boolean clear() throws IOException{
        if (isUnmmap()) {
            throw new IOException("Can't clear isUnmmap memory file.");
        }

        return native_clear(mPtr);
    }



    private native void native_write(long mPtr, byte[] buffer,boolean reset);
    private native byte[] native_read(long mPtr);

    private native boolean native_clear(long mPtr);

    private native long native_init(String path);
    private native void native_unmmap(long ptr);
    private native int native_getLength(long ptr);
}
