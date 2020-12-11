package com.buptsdmda.openbyrtv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationClickReceiver extends BroadcastReceiver {


    @Override

    public void onReceive(Context context, Intent intent) {

        //todo 跳转之前要处理的逻辑

        //Log.i("TAG", "userClick:我被点击啦！！！ ");

        //Intent newIntent = new Intent(context, Main2Activity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //context.startActivity(newIntent);
//        context.stopService(new Intent(context,AudioService.class));
        System.exit(0); // Stupid though but it works.
    }

}