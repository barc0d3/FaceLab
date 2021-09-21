package com.FaceLab.FaceLab.ui

import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.FaceLab.FaceLab.R
import com.FaceLab.FaceLab.model.TestModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.io.IOException

class TestActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private var byteArray: ByteArray? = null
    private var bmp: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        MobileAds.initialize(this) { }
        val ivFace = findViewById<ImageView>(R.id.test_iv_face)
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // 광고가 제대로 로드 되는지 테스트 하기 위한 코드입니다.
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                // 광고가 문제 없이 로드시 출력됩니다.
                Log.d("@@@", "onAdLoaded")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

        imageUri = Uri.parse(intent.extras!!.getString("imageUri"))
        try {
            bmp = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val scale = (1024 / bmp!!.width.toFloat())
        val imageW = (bmp!!.width * scale).toInt()
        val imageH = (bmp!!.height * scale).toInt()
        bmp = Bitmap.createScaledBitmap(bmp!!, imageW, imageH, true)

        ivFace.setImageBitmap(getRoundedCornerBitmap(bmp))
    }

    fun customOnClick(v: View) {
        if (v.id == R.id.test_cl_analysis) {
            val model = TestModel(this, bmp!!)
            model.runModel()
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("imageUri", imageUri.toString())
            intent.putExtra("image", byteArray)
            intent.putExtra("output", model.output)
            intent.putExtra("sexAge", model.ageSex)
            intent.putExtra("emotion", model.emotion)
            intent.putExtra("result", model.result)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        //모서리가 둥근 이미지를 보여주는 함수
        fun getRoundedCornerBitmap(bitmap: Bitmap?): Bitmap {
            val output = Bitmap.createBitmap(
                bitmap!!.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = 40f
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }
}