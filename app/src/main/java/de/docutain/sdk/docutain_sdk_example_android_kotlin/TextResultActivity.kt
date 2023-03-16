package de.docutain.sdk.docutain_sdk_example_android_kotlin

import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.docutain.sdk.DocutainSDK
import de.docutain.sdk.dataextraction.DocumentDataReader
import de.docutain.sdk.docutain_sdk_example_android_kotlin.Extensions.parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TextResultActivity : AppCompatActivity() {

    private val logTag = "DocutainSDK"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_result)

        loadText()
    }

    private fun loadText() = CoroutineScope(Dispatchers.IO).launch{
        val uri = intent.parcelable<Uri>("uri")
        if(uri != null) {
            //if an uri is available it means we have imported a file. If so, we need to load it into the SDK first
            if (!DocumentDataReader.loadFile(uri)) {
                //an error occured, get the latest error message
                Log.i(
                    logTag,
                    "DocumentDataReader.loadFile failed, last error: ${DocutainSDK.getLastError()}"
                )
                return@launch
            }
        }

        //get the text of all currently loaded pages
        //if you want text of just one specific page, define the page number
        //see [https://docs.docutain.com/docs/Android/textDetection] for more details
        val text = DocumentDataReader.getText()
        MainScope().launch {
            findViewById<ProgressBar>(R.id.activity_indicator).visibility = View.GONE
            findViewById<TextView>(R.id.textView).text = text
        }
    }
}