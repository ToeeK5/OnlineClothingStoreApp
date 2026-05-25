package com.example.onlineclothingstoreapp.profile

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.R

class SettingsFragment : Fragment() {

    private lateinit var btnBack: TextView
    private lateinit var seekVolume: SeekBar
    private lateinit var layoutSupport: LinearLayout
    private lateinit var layoutAbout: LinearLayout
    private lateinit var txtSupportContent: TextView
    private lateinit var txtAboutContent: TextView
    private lateinit var btnLogout: Button

    private lateinit var audioManager: AudioManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        AnhXa(view)
        CaiDatAmLuong()
        SuKien()

        return view
    }

    private fun AnhXa(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        seekVolume = view.findViewById(R.id.seekVolume)
        layoutSupport = view.findViewById(R.id.layoutSupport)
        layoutAbout = view.findViewById(R.id.layoutAbout)
        txtSupportContent = view.findViewById(R.id.txtSupportContent)
        txtAboutContent = view.findViewById(R.id.txtAboutContent)
        btnLogout = view.findViewById(R.id.btnLogout)

        audioManager =
            requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
            ChuyenManHinh.QuayLai(requireActivity())
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
            // Tạm thời để trống, lát nối về Login sau
        }
    }

    private fun AnHienNoiDung(textView: TextView) {
        if (textView.visibility == View.GONE) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun MoTinNhan(soDienThoai: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:$soDienThoai")
        intent.putExtra("sms_body", "Xin chào, tôi cần hỗ trợ về ứng dụng LUMIÈRE.")
        startActivity(intent)
    }
}