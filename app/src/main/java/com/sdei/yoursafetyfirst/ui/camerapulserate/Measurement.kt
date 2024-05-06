package com.app.yoursafetyfirst.ui.camerapulserate

import java.io.Serializable
import java.util.Date

class Measurement<T>(val timestamp: Date, val measurement: T):Serializable