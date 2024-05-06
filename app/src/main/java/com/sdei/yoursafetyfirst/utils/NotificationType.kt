package com.app.yoursafetyfirst.utils

sealed class NotificationType() {

    data object NoticeSDNC : NotificationType() {

        fun getValue(): String {
            return "Notice from SDNC"
        }
    }

    data object NoticeApplication : NotificationType(){
        fun getValue(): String {
            return "Notice regarding the application"
        }
    }

    data object NoticeTransportation : NotificationType() {
        fun getValue(): String {
            return "Transportation related information"
        }
    }

}
