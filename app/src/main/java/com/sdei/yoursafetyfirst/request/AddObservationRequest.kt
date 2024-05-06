package com.app.yoursafetyfirst.request

class AddObservationRequest(
    val declarationId: String? = "",
    val deviceType: String? = "",
    val pulse: String? = "",
    val QOL: String? = "",
    val steps: String? = "",
    val ringToken: String? = "",
    val pulseData:ArrayList<String>,
    val pulseTimestampData:ArrayList<String>
)


