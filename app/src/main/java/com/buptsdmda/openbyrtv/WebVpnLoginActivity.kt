package com.buptsdmda.openbyrtv

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.just.agentweb.AgentWeb
import org.jetbrains.anko.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import kotlin.coroutines.suspendCoroutine


class WebVpnLoginActivity : AppCompatActivity() {
    val instance by lazy { this }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SQLiteDatabase.openDatabase()

        setContentView(R.layout.activity_web_vpn_login)


//        val mAgentWeb = AgentWeb.with(this)
//            .setAgentWebParent((findViewById<LinearLayout>(R.id.webvpn_view)), LinearLayout.LayoutParams(-1, -1))
//            .useDefaultIndicator()
//            .createAgentWeb()
//            .ready()
//            .go("http://webvpn.bupt.edu.cn/login")

        val code = """
            function setUserPass(user,pass){
            ${'$'}("#user_name").val(user)
            ${'$'}(".password-input")[0].children[0].value=pass
            }
        """.trimIndent()


//        mAgentWeb.jsAccessEntrace.quickCallJs("$('user-name')")


        val webView = findViewById<WebView>(R.id.webvpn_view)
        WebView.setWebContentsDebuggingEnabled(true)
        webView.webChromeClient=object :WebChromeClient(){
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                result!!.confirm()
                val res=message!!.split("\n")
                val username=res[0]
                val password=res[1]
                SQLiteHelper(instance).use {
                    val cv=ContentValues()
                    cv.put("username",username)
                    cv.put("password",password)
                    update("User", cv, "_id=0",null)
                }
                //return super.onJsAlert(view, url, message, result)
                return true

            }
        }
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url!!.contains("77726476706e69737468656265737421e4e10f9e3e22265370")) {
                    // Login successfully
                    (application as OpenBYRTVApplication).vid_root =
                        "http://webvpn.bupt.edu.cn/http/77726476706e69737468656265737421e4e10f9e3e22265370/"
                    (application as OpenBYRTVApplication).useVpn = true
                    (application as OpenBYRTVApplication).vpn_cookie =
                        CookieManager.getInstance().getCookie("http://webvpn.bupt.edu.cn/")
                            .split(";")[0].split("=")[1]

                    startActivity(intentFor<WelcomeActivity>().newTask().clearTask())
                } else if (url!!.contains("webvpn")) {
                    SQLiteHelper(instance).use {

                        select("User").whereArgs("_id=0").exec {
                                moveToNext()
                                val username = getString(1)
                                val password = getString(2)
                            webView.evaluateJavascript("""
                        function setLoginHook(){
                            ${'$'}(".el-form")[0].onsubmit=function(){
                            alert(${'$'}("#user_name").val()+'\n'+${'$'}(".password-input")[0].children[0].value)}
                        }
                        function setUserPass(user,pass){
                            ${'$'}("#user_name").val(user)
                            ${'$'}(".password-input")[0].children[0].value=pass
                        }
                        setLoginHook()
                        setUserPass("$username","$password")
                        """.trimIndent(), null
                            )



                        }






//                        select("User","username,password").whereArgs("_id=0").exec {
//                            val username=getString(1)
//                            val password=getString(2)
//                            webView.evaluateJavascript("""
//                        function setLoginHook(){
//                            ${'$'}(".el-form")[0].onsubmit=function(){
//                            alert(${'$'}("#user_name").val()+'\n'+${'$'}(".password-input")[0].children[0].value)}
//                        }
//                        function setUserPass(user,pass){
//                            ${'$'}("#user_name").val(user)
//                            ${'$'}(".password-input")[0].children[0].value=pass
//                        }
//                        setLoginHook()
//                        setUserPass("$username","$password")
//                        """.trimIndent(), object : ValueCallback<String> {
//                                override fun onReceiveValue(value: String?) {
//                                    Log.d("JS:", value!!)
//                                }
//
//                            })
//
//
//
//                        }
                    }


                }
            }


//            override fun shouldInterceptRequest(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): WebResourceResponse? {
//                if(request!!.method.equals("POST"))
//                    if(request!!.url.toString().contains("do-login"))
//                        webView.evaluateJavascript("""
//                        function getUserPass(){
//                            return ${'$'}("#user_name").val()+'\n'+${'$'}(".password-input")[0].children[0].value
//                        }
//                        getUserPass()
//                        """.trimIndent(), object : ValueCallback<String> {
//                            override fun onReceiveValue(value: String?) {
//                                Log.d("JS:", value!!)
//                            }
//
//                        })
//
//
//
//
//                return super.shouldInterceptRequest(view, request)
//            }


        }

        CookieManager.getInstance().removeAllCookies(null)// clean cookie for demo


        webView.settings.javaScriptEnabled = true
        webView.loadUrl("http://webvpn.bupt.edu.cn/http/77726476706e69737468656265737421e4e10f9e3e22265370/show")
//        webView.evaluateJavascript("""
//            function setUserPass(user,pass){
//            ${'$'}("#user_name").val(user)
//            ${'$'}(".password-input")[0].children[0].value=pass
//            }
//            setUserPass("2018213312","200077766a")
//        """.trimIndent(), object:ValueCallback<String> {
//            override fun onReceiveValue(value: String?) {
//                Log.d("JS:",value!!)
//            }
//
//        })

    }
}
