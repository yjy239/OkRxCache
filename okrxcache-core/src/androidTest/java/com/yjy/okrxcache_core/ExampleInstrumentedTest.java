package com.yjy.okrxcache_core;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.yjy.okrxcache_core.Cache.MMCache.MMemoryFile;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * test for memoryFile
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    int count = 3;
    CountDownLatch latch = new CountDownLatch(count);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.yjy.okrxcache_core.test", appContext.getPackageName());
    }

    @Test
    public void testMMemeoryFile()throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String dir = appContext.getFilesDir().getAbsolutePath();
        String file1 = dir+ File.separator+"test1";
        MMemoryFile file = new MMemoryFile(file1);

        String t = "qqqasdascacascdsccascsacsacadscacacasc";
        file.writeBytes(t.getBytes(),true);

        byte[] bytes = file.readBytes();

        String ret = new String(bytes);
        assertEquals(ret,t);
    }

    @Test
    public void testManyMMemeoryFile()throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String dir = appContext.getFilesDir().getAbsolutePath();

        for(int i = 0;i<100;i++){
            String file1 = dir+ File.separator+"test"+i;
            Log.e("testfile:",file1);
            MMemoryFile file = new MMemoryFile(file1);
            String t = "qqqasdascacascdsccascsacsacadscacacasc";
            file.writeBytes(t.getBytes(),true);

            byte[] bytes = file.readBytes();

            String ret = new String(bytes);
            assertEquals(ret,t);
            file.close();
        }


    }


    @Test
    public void testMoreMMemeoryFile()throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String dir = appContext.getFilesDir().getAbsolutePath();
        String file1 = dir+ File.separator+"threadtest";
        MMemoryFile file = new MMemoryFile(file1);

        String t = "qqqasdascacascdsccascsacsacadscacacasc";

        for(int i = 0;i<20;i++){
            //累加
            file.writeBytes(t.getBytes(),false);

            byte[] bytes = file.readBytes();

            String ret = new String(bytes);

            StringBuilder builder = new StringBuilder(t);
            for(int j = 0;j<i;j++){
                builder.append(t);
            }
            assertEquals(ret,builder.toString());
        }

    }


    @Test
    public void testThreadMMemeoryFile()throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String dir = appContext.getFilesDir().getAbsolutePath();
        String file1 = dir+ File.separator+"threadtest";
        final MMemoryFile file = new MMemoryFile(file1);

        file.clear();

        final String t = "qqqasdascacascdsccascsacsacadscacacasc|";
        for(int i = 0;i<count;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //累加
                    try {
                        file.writeBytes(t.getBytes(),false);
                        latch.countDown();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        }



        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //直到所有线程完成
                    latch.await();

                    byte[] bytes = file.readBytes();

                    String ret = new String(bytes);

                    StringBuilder builder = new StringBuilder();
                    for(int j = 0;j<count;j++){
                        builder.append(t);
                    }
                    Log.e("result",ret);
                    Log.e("test",builder.toString());
                    assertEquals(ret,builder.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        readThread.start();

        readThread.join();



    }
}
