package com.lmjssjj.autotextswitcher

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        var ats = findViewById(R.id.ats) as AutoTextSwitcher
        var datas = arrayListOf<CharSequence>(
            "1111111111111111111",
            "2222222222222222222",
            "333333333333333333333333"
        )
        ats.setTextList(datas)
        ats.setOnItemClickListener() {
            Toast.makeText(
                this@MainActivity,
                "点击了 : " + datas.get(it),
                Toast.LENGTH_SHORT
            ).show()
        }
        ats.start()
    }
}