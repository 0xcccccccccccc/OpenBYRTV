package com.buptsdmda.openbyrtv

import android.net.Uri
import android.os.Bundle
import android.os.Handler

import androidx.appcompat.app.AppCompatActivity

import com.google.android.exoplayer2.*

import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

import com.google.android.exoplayer2.ui.SimpleExoPlayerView

import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory




class PlayerActivity : AppCompatActivity() {
    val instance by lazy { this }
    private var player:SimpleExoPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        var videoView=findViewById<SimpleExoPlayerView>(R.id.playerView)


        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(instance), DefaultTrackSelector(), DefaultLoadControl())

        var url = intent.getStringExtra("url")

        videoView.player=player

        val uri = Uri.parse(url)
        val dataSourceFactory = DefaultDataSourceFactory(instance, "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs")
        val mediaSource = HlsMediaSource(uri,dataSourceFactory,null,null)
        player?.prepare(mediaSource)
        player?.playWhenReady = true

    }
}
