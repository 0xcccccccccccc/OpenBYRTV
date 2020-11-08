package com.buptsdmda.openbyrtv

import android.app.Application
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

// vid list:http://tv.byr.cn/vod-show?tags=0_5
class WelcomeActivity : AppCompatActivity() {

    val instance by lazy { this }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)
        Toast.makeText(instance,"Loading vidlist",Toast.LENGTH_SHORT).show()
        var asyncHttpClient=AsyncHttpClient();
        asyncHttpClient.addHeader("Content-Type","text/html; charset=UTF-8")
        asyncHttpClient.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs");
        asyncHttpClient.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        asyncHttpClient.addHeader("Accept-Language","zh-cn,zh;q=0.5")
        asyncHttpClient.addHeader("Host","tv.byr.cn");


        asyncHttpClient.get("http://tv.byr.cn/vod-show?tags=0_5",object:AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                startActivity(intentFor<ChannelActivity>("text" to responseBody).newTask().clearTask()) // Pop self & Push new ChannelActivity
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                if(statusCode==403){
                    //Toast.makeText(instance,"Access Denied! IPV6 may still unsuported in your current network!",Toast.LENGTH_SHORT).show()
                    val alert=AlertDialog.Builder(instance)
                    alert.setMessage("与服务器的连接被拒绝，请在IPV6环境下使用本App！")
                    alert.show()
                }
                else {
                    //Toast.makeText(instance, "Network error!", Toast.LENGTH_SHORT).show()
                    val alert=AlertDialog.Builder(instance)
                    alert.setMessage("网络错误！")
                    alert.show()
                }
                System.exit(0);
            }


        })




    }
}
