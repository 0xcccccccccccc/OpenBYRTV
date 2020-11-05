package com.buptsdmda.openbyrtv

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.danikula.videocache.CacheListener
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.File


class PlayerActivity : AppCompatActivity(),CacheListener {
    val instance by lazy { this }
    private var player:SimpleExoPlayer?=null
    var fullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        var videoView=findViewById<SimpleExoPlayerView>(R.id.playerView)


        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(instance), DefaultTrackSelector(), DefaultLoadControl())

        var url = intent.getStringExtra("url")
        var title = intent.getStringExtra("title")
        var detail = intent.getStringExtra("detail")
        findViewById<TextView>(R.id.textTitle).setText(title)
        findViewById<TextView>(R.id.textDetail).setText(detail)
        val dataSourceFactory = DefaultDataSourceFactory(instance, "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs")
        if(url!!.startsWith("http")){

            videoView.player=player


            val mediaSource = HlsMediaSource(Uri.parse(url),dataSourceFactory,null,null)
            player?.prepare(mediaSource)
            player?.playWhenReady = true
        }else if(url!!.startsWith("file")){
                val mediaSource = ExtractorMediaSource(Uri.parse(url),dataSourceFactory,DefaultExtractorsFactory(),null,null)
                player?.prepare(mediaSource)
                player?.playWhenReady=true
        }else{
            instance.finish()
        }


        findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            startActivity(intentFor<DownloadActivity>("url" to url,"title" to title,"detail" to detail).newTask())
        }




    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        var playerView=findViewById<SimpleExoPlayerView>(R.id.playerView)
        val dm=DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        Log.d("PlayerActivity", newConfig.orientation.toString())
        if (newConfig.orientation!=1) {
            val params =
                playerView.getLayoutParams() as LinearLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height =ViewGroup.LayoutParams.MATCH_PARENT
            playerView.setLayoutParams(params)
            findViewById<LinearLayout>(R.id.TextArea).visibility=LinearLayout.INVISIBLE
            //fullscreen = false
        } else {// back to normal

            val params =
                playerView.getLayoutParams() as LinearLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = (252*dm.density).toInt()
            playerView.setLayoutParams(params)
            findViewById<LinearLayout>(R.id.TextArea).visibility=LinearLayout.VISIBLE
            //fullscreen = true
        }
    }

    override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {
        Log.d("PlayerActivity",String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, cacheFile, url))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player!!.release()
        instance.releaseInstance()
    }
}
