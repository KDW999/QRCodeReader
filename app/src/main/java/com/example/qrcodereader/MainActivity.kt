package com.example.qrcodereader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import com.example.qrcodereader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 설정
        binding = ActivityMainBinding.inflate(layoutInflater) // 바인딩 변수에 ActivityMainBinding 클래스에 있는 인플레이트 함수 객체 선언, 레이아웃 사용 가능
        val view = binding.root  // 뷰 객체에 ActivityMainBinding 클래스에 있는 root 대입, 항상 자동 생성되는 루트 뷰 반환
        setContentView(view) // root 화면 연결
    }

    // 미리보기와 이미지 분석 시작
    fun startCamera(){

    }

    // 미리보기 객체 반환
    fun getPreview() : Preview {

        val preview : Preview = Preview.Builder().build() // Preview 객체 생성
        preview.setSurfaceProvider(binding.barcodePreview.getSurfaceProvider())

        return preview
    }
}