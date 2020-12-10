package com.buptsdmda.openbyrtv

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import org.jetbrains.anko.notificationManager


class AudioService : Service() {
    private lateinit var exoPlayer: SimpleExoPlayer

//    val instance by lazy { this }
    /**
     * Will be called by our activity to get information about exo player.
     */
    override fun onBind(intent: Intent?): IBinder {
        exoPlayer.playWhenReady = true//Tell exoplayer to start as soon as it's content is loaded.
        var url = intent!!.getStringExtra("url")
        val httpDataSourceFactory= DefaultHttpDataSourceFactory("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs",null)
        if((application as OpenBYRTVApplication).useVpn){
            val cookieValue = "wengine_vpn_ticket="+(application as OpenBYRTVApplication).vpn_cookie+";Path=/;Domain=webvpn.bupt.edu.cn"
            httpDataSourceFactory.defaultRequestProperties.set("Cookie",cookieValue)

        }
        val mediaSource = HlsMediaSource(Uri.parse(url),httpDataSourceFactory,null,null)
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = true
        return AudioServiceBinder()
    }


    override fun onCreate() {
        super.onCreate()
        val trackSelection = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val trackSelector = DefaultTrackSelector(trackSelection)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "OpenBYRTVAudioService",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("OpenBYRTV广播模式正在运行")
                .setContentText("点击以停止").
                setAutoCancel(true).
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC).
                setSmallIcon(R.mipmap.ic_launcher).
                setContentIntent(PendingIntent.getBroadcast(this, 0, Intent(this,NotificationClickReceiver::class.java), 0)).
                build()
            startForeground(1, notification)
            //notificationManager.notify(2, notification)

        }


    }

    /**
     * This class will be what is returned when an activity binds to this service.
     * The activity will also use this to know what it can get from our service to know
     * about the video playback.
     */
    inner class AudioServiceBinder : Binder() {

        /**
         * This method should be used only for setting the exoplayer instance.
         * If exoplayer's internal are altered or accessed we can not guarantee
         * things will work correctly.
         */
        fun getExoPlayerInstance() = exoPlayer
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        exoPlayer.playWhenReady=false
        exoPlayer.release()
    }
    /**
     * When called will load into exo player our sample playback video.
     */


}
