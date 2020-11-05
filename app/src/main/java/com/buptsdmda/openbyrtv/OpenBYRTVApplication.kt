package com.buptsdmda.openbyrtv

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer



class OpenBYRTVApplication:Application(){
    private var proxy: HttpProxyCacheServer? = null

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