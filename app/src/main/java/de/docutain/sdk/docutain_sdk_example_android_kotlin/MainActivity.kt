package de.docutain.sdk.docutain_sdk_example_android_kotlin

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import de.docutain.sdk.Logger
import de.docutain.sdk.dataextraction.AnalyzeConfiguration
import de.docutain.sdk.dataextraction.DocumentDataReader
import de.docutain.sdk.docutain_sdk_example_android_kotlin.settings.ColorSettings
import de.docutain.sdk.docutain_sdk_example_android_kotlin.settings.EditSettings
import de.docutain.sdk.docutain_sdk_example_android_kotlin.settings.ScanSettings
import de.docutain.sdk.docutain_sdk_example_android_kotlin.settings.SettingsActivity
import de.docutain.sdk.docutain_sdk_example_android_kotlin.settings.SettingsSharedPreferences
import de.docutain.sdk.ui.DocumentScannerConfiguration
import de.docutain.sdk.ui.DocutainColor
import de.docutain.sdk.ui.ScanResult
import de.docutain.sdk.ui.Source
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
        if (!result) {
            Log.i(logTag, "canceled scan process")
            return@registerForActivityResult
        }

        //proceed depending on the previously selected option
        when (selectedOption) {
            ListAdapter.ItemType.PDF_GENERATING -> generatePDF(null)
            ListAdapter.ItemType.DATA_EXTRACTION -> openDataResultActivity(null)
            ListAdapter.ItemType.TEXT_RECOGNITION -> openTextResultActivity(null)
            else -> Log.i(logTag, "Select an input option first")
        }
    }

    //declare a PDF picker activity launcher for importing PDF documents
    private val pickPDF =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                //proceed depending on the previously selected option
                when (selectedOption) {
                    ListAdapter.ItemType.PDF_GENERATING -> generatePDF(uri)
                    ListAdapter.ItemType.DATA_EXTRACTION -> openDataResultActivity(uri)
                    ListAdapter.ItemType.TEXT_RECOGNITION -> openTextResultActivity(uri)
                    else -> Log.i(logTag, "Select an input option first")
                }
            } else {
                Log.i(logTag, "canceled PDF import")
            }
        }

    private var selectedOption = ListAdapter.ItemType.NONE
    private val settingsSharedPreferences = SettingsSharedPreferences(this)

    //A valid license key is required, you can generate one on our website https://sdk.docutain.com/TrialLicense?Source=655617
    private val licenseKey = "7QeVRaqwTDV623EQDfR96VQf7L6Zzgf9bnkw2wfYzWeMO8nFHK+dUPnkjq++1XzRQpB/Hxej5uzKw9GgvgQ3if/pZn1tnVV0/bME+I6GgWaoO9n8ElfNVxAmv17s5SdUvklgQTzZiYjeI+mJ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //the Docutain SDK needs to be initialized prior to using any functionality of it
        //a valid license key is required, you can generate one on our website https://sdk.docutain.com/TrialLicense?Source=655617
        if(!DocutainSDK.initSDK(application, licenseKey)){
            //init of Docutain SDK failed, get the last error message
            Log.e(logTag,"Initialization of the Docutain SDK failed: ${DocutainSDK.getLastError()}")
            //your logic to deactivate access to SDK functionality
            if(licenseKey == "YOUR_LICENSE_KEY_HERE"){
                showLicenseEmptyInfo()
                return
            }else{
                showLicenseErrorInfo()
                return
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

        if (settingsSharedPreferences.isEmpty())
            settingsSharedPreferences.defaultSettings()

        setContentView(R.layout.activity_main)

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
                ListAdapter.ItemType.SETTINGS -> {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }
                else -> {
                    selectedOption = ListAdapter.ItemType.NONE
                    Log.i(logTag, "invalid item clicked")
                }
            }
        }
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun startScan(imageImport: Boolean = false) {
        //There are a lot of settings to configure the scanner to match your specific needs
        //Check out the documentation to learn more https://docs.docutain.com/docs/Android/docScan#change-default-scan-behaviour
        val scanConfig = DocumentScannerConfiguration()

        if(imageImport){
            scanConfig.source = Source.GALLERY_MULTIPLE
        }

        //In this sample app we provide a settings page which the user can use to alter the scan settings
        //The settings are stored in and read from SharedPreferences
        //This is supposed to be just an example, you do not need to implement it in that exact way
        //If you do not want to provide your users the possibility to alter the settings themselves at all
        //You can just set the settings according to the apps needs

        //set scan settings
        scanConfig.allowCaptureModeSetting =
            settingsSharedPreferences.getScanItem(ScanSettings.AllowCaptureModeSetting).checkValue
        scanConfig.autoCapture =
            settingsSharedPreferences.getScanItem(ScanSettings.AutoCapture).checkValue
        scanConfig.autoCrop =
            settingsSharedPreferences.getScanItem(ScanSettings.AutoCrop).checkValue
        scanConfig.multiPage =
            settingsSharedPreferences.getScanItem(ScanSettings.MultiPage).checkValue
        scanConfig.preCaptureFocus =
            settingsSharedPreferences.getScanItem(ScanSettings.PreCaptureFocus).checkValue
        scanConfig.defaultScanFilter = settingsSharedPreferences.getScanFilterItem(ScanSettings.DefaultScanFilter).scanValue

        //set edit settings
        scanConfig.pageEditConfig.allowPageFilter =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageFilter).editValue
        scanConfig.pageEditConfig.allowPageRotation =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageRotation).editValue
        scanConfig.pageEditConfig.allowPageArrangement =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageArrangement).editValue
        scanConfig.pageEditConfig.allowPageCropping =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageCropping).editValue
        scanConfig.pageEditConfig.pageArrangementShowDeleteButton =
            settingsSharedPreferences.getEditItem(EditSettings.PageArrangementShowDeleteButton).editValue
        scanConfig.pageEditConfig.pageArrangementShowPageNumber =
            settingsSharedPreferences.getEditItem(EditSettings.PageArrangementShowPageNumber).editValue

        //set color settings
        val colorPrimary=settingsSharedPreferences.getColorItem(ColorSettings.ColorPrimary)
        scanConfig.colorConfig.colorPrimary= DocutainColor(colorPrimary.lightCircle,colorPrimary.darkCircle)
        val colorSecondary = settingsSharedPreferences.getColorItem(ColorSettings.ColorSecondary)
        scanConfig.colorConfig.colorSecondary = DocutainColor(colorSecondary.lightCircle, colorSecondary.darkCircle)
        val colorOnSecondary = settingsSharedPreferences.getColorItem(ColorSettings.ColorOnSecondary)
        scanConfig.colorConfig.colorOnSecondary = DocutainColor(colorOnSecondary.lightCircle, colorOnSecondary.darkCircle)
        val colorScanButtonsLayoutBackground = settingsSharedPreferences.getColorItem(ColorSettings.ColorScanButtonsLayoutBackground)
        scanConfig.colorConfig.colorScanButtonsLayoutBackground = DocutainColor(colorScanButtonsLayoutBackground.lightCircle, colorScanButtonsLayoutBackground.darkCircle)
        val colorScanButtonsForeground = settingsSharedPreferences.getColorItem(ColorSettings.ColorScanButtonsForeground)
        scanConfig.colorConfig.colorScanButtonsForeground = DocutainColor(colorScanButtonsForeground.lightCircle, colorScanButtonsForeground.darkCircle)
        val colorScanPolygon = settingsSharedPreferences.getColorItem(ColorSettings.ColorScanPolygon)
        scanConfig.colorConfig.colorScanPolygon = DocutainColor(colorScanPolygon.lightCircle, colorScanPolygon.darkCircle)
        val colorBottomBarBackground = settingsSharedPreferences.getColorItem(ColorSettings.ColorBottomBarBackground)
        scanConfig.colorConfig.colorBottomBarBackground = DocutainColor(colorBottomBarBackground.lightCircle, colorBottomBarBackground.darkCircle)
        val colorBottomBarForeground = settingsSharedPreferences.getColorItem(ColorSettings.ColorBottomBarForeground)
        scanConfig.colorConfig.colorBottomBarForeground = DocutainColor(colorBottomBarForeground.lightCircle, colorBottomBarForeground.darkCircle)
        val colorTopBarBackground = settingsSharedPreferences.getColorItem(ColorSettings.ColorTopBarBackground)
        scanConfig.colorConfig.colorTopBarBackground = DocutainColor(colorTopBarBackground.lightCircle, colorTopBarBackground.darkCircle)
        val colorTopBarForeground = settingsSharedPreferences.getColorItem(ColorSettings.ColorTopBarForeground)
        scanConfig.colorConfig.colorTopBarForeground = DocutainColor(colorTopBarForeground.lightCircle, colorTopBarForeground.darkCircle)

        //alter the onboarding image source if you like
        //scanConfig.onboardingImageSource = ...

        //detailed information about theming possibilities can be found here [https://docs.docutain.com/docs/Android/theming]
        //scanConfig.theme = ...

        //start the scanner using the provided config
        documentScanResult.launch(scanConfig)
    }

    private fun startPDFImport() {
        pickPDF.launch("application/pdf")
    }

    private fun startDataExtraction() {
        showInputOptionAlert()
    }

    private fun startTextRecognition() {
        showInputOptionAlert()
    }

    private fun startPDFGenerating() {
        showInputOptionAlert()
    }

    private fun showInputOptionAlert() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(R.string.title_input_option)
            .setItems(
                arrayOf(
                    getString(R.string.input_option_scan),
                    getString(R.string.input_option_PDF),
                    getString(R.string.input_option_image)
                )
            ) { _, which ->
                when (which) {
                    0 -> startScan()
                    1 -> startPDFImport()
                    2 -> startScan(imageImport = true)
                }
            }
        builder.create().show()
    }

    private fun generatePDF(uri: Uri?) = CoroutineScope(Dispatchers.IO).launch {
        if (uri != null) {
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
        //define the output file for the PDF
        val file = File(filesDir, "SamplePDF")
        //generate the PDF from the currently loaded document
        //the generated PDF also contains the detected text, making the PDF searchable
        //see [https://docs.docutain.com/docs/Android/pdfCreation] for more details
        val fileReturn = Document.writePDF(file, true, Document.PDFPageFormat.A4)
        if (fileReturn == null) {
            //an error occured, get the latest error message
            Log.i(
                logTag,
                "DocumentDataReader.loadFile failed, last error: ${DocutainSDK.getLastError()}"
            )
            return@launch
        }

        //display the PDF by using the system's default viewer for demonstration purposes
        val pdfUri = FileProvider.getUriForFile(
            this@MainActivity,
            "de.docutain.sdk.docutain_sdk_example_android_kotlin.attachments",
            fileReturn
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.data = pdfUri
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Log.i(logTag, "No Activity available for displaying the PDF")
        }
    }

    private fun openDataResultActivity(uri: Uri?) {
        val intent = Intent(this@MainActivity, DataResultActivity::class.java)
        if (uri != null) {
            intent.putExtra("uri", uri)
        }
        startActivity(intent)
    }

    private fun openTextResultActivity(uri: Uri?) {
        val intent = Intent(this@MainActivity, TextResultActivity::class.java)
        if (uri != null) {
            intent.putExtra("uri", uri)
        }
        startActivity(intent)
    }

    private fun showLicenseEmptyInfo() {
        MaterialAlertDialogBuilder(this).setTitle("License empty")
            .setMessage("A valid license key is required. Please click \"GET LICENSE\" in order to create a free trial license key on our website.")
            .setPositiveButton("Get License") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sdk.docutain.com/TrialLicense?Source=655617"))
                startActivity(intent)
                finish()
            }.setCancelable(false).show()
    }

    private fun showLicenseErrorInfo() {
        MaterialAlertDialogBuilder(this).setTitle("License error")
            .setMessage("A valid license key is required. Please contact our support to get an extended trial license.")
            .setPositiveButton("Contact Support") { _, _ ->
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:") // only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("support.sdk@Docutain.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Trial License Error")
                    putExtra(Intent.EXTRA_TEXT, "Please keep your following trial license key in this e-mail: $licenseKey")
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(
                        logTag,
                        "No Mail App available, please contact us manually via sdk@Docutain.com"
                    )
                }
            }.setCancelable(false).show()
    }
}