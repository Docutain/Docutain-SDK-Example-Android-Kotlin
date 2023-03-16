package de.docutain.sdk.docutain_sdk_example_android_kotlin

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.docutain.sdk.Document
import de.docutain.sdk.DocutainSDK
import de.docutain.sdk.dataextraction.DocumentDataReader
import de.docutain.sdk.ui.DocumentScannerConfiguration
import de.docutain.sdk.ui.ScanResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {

    private val logTag = "DocutainSDK"

    //declare an ActivityResultLauncher which starts the document scanner and
    //returns once the user finished scanning or canceled the scan process
    //see [https://docs.docutain.com/docs/Android/docScan] for more information
    private val documentScanResult = registerForActivityResult(ScanResult()) { result ->
        if(!result){
            Log.i(logTag,"canceled scan process")
            return@registerForActivityResult
        }

        //proceed depending on the previously selected option
        when(selectedOption){
            ListAdapter.ItemType.PDF_GENERATING ->  generatePDF(null)
            ListAdapter.ItemType.DATA_EXTRACTION -> openDataResultActivity(null)
            ListAdapter.ItemType.TEXT_RECOGNITION ->  openTextResultActivity(null)
            else -> Log.i(logTag,"Select an input option first")
        }
    }

    //declare an image picker activity launcher in single-select mode which is used to generate a pdf from the selected image
    private val pickImageForPDFGenerating = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            generatePDF(uri)
        } else {
            Log.i(logTag,"canceled image import")
        }
    }

    //declare an image picker activity launcher in single-select mode which is used to extract data from the selected image
    private val pickImageForDataExtraction = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
           openDataResultActivity(uri)
        } else {
            Log.i(logTag,"canceled image import")
        }
    }

    //declare an image picker activity launcher in single-select mode which is used to recognize text from the selected image
    private val pickImageForTextRecognition = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            openTextResultActivity(uri)
        } else {
            Log.i(logTag,"canceled image import")
        }
    }

    //declare a PDF picker activity launcher which is used to recognize text from the selected PDF
    private val pickPDFForTextRecognition = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            openTextResultActivity(uri)
        } else {
            Log.i(logTag,"canceled PDF import")
        }
    }

    //declare a PDF picker activity launcher which is used to extract data from the selected PDF
    private val pickPDFForDataExtraction = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            openDataResultActivity(uri)
        } else {
            Log.i(logTag, "canceled PDF import")
        }
    }

    private var selectedOption = ListAdapter.ItemType.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(App.licenseKeyMissing){
            //the Docutain SDK needs to be initialized prior to using any functionality of it
            //a valid license key is required (contact us via [mailto:sdk@Docutain.com] to get a trial license)
            showLicenseEmptyInfo()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ListAdapter {
            when (it.type) {
                ListAdapter.ItemType.DOCUMENT_SCAN -> {
                    selectedOption = ListAdapter.ItemType.NONE
                    startScan()
                }
                ListAdapter.ItemType.DATA_EXTRACTION -> {
                    selectedOption = ListAdapter.ItemType.DATA_EXTRACTION
                    startDataExtraction()
                }
                ListAdapter.ItemType.TEXT_RECOGNITION -> {
                    selectedOption = ListAdapter.ItemType.TEXT_RECOGNITION
                    startTextRecognition()
                }
                ListAdapter.ItemType.PDF_GENERATING -> {
                    selectedOption = ListAdapter.ItemType.PDF_GENERATING
                    startPDFGenerating()
                }
                else -> {
                    selectedOption = ListAdapter.ItemType.NONE
                    Log.i(logTag,"invalid item clicked")
                }
            }
        }
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun startScan(){
        //define a DocumentScannerConfiguration to alter the scan process and define a custom theme to match your branding
        val scanConfig = DocumentScannerConfiguration()
        scanConfig.allowCaptureModeSetting = true //defaults to false
        scanConfig.pageEditConfig.allowPageFilter = true //defaults to true
        scanConfig.pageEditConfig.allowPageRotation = true //defaults to true
        //alter the onboarding image source if you like
        //scanConfig.onboardingImageSource = ...

        //detailed information about theming possibilities can be found here [https://docs.docutain.com/docs/Android/theming]
        scanConfig.theme = R.style.Theme_DocutainSDK
        documentScanResult.launch(scanConfig)
    }

    private fun startPDFImport(){
        when(selectedOption){
            ListAdapter.ItemType.PDF_GENERATING ->  Log.i(logTag,"Generating a PDF from a file which is already a PDF makes no sense, please scan a document or import an image.")
            ListAdapter.ItemType.DATA_EXTRACTION -> pickPDFForDataExtraction.launch("application/pdf")
            ListAdapter.ItemType.TEXT_RECOGNITION ->  pickPDFForTextRecognition.launch("application/pdf")
            else -> Log.i(logTag,"Select an input option first")
        }
    }

    private fun startImageImport(){
        when(selectedOption){
            ListAdapter.ItemType.PDF_GENERATING ->  pickImageForPDFGenerating.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            ListAdapter.ItemType.DATA_EXTRACTION ->  pickImageForDataExtraction.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            ListAdapter.ItemType.TEXT_RECOGNITION ->  pickImageForTextRecognition.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            else -> Log.i(logTag,"Select an input option first")
        }
    }

    private fun startDataExtraction(){
        showInputOptionAlert()
    }

    private fun startTextRecognition(){
        showInputOptionAlert()
    }

    private fun startPDFGenerating(){
        showInputOptionAlert()
    }

    private fun showInputOptionAlert(){
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(R.string.title_input_option)
            .setItems(
                arrayOf(getString(R.string.input_option_scan), getString(R.string.input_option_PDF), getString(R.string.input_option_image)),
                DialogInterface.OnClickListener { _, which ->
                    when(which){
                        0 -> startScan()
                        1 -> startPDFImport()
                        2 -> startImageImport()
                    }
                })
        builder.create().show()
    }

    private fun generatePDF(uri: Uri?) = CoroutineScope(Dispatchers.IO).launch{
        if(uri != null){
            //if an uri is available it means we have imported a file. If so, we need to load it into the SDK first
            if(!DocumentDataReader.loadFile(uri)){
                //an error occured, get the latest error message
                Log.i(logTag,"DocumentDataReader.loadFile failed, last error: ${DocutainSDK.getLastError()}")
                return@launch
            }
        }
        //define the output file for the PDF
        val file = File(filesDir, "SamplePDF")
        //generate the PDF from the currently loaded document
        //the generated PDF also contains the detected text, making the PDF searchable
        //see [https://docs.docutain.com/docs/Android/pdfCreation] for more details
        val fileReturn = Document.writePDF(file, true, Document.PDFPageFormat.A4)
        if(fileReturn == null){
            //an error occured, get the latest error message
            Log.i(logTag,"DocumentDataReader.loadFile failed, last error: ${DocutainSDK.getLastError()}")
            return@launch
        }

        //display the PDF by using the system's default viewer for demonstration purposes
        val pdfUri = FileProvider.getUriForFile(this@MainActivity,  "de.docutain.sdk.docutain_sdk_example_android_kotlin.attachments", fileReturn)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.data = pdfUri
        try{
            startActivity(intent)
        } catch(ex: ActivityNotFoundException){
            Log.i(logTag,"No Activity available for displaying the PDF")
        }
    }

    private fun openDataResultActivity(uri: Uri?){
        val intent = Intent(this@MainActivity, DataResultActivity::class.java)
        if(uri != null){
            intent.putExtra("uri", uri)
        }
        startActivity(intent)
    }

    private fun openTextResultActivity(uri: Uri?){
        val intent = Intent(this@MainActivity, TextResultActivity::class.java)
        if(uri != null){
            intent.putExtra("uri", uri)
        }
        startActivity(intent)
    }

    private fun showLicenseEmptyInfo(){
        MaterialAlertDialogBuilder(this).setTitle("License empty")
            .setMessage("A valid license key is required. Please contact us via sdk@Docutain.com to get a trial license.")
            .setNegativeButton("Cancel") { _, _ ->

            }.setPositiveButton("Get License") {_, _ ->
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:") // only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("sdk@Docutain.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Trial License Request")
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else{
                    Log.e(logTag,"No Mail App available, please contact us manually via sdk@Docutain.com")
                }
            }.show()
    }
}