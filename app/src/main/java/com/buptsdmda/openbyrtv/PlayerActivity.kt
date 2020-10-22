package com.buptsdmda.openbyrtv


import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.universalvideoview.UniversalMediaController
import com.universalvideoview.UniversalVideoView
import com.universalvideoview.UniversalVideoView.VideoViewCallback


class PlayerActivity : AppCompatActivity() {
    val instance by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        //var mWebview=findViewById<WebView>(R.id.webView)
        //
        //mWebview.loadUrl("file:///android_asset/player.html")
        var videoView=findViewById<VideoView>(R.id.videoView)
        var url = intent.getStringExtra("url")
        videoView.setVideoPath(url)
    }
}
