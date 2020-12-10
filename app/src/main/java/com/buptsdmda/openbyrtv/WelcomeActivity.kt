package com.buptsdmda.openbyrtv

import android.R.attr
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.impl.client.BasicCookieStore
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie
import kotlinx.android.synthetic.main.activity_welcome.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask


// vid list:http://tv.byr.cn/vod-show?tags=0_5
class WelcomeActivity : AppCompatActivity() {

    val instance by lazy { this }
    fun startAnimation(view: View?) {
        val animationView=view
        val colorAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), Color.BLACK, Color.DKGRAY)
        colorAnimator.addUpdateListener { animation ->
            val color = animation.getAnimatedValue() as Int //之后就可以得到动画的颜色了
            animationView!!.setBackgroundColor(color) //设置一下, 就可以看到效果.

        }
        colorAnimator.setDuration(1000);
        // 无限循环播放动画
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        // 循环时倒序播放
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.start()
    }
    override fun onResume() {
        super.onResume()
        startAnimation(bg_view)
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
