package com.example.qrcodereader

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


class QRCodeAnalyzer(val onDetectListener: OnDetectListener) : ImageAnalysis.Analyzer{

    // 바코드 스캐닝 객체 생성
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if(mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees) // 회전 각도를 고려한 입력 이미지 생성

            scanner.process(image).addOnSuccessListener { qrCdoes ->
                for(qrCode in qrCdoes){
                    onDetectListener.onDetect(qrCode.rawValue ?: "")
                }
            }
                .addOnFailureListener{
                    it.printStackTrace() // 실패시 에러 로그 프린트
                }
                .addOnCompleteListener{
                    imageProxy.close() // 이미지 분석 완료 시 이미지 프록시 닫는 작업
                }
        }
    }
}