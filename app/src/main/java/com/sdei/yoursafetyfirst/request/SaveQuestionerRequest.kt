package com.app.yoursafetyfirst.request

data class SaveQuestionerRequest(
    val declarationId: String? = "",
    val count: String? = "",
    val response: ArrayList<Response>
)


data class Response(
    val questionName: String? = "",
    val ansType: String? = "",
    val options: ArrayList<Options>?
)

data class Options(
    val name: String? = "",
    val scoreVal: String? = "",
    val selected: Boolean
)



