package com.example.onlineclothingstoreapp.activities

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.profile.QuanLyDangXuat

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var seekVolume: SeekBar
    private lateinit var layoutSupport: LinearLayout
    private lateinit var layoutAbout: LinearLayout
    private lateinit var txtSupportContent: TextView
    private lateinit var txtAboutContent: TextView
    private lateinit var btnLogout: Button

    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_settings)

        AnhXa()
        CaiDatAmLuong()
        SuKien()
    }

    private fun AnhXa() {
        btnBack = findViewById(R.id.btnBack)
        seekVolume = findViewById(R.id.seekVolume)
        layoutSupport = findViewById(R.id.layoutSupport)
        layoutAbout = findViewById(R.id.layoutAbout)
        txtSupportContent = findViewById(R.id.txtSupportContent)
        txtAboutContent = findViewById(R.id.txtAboutContent)
        btnLogout = findViewById(R.id.btnLogout)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun CaiDatAmLuong() {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        seekVolume.max = maxVolume
        seekVolume.progress = currentVolume

        seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress,
                        AudioManager.FLAG_SHOW_UI
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun SuKien() {
        btnBack.setOnClickListener {
            finish()
        }

        layoutSupport.setOnClickListener {
            AnHienNoiDung(txtSupportContent)
        }

        layoutAbout.setOnClickListener {
            AnHienNoiDung(txtAboutContent)
        }

        txtSupportContent.setOnClickListener {
            MoTinNhan("0909123456")
        }

        btnLogout.setOnClickListener {
            QuanLyDangXuat.DangXuat(this)
        }
    }

    private fun AnHienNoiDung(textView: TextView) {
        textView.visibility =
            if (textView.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun MoTinNhan(soDienThoai: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:$soDienThoai")
        intent.putExtra(
            "sms_body",
            "Xin chào, tôi cần hỗ trợ về ứng dụng LUMIÈRE."
        )
        startActivity(intent)
    }
}