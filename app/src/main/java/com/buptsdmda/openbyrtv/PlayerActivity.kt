package com.buptsdmda.openbyrtv

import android.app.Activity
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import org.jetbrains.anko.audioManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.File


class PlayerActivity : AppCompatActivity(),CacheListener {
    val instance by lazy { this }
    private var player:SimpleExoPlayer?=null
    private var fullscreen = false
    enum class TOUCH_STATUS{
        INIT,
        ACTIVATED

    }
    private var x=0
    private var y=0
    private var originalVolume=0.0
    private var originalBrightness=0.0
    private var touchStatus=TOUCH_STATUS.INIT;
    private var controllerActivated=false;
    private fun setLight(context: Activity, brightness: Int) {
        val lp: WindowManager.LayoutParams = context.getWindow().getAttributes()
        lp.screenBrightness = java.lang.Float.valueOf(brightness.toFloat()) * (1f / 255f)
        context.getWindow().setAttributes(lp)
    }
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
        }else if(url!!.startsWith("file://")){
                val mediaSource = ExtractorMediaSource(Uri.parse(url),dataSourceFactory,DefaultExtractorsFactory(),null,null)
                player?.prepare(mediaSource)
                player?.playWhenReady=true
        }else{
            instance.finish()
        }

//        videoView.setOnDragListener(object :View.OnDragListener{
//            override fun onDrag(v: View?, event: DragEvent?): Boolean {
//                when(event!!.action){
//                    DragEvent.
//                }
//            }
//
//        })
        videoView.hideController()
        videoView.controllerAutoShow=false
        videoView.controllerHideOnTouch=false



        videoView.setOnTouchListener(object:View.OnTouchListener{

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val action = event!!.action
                when (action) {

                    MotionEvent.ACTION_MOVE-> {
                        val upX = event.rawX.toInt()
                        val upY = event.rawY.toInt()
                        val disX = upX - x
                        val disY = upY - y
                        if (Math.abs(disX) < Math.abs(disY) && Math.abs(
                                disY
                            ) > 20
                        ) {


                            if (touchStatus == TOUCH_STATUS.INIT) {
                                touchStatus = TOUCH_STATUS.ACTIVATED
                                originalVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toDouble()
                                originalBrightness=Math.abs(window.attributes.screenBrightness.toDouble())

                            }

                            val tuneRatio=Math.min(1.5,Math.max(0.5,1.0+(-disY.toFloat() / videoView.measuredHeight.toFloat())))

                            if(x<videoView.measuredWidth.toFloat()/2){//volume

                                val maxVol=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                val minVol= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) else 0

                                val volRange=maxVol-minVol

                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,Math.min(maxVol,Math.max(minVol,(originalVolume+volRange*(tuneRatio-1.0)).toInt())),AudioManager.FLAG_SHOW_UI)
                            }
                            else{//brightness

                                //window.attributes.screenBrightness=(255.0*(tuneRatio-0.5)).toFloat()
                                setLight(instance,(255.0*(tuneRatio-0.5)).toInt())


                            }
                            Log.d(
                                    "Ratio",
                                    tuneRatio.toString()

                                )
                            Log.d(
                                "Brightness",
                                window.attributes.screenBrightness.toString()

                            )

//                            if (!fullscreen) {
//                                Log.d(
//                                    "Brightness",
//                                    (-disY.toFloat() / videoView.measuredHeight.toFloat()).toString()
//                                )
//
//
//
//                            } else {
//                                Log.d(
//                                    "Brightness",
//                                    (-disY.toFloat() / videoView.measuredHeight.toFloat()).toString()
//
//                                )
//
//                            }
                            return true


//                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+disX,AudioManager.FLAG_SHOW_UI)

                                //window.attributes.screenBrightness+=(disY.toFloat()/videoView.layoutParams.height.toFloat())
                        }
                        return false
                    }
                    MotionEvent.ACTION_DOWN -> {
                        x = event.rawX.toInt()
                        y = event.rawY.toInt()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        x = 0
                        y = 0
                        if(touchStatus==TOUCH_STATUS.INIT){//Click
                            if(controllerActivated)
                            {videoView.hideController()}
                            else {videoView.showController()}
                            controllerActivated=!controllerActivated
                            return false
                        }else if(touchStatus==TOUCH_STATUS.ACTIVATED){
                            touchStatus=TOUCH_STATUS.INIT
                            return true
                        }

                    }
                }
                return false
            }


        })

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
            fullscreen = true
        } else {// back to normal

            val params =
                playerView.getLayoutParams() as LinearLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = (252*dm.density).toInt()
            playerView.setLayoutParams(params)
            findViewById<LinearLayout>(R.id.TextArea).visibility=LinearLayout.VISIBLE
            fullscreen = false
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
