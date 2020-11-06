package com.buptsdmda.openbyrtv

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import java.util.*



class DownloadActivity : AppCompatActivity() {
    val instance by lazy { this }
    var url: String? =null;
    var title:String?=null;

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )
    fun verifyStoragePermissions(activity: Activity?) {
        try {
            //检测是否有写的权限
            val permission = ActivityCompat.checkSelfPermission(
                activity!!,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            instance.releaseInstance()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        url = intent.getStringExtra("url")
        title = intent.getStringExtra("title")
        findViewById<TextView>(R.id.ChannelName).setText(title)

        val timePicker=findViewById<TimePicker>(R.id.time_picker)
        timePicker.setIs24HourView(true)
        timePicker.hour=0
        timePicker.minute=0
        val button=findViewById<Button>(R.id.button)
        button.setOnClickListener{
//            val ffmpeg = FFmpeg.getInstance(this)
//
////             to execute "ffmpeg -version" command you just need to pass "-version"
////             to execute "ffmpeg -version" command you just need to pass "-version"
//            val cmds=String.format("ffmpeg -i %s -vcodec copy -acodec copy -absf aac_adtstoasc %s.mp4",url,title+Date().time.toString()).split(" ").toTypedArray()
//            ffmpeg.execute(cmds, object : ExecuteBinaryResponseHandler() {
//                override fun onStart() {
//                    Toast.makeText(instance,"下载任务开始！",Toast.LENGTH_SHORT).show()
//                }
//                override fun onProgress(message: String) {}
//                override fun onFailure(message: String) {}
//                override fun onSuccess(message: String) {
//                    Toast.makeText(instance,"下载任务成功结束！",Toast.LENGTH_SHORT).show()
//                }
//                override fun onFinish() {
//
//                }
//
//                })
            val cmd=String.format("-i %s -vcodec copy -acodec copy -absf aac_adtstoasc -s 1280x720 %s.mp4",url,instance.getFilesDir().getPath()+"/"+title+ Date().time.toString())
            val thread=Thread() {
                //Toast.makeText(instance,"下载开始",Toast.LENGTH_SHORT).show()
                val executionId =
                    FFmpeg.executeAsync(
                        cmd
                    ) { executionId, returnCode ->
                        if (returnCode == RETURN_CODE_SUCCESS) {
                            Log.i(Config.TAG, "Async command execution completed successfully.")
                        } else if (returnCode == RETURN_CODE_CANCEL) {
                            Log.i(Config.TAG, "Async command execution cancelled by user.")
                        } else {
                            Log.i(
                                Config.TAG,
                                String.format(
                                    "Async command execution failed with returnCode=%d.",
                                    returnCode
                                )
                            )
                        }
                    }
                Thread.sleep(((timePicker.minute*60+timePicker.hour*60*60)*1000).toLong())
                FFmpeg.cancel(executionId)
                //Toast.makeText(instance,"下载完成",Toast.LENGTH_SHORT).show()

            }
            thread.start()
            Toast.makeText(instance,"下载开始",Toast.LENGTH_SHORT).show()
            instance.finish()






        }



        verifyStoragePermissions(this)
//        if (!FFmpeg.getInstance(this).isSupported()) {
//            Toast.makeText(instance,"ERROR: FFmpeg is unsupported on this machine!",Toast.LENGTH_SHORT).show()
//            instance.releaseInstance()
//            // ffmpeg is supported
//        }
    }
}
