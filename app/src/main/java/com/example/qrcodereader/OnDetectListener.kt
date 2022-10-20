package com.example.qrcodereader

interface OnDetectListener {
    fun onDetect(msg : String) // QRCodeAnalyzer에서 QR코드가 인식됐을 때 호출할 함수
}