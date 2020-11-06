package com.buptsdmda.openbyrtv


//import com.psaravan.filebrowserview.lib.View.FileBrowserView

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_choose_cache.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.File
import java.io.FilenameFilter


class ChooseCacheActivity : AppCompatActivity() {
    val instance by lazy { this }
    var fileList:MutableList<String>?=null
    var adapter:FileViewAdapter?=null
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
        setContentView(R.layout.activity_choose_cache)
        verifyStoragePermissions(instance)
        setSupportActionBar(toolbar)
        val path=instance.getFilesDir().getPath()
        fileList=File(path).list(object:FilenameFilter{
            override fun accept(dir: File?, name: String?): Boolean {
                return name!!.endsWith("mp4")
            }
        }).toMutableList()
//        val fileBrowserView = findViewById(R.id.fileBrowserView) as FileBrowserView
//        fileBrowserView.setFileBrowserLayoutType(FileBrowserView.FILE_BROWSER_LIST_LAYOUT) //Set the type of view to use.
//            .setDefaultDirectory(File(path)) //Set the default directory to show.
//            .setShowHiddenFiles(true) //Set whether or not you want to show hidden files.
//            .showItemSizes(true) //Shows the sizes of each item in the list.
//            .showOverflowMenus(true) //Shows the overflow menus for each item in the list.
//            .init(); //Loads the view. You MUST call this method, or the view will not be displayed.
        val mFileView=findViewById<RecyclerView>(R.id.fileView)
        mFileView.layoutManager=LinearLayoutManager(instance)
        adapter=FileViewAdapter(ArrayList())
        adapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                startActivity(intentFor<PlayerActivity>("url" to "file://"+path+"/"+fileList!!.get(position),"title" to "缓存："+fileList!!.get(position),"detail" to "本地缓存").newTask())
            }
        })
        mFileView.adapter=adapter
        adapter!!.setNewData(fileList)




        fab.setOnClickListener { view ->
            Snackbar.make(view, "正在刷新缓存", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            val path=instance.getFilesDir().getPath()
            fileList=File(path).list(object:FilenameFilter{
                override fun accept(dir: File?, name: String?): Boolean {
                    return name!!.endsWith("mp4")
                }
            }).toMutableList()
            adapter!!.setNewData(fileList)

        }

    }

}
