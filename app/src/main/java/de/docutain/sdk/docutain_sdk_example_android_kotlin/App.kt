package de.docutain.sdk.docutain_sdk_example_android_kotlin

import android.app.Application
import android.util.Log
import de.docutain.sdk.DocutainSDK
import de.docutain.sdk.Logger
import de.docutain.sdk.dataextraction.AnalyzeConfiguration
import de.docutain.sdk.dataextraction.DocumentDataReader

class App : Application() {

    private val logTag = "DocutainSDK"
    private val licenseKey = "YOUR_LICENSE_KEY_HERE"
    companion object{
        var licenseKeyMissing = false
    }

    override fun onCreate() {
        super.onCreate()

        //the Docutain SDK needs to be initialized prior to using any functionality of it
        //a valid license key is required (contact us via [mailto:sdk@Docutain.com] to get a trial license)
        if(!DocutainSDK.initSDK(this, licenseKey)){
            //init of Docutain SDK failed, get the last error message
            Log.e(logTag,"Initialization of the Docutain SDK failed: ${DocutainSDK.getLastError()}")
            //your logic to deactivate access to SDK functionality
            if(licenseKey == "YOUR_LICENSE_KEY_HERE"){
                licenseKeyMissing = true
            }
        }

        //If you want to use text detection (OCR) and/or data extraction features, you need to set the AnalyzeConfiguration
        //in order to start all the necessary processes
        val analyzeConfig = AnalyzeConfiguration()
        analyzeConfig.readBIC = true
        analyzeConfig.readPaymentState = true
        if(!DocumentDataReader.setAnalyzeConfiguration(analyzeConfig)){
            Log.e(logTag,"Setting AnalyzeConfiguration failed: ${DocutainSDK.getLastError()}")
        }

        //Depending on your needs, you can set the Logger's level
        Logger.setLogLevel(Logger.Level.VERBOSE)

        //Depending on the log level that you have set, some temporary files get written on the filesystem
        //You can delete all temporary files by using the following method
        DocutainSDK.deleteTempFiles(true)
    }
}