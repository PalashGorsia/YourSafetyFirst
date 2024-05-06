package com.app.yoursafetyfirst.response

class PrivacyPolicyResponse : ArrayList<PrivacyPolicyResponseItem>()


data class PrivacyPolicyResponseItem(
    val description: Description,
    val title: Title
)

