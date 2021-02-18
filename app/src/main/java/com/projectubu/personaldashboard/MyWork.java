package com.projectubu.personaldashboard;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ideabus.model.XlogUtils;
import com.ideabus.model.protocol.BPMProtocol;

import java.util.Random;

public class MyWork extends Worker {
    public BPMProtocol bpmProtocol;
    public boolean isSacning = false;
    public boolean isBtState;

    public MyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //真正需要执行的事
        Boolean isSuccess = doSomthing();
        /**
         * 返回值一共有五种
         * 基础的有是三种
         * Result.success()
         * Result.retry()
         * Result.failure()
         * 剩下两种是带有输出数据
         * Result.success(Data outputData)
         * Result.failure(Data outputData)
         */
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    public Boolean doSomthing() {
        Random ran = new Random();
        int age = ran.nextInt(99);
        Boolean isSuccess = (age%2 == 0);
        XlogUtils.xLog("test","do Som thing:" +age +"%2 == 0:" +isSuccess);
        return isSuccess;
    }

}
