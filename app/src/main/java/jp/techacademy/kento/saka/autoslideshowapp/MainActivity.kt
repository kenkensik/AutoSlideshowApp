package jp.techacademy.kento.saka.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity() ,View.OnClickListener{

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    var count:Int=1
    var count2:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)

            }
            // Android 5系以下の場合
        } else {

            getContentsInfo()
        }


        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when(v.id){
            R.id.button1 ->next()
            R.id.button2 ->back()
            R.id.button3 ->start()

        }
    }

    private fun start() {



        if (count2 % 2 == 0&&mTimer == null) {
            mTimer = Timer()

            button1.setEnabled(false)
            button2.setEnabled(false)
            button3.text = "停止"

            mTimer!!.schedule(object : TimerTask() {
                override fun run() {

                    mHandler.post {
                        next()
                    }
                }
            }, 2000, 2000)

        }else{
            if (mTimer != null){
                mTimer!!.cancel()
                mTimer = null
            }
            button1.setEnabled(true)
            button2.setEnabled(true)
            button3.text = "再生"

        }
        count2++
    }

     private fun next(){

        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        )

        cursor.moveToFirst()
        if(count==1){
            cursor.moveToNext()
            count++
        }else if(count==2){
            cursor.moveToNext()
            cursor.moveToNext()
            count++
        }else{count=1}

            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)

        cursor.close()

    }

    private fun back(){
        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        )

        cursor.moveToFirst()
        if(count==1){
            cursor.moveToNext()
            cursor.moveToNext()
            count=3
        }else if(count==2){
            count--
        }else{cursor.moveToNext()
            count--
        }

        // indexからIDを取得し、そのIDから画像のURIを取得する
        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor.getLong(fieldIndex)
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        imageView.setImageURI(imageUri)

        cursor.close()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        )

        if (cursor.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する

                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)

        }
        cursor.close()
    }


}
