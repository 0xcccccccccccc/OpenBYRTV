package com.buptsdmda.openbyrtv

import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.buptsdmda.openbyrtv.WebVpnLoginActivity
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.cookie.Cookie
import cz.msebera.android.httpclient.impl.client.BasicCookieStore
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookieStore
import java.util.*

// vid list:http://tv.byr.cn/vod-show?tags=0_5
class WelcomeActivity : AppCompatActivity() {

    val instance by lazy { this }
    override fun onResume() {
        super.onResume()
        Toast.makeText(instance,"Loading vidlist",Toast.LENGTH_SHORT).show()
        var asyncHttpClient=AsyncHttpClient();
        asyncHttpClient.addHeader("Content-Type","text/html; charset=UTF-8")
        asyncHttpClient.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs");
        asyncHttpClient.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        asyncHttpClient.addHeader("Accept-Language","zh-cn,zh;q=0.5")

        if((application as OpenBYRTVApplication).useVpn)
        {
            asyncHttpClient.addHeader("Host","webvpn.bupt.edu.cn");
            val cookieStore=BasicCookieStore()
            val cookie=BasicClientCookie("wengine_vpn_ticket",(application as OpenBYRTVApplication).vpn_cookie)
            cookie.domain="webvpn.bupt.edu.cn"
            cookie.path="/"
            cookie.isSecure=false
            
            cookieStore.addCookie(cookie)
            asyncHttpClient.setCookieStore(cookieStore)
        }
        else{
            asyncHttpClient.addHeader("Host","tv.byr.cn");
        }


        asyncHttpClient.get((application as OpenBYRTVApplication).vid_root+"vod-show?tags=0_5",object:AsyncHttpResponseHandler(){
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
//                if(statusCode==403){
                //Toast.makeText(instance,"Access Denied! IPV6 may still unsuported in your current network!",Toast.LENGTH_SHORT).show()
                val alert=AlertDialog.Builder(instance)


                alert.setMessage("与服务器的连接被拒绝，请尝试登陆WebVpn").setNeutralButton("登陆",object:DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        startActivity(intentFor<WebVpnLoginActivity>().newTask().clearTask())
                    }
                }).setNegativeButton("退出",object:DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        System.exit(0);
                    }
                })


                alert.create().show()
//                }
//                else {
//                    //Toast.makeText(instance, "Network error!", Toast.LENGTH_SHORT).show()
//                    val alert=AlertDialog.Builder(instance).setMessage("网络错误！").setNeutralButton("Exit",object:DialogInterface.OnClickListener {
//                        override fun onClick(dialog: DialogInterface?, which: Int) {
//                            System.exit(0);
//                        }
//
//                    })
//                    alert.create().show()
//                }

            }


        })


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)





    }
}
