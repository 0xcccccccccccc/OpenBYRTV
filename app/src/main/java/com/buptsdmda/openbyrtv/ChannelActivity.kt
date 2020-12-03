package com.buptsdmda.openbyrtv


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.impl.client.BasicCookieStore
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

import org.jsoup.Jsoup


class ChannelActivity : AppCompatActivity() {

    val instance by lazy { this }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        var recycleView=findViewById<RecyclerView>(R.id.recyclerView)

        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            startActivity(intentFor<ChooseCacheActivity>().newTask())
        }

        var doc=Jsoup.parse(String(intent.getByteArrayExtra("text")!!))
        var elems=doc.getElementsByClass("content-item__card")
        var channelList=ArrayList<Channel>()

        recycleView.layoutManager=StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        var adapter=ChannelViewAdapter(application as OpenBYRTVApplication,channelList,object : ItemClickCallback() {
            override fun onClick(
                position: Int
            ) {
                var vid=channelList[position].vid
                var asyncHttpClient= AsyncHttpClient()
                asyncHttpClient.addHeader("Content-Type","text/html; charset=UTF-8")
                asyncHttpClient.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs");
                asyncHttpClient.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                asyncHttpClient.addHeader("Accept-Language","zh-cn,zh;q=0.5")

                if((application as OpenBYRTVApplication).useVpn)
                {
                    asyncHttpClient.addHeader("Host","webvpn.bupt.edu.cn");
                    val cookieStore= BasicCookieStore()
                    val cookie= BasicClientCookie("wengine_vpn_ticket",(application as OpenBYRTVApplication).vpn_cookie)
                    cookie.domain="webvpn.bupt.edu.cn"
                    cookie.path="/"
                    cookie.isSecure=false

                    cookieStore.addCookie(cookie)
                    asyncHttpClient.setCookieStore(cookieStore)
                }
                else{
                    asyncHttpClient.addHeader("Host","tv.byr.cn");
                }


                asyncHttpClient.get(vid,object:
                    AsyncHttpResponseHandler(){
                    override fun onSuccess(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseBody: ByteArray?
                    ) {
                        var body= String(responseBody!!)
                        var m3u8url="http://"+Regex("(tv\\.byr\\.cn/liverespath/.*?/index\\.m3u8)").find(body)!!.groups[0]!!.value
                        if((application as OpenBYRTVApplication).useVpn)
                            m3u8url=m3u8url.replace("tv.byr.cn","webvpn.bupt.edu.cn/http/77726476706e69737468656265737421e4e10f9e3e22265370")

                        startActivity(intentFor<PlayerActivity>("url" to m3u8url,"title" to channelList[position].name,"detail" to channelList[position].desc).newTask())
                        //startActivity(intentFor<ChannelActivity>("url" to responseBody).newTask().clearTask()) // Pop self & Push new ChannelActivity
                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseBody: ByteArray?,
                        error: Throwable?
                    ) {

                        Toast.makeText(instance,"Access Denied! IPV6 may still unsuported in your current network!",Toast.LENGTH_SHORT).show()

                    }

                })

            }


        })
        recycleView.adapter=adapter
        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private fun isScrollToEnd(recycleview: RecyclerView?): Boolean {
                if (recycleview == null) return false
                return if (recycleview.computeVerticalScrollExtent() + recycleview.computeVerticalScrollOffset() >= recycleview.computeVerticalScrollRange()) true else false
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                 if (isScrollToEnd(recycleView)) {
                    Toast.makeText(instance,"到底儿啦！",Toast.LENGTH_SHORT)
                }

            }
        })

        for(elem in elems){
            var name=elem.getElementsByClass("card-title")[0].text()
            var imgUrl=elem.getElementsByClass("card-img")[0].getElementsByTag("img")[0].attr("src")
            var nowPlaying=elem.getElementsByClass("card-info")[0].children().text()
            var url=elem.getElementsByClass("card-img")[0].getElementsByTag("a")[0].attr("href")
            channelList.add(Channel(name = name,imageId = (if((application as OpenBYRTVApplication).useVpn)"http://webvpn.bupt.edu.cn" else "http://tv.byr.cn")+imgUrl,
                                                desc = nowPlaying,vid=(if((application as OpenBYRTVApplication).useVpn)"http://webvpn.bupt.edu.cn" else "http://tv.byr.cn")+url))

            //println(name)
        }

        adapter.notifyDataSetChanged()


    }

    override fun onResume() {
        super.onResume()
    }


}
