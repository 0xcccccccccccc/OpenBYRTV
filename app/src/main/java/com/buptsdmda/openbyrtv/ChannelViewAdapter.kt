package com.buptsdmda.openbyrtv


import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.image


class ChannelViewAdapter:RecyclerView.Adapter<ChannelViewAdapter.ViewHolder>{
    private var mChannelList: List<Channel>? = null
    private var clickListener: ItemClickCallback


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var channelImage: ImageView
        var channelName: TextView
        var channelDesc: TextView


        init {
            channelImage =
                view.findViewById<View>(R.id.channel_image) as ImageView
            channelName = view.findViewById<View>(R.id.channel_name) as TextView
            channelDesc = view.findViewById<View>(R.id.channel_desc) as TextView
        }

    }



    constructor(channelList: List<Channel>?,onClickListener: ItemClickCallback) {
        mChannelList = channelList
        clickListener=onClickListener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.channel_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel: Channel = mChannelList!![position]

        //Ion.with(holder.fruitImage).load(fruit.imageId)
        //holder.fruitImage.setImageResource(fruit.imageId)

        //holder.fruitImage.imageBitmap=getBitmapFromURL(fruit.imageId)
        if(holder.channelImage.image==null)
            loadImage(holder.channelImage,channel.imageId)
        holder.channelName.setText(channel.name)
        holder.channelDesc.setText(channel.desc)
        holder.itemView.setOnClickListener(
            object :View.OnClickListener{
                override fun onClick(v: View?) {
                    clickListener.onClick(position)
                }

            }
        )


    }
    fun loadImage(imageViewHandler:ImageView,url:String){
        var asyncHttpClient=AsyncHttpClient();
        asyncHttpClient.addHeader("Content-Type","text/html; charset=UTF-8")
        asyncHttpClient.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs");
        asyncHttpClient.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        asyncHttpClient.addHeader("Accept-Language","zh-cn,zh;q=0.5")
        asyncHttpClient.addHeader("Host","tv.byr.cn");


        asyncHttpClient.get(url,object:AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                imageViewHandler.setImageBitmap( BitmapFactory.decodeByteArray(responseBody,0,responseBody!!.size))
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                println(String(responseBody!!))
            }

        })
    }
    override fun getItemCount(): Int {
        return mChannelList!!.size
    }
}