package com.buptsdmda.openbyrtv

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import java.net.CookieHandler
import java.net.CookieStore


class OpenBYRTVApplication:Application(){
    private var proxy: HttpProxyCacheServer? = null
    var vid_root:String="http://tv.byr.cn/"
    var useVpn:Boolean=false
    var vpn_cookie:String=""

    fun getProxy(context: Context): HttpProxyCacheServer? {
        val app: OpenBYRTVApplication = context.getApplicationContext() as OpenBYRTVApplication
        return if (app.proxy == null) app.newProxy().also({ app.proxy = it }) else app.proxy
    }

    private fun newProxy(): HttpProxyCacheServer? {

        return HttpProxyCacheServer.Builder(this)
            .cacheDirectory(Utils.getVideoCacheDir(this))
            .build()
    }

}