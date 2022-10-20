package com.example.qrcodereader

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.qrcodereader.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var isDetected = false // onDetect() 함수가 여러 번 실행되는 경우를 막는 변수
    private lateinit var binding : ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val PERMISSIONS_REQUEST_CODE = 1
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA) // 카메라 권한 지정




   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 설정
        binding = ActivityMainBinding.inflate(layoutInflater) // 바인딩 변수에 ActivityMainBinding 클래스에 있는 인플레이트 함수 객체 선언, 레이아웃 사용 가능
        val view = binding.root  // 뷰 객체에 ActivityMainBinding 클래스에 있는 root 대입, 항상 자동 생성되는 루트 뷰 반환
        setContentView(view) // root 화면 연결

       if(!hasPermissions(this)){ // 카메라 권한 요청
           requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
       }
       else{
           // 만약 이미 권한이 있다면 카메라 시작
           startCamera()
       }
    }

    override fun onResume() {
        super.onResume()
        isDetected = false
    }

    fun getImageAnalysis(): ImageAnalysis{

        val cameraExecutor : ExecutorService = Executors.newSingleThreadExecutor()
        val imageAnalysis = ImageAnalysis.Builder().build()

        //Analyzer 설정
        imageAnalysis.setAnalyzer(cameraExecutor, QRCodeAnalyzer(object : OnDetectListener{
            override fun onDetect(msg: String) {
                if(!isDetected){ // QR코드가 인식된 적 없는지 검사해서 중복 실행을 막는다.
                    isDetected = true // 데이터가 감지되면 true로 바꾸어준다.

                    val intent = Intent(this@MainActivity,
                    ResultActivity::class.java)
                    intent.putExtra("msg", msg)
                    startActivity(intent)

                }
            }
        }))
        return imageAnalysis
    }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // requestPermissions의 인수로 넣은 PERMISSIONS_REQUEST_CODE와 맞는지 확인
        if(requestCode == PERMISSIONS_REQUEST_CODE){
            if(PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()){
                Toast.makeText(this@MainActivity, "권한 요청이 승인되었습", Toast.LENGTH_SHORT).show()
                startCamera()
            }
            else{
                Toast.makeText(this@MainActivity, "권한 요청이 거부되었습", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 미리보기와 이미지 분석 시작
    fun startCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable { 
            val cameraProvider = cameraProviderFuture.get() 
        
        val preview = getPreview() // 미리보기 객체 가져오기
        val imageAnalysis = getImageAnalysis()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 후면 카메라 선택    
        
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis) // 미리보기 기능 선택

        }, ContextCompat.getMainExecutor(this))
    }

    // 미리보기 객체 반환
    fun getPreview() : Preview {

        val preview : Preview = Preview.Builder().build() // Preview 객체 생성
        preview.setSurfaceProvider(binding.barcodePreview.getSurfaceProvider())

        return preview
    }
}