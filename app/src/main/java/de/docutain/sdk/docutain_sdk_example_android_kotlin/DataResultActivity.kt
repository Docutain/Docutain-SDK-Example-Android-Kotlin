package de.docutain.sdk.docutain_sdk_example_android_kotlin

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import de.docutain.sdk.DocutainSDK
import de.docutain.sdk.dataextraction.DocumentDataReader
import de.docutain.sdk.docutain_sdk_example_android_kotlin.Extensions.parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class DataResultActivity : AppCompatActivity() {

    private lateinit var textViewName1: TextInputLayout
    private lateinit var textViewName2: TextInputLayout
    private lateinit var textViewName3: TextInputLayout
    private lateinit var textViewZipcode: TextInputLayout
    private lateinit var textViewCity: TextInputLayout
    private lateinit var textViewStreet: TextInputLayout
    private lateinit var textViewPhone: TextInputLayout
    private lateinit var textViewCustomerID: TextInputLayout
    private lateinit var textViewIBAN: TextInputLayout
    private lateinit var textViewBIC: TextInputLayout
    private lateinit var textViewDate: TextInputLayout
    private lateinit var textViewAmount: TextInputLayout
    private lateinit var textViewInvoiceId: TextInputLayout
    private lateinit var textViewReference: TextInputLayout
    private lateinit var textViewPaymentState: TextInputLayout
    private val logTag = "DocutainSDK"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_result)

        //initialize the textViews which display the detected data
        textViewName1 = findViewById(R.id.textField_name1)
        textViewName2 = findViewById(R.id.textField_name2)
        textViewName3 = findViewById(R.id.textField_name3)
        textViewZipcode = findViewById(R.id.textField_zipcode)
        textViewCity = findViewById(R.id.textField_city)
        textViewStreet = findViewById(R.id.textField_street)
        textViewPhone = findViewById(R.id.textField_phone)
        textViewCustomerID = findViewById(R.id.textField_customerId)
        textViewIBAN = findViewById(R.id.textField_IBAN)
        textViewBIC = findViewById(R.id.textField_BIC)
        textViewDate = findViewById(R.id.textField_Date)
        textViewAmount = findViewById(R.id.textField_Amount)
        textViewInvoiceId = findViewById(R.id.textField_InvoiceId)
        textViewReference = findViewById(R.id.textField_reference)
        textViewPaymentState = findViewById(R.id.textField_paymentState)

        //analyze the document and load the detected data
        loadData()
    }

    private fun loadData() = CoroutineScope(Dispatchers.IO).launch{
        val uri = intent.parcelable<Uri>("uri")
        if(uri != null){
            //if an uri is available it means we have imported a file. If so, we need to load it into the SDK first
            if(!DocumentDataReader.loadFile(uri)){
                //an error occured, get the latest error message
                Log.i(logTag, "DocumentDataReader.loadFile failed, last error: ${DocutainSDK.getLastError()}")
                return@launch
            }
        }

        //analyze the currently loaded document and get the detected data
        val analyzeData = DocumentDataReader.analyze()
        if(analyzeData.isEmpty()){
            //no data detected
            return@launch
        }

        //detected data is returned as JSON, so serializing the data in order to extract the key value pairs
        //see [https://docs.docutain.com/docs/Android/dataExtraction] for more information
        val jsonArray = JSONObject(analyzeData)
        val address = JSONObject(jsonArray.getString("Address"))
        val name1 = address.getString("Name1")
        val name2 = address.getString("Name2")
        val name3 = address.getString("Name3")
        val zipcode = address.getString("Zipcode")
        val city = address.getString("City")
        val street = address.getString("Street")
        val phone = address.getString("Phone")
        val customerId = address.getString("CustomerId")
        val bank = address.getJSONArray("Bank")
        var IBAN = ""
        var BIC = ""
        //TODO: handle multiple bank accounts
        if(bank.length() > 0){
            val object1 = bank.getJSONObject(0)
            IBAN = object1.getString("IBAN")
            BIC = object1.getString("BIC")
        }
        val date = jsonArray.getString("Date")
        val amount = jsonArray.getString("Amount")
        val invoiceId = jsonArray.getString("InvoiceId")
        val reference = jsonArray.getString("Reference")
        val paid = jsonArray.optString("PaymentState")

        //load the text into the textfields if value is detected
        MainScope().launch {
            if(name1.isNotEmpty()){
                textViewName1.editText!!.setText(name1)
                textViewName1.visibility = View.VISIBLE
            }
            if(name2.isNotEmpty()){
                textViewName2.editText!!.setText(name2)
                textViewName2.visibility = View.VISIBLE
            }
            if(name3.isNotEmpty()){
                textViewName3.editText!!.setText(name3)
                textViewName3.visibility = View.VISIBLE
            }
            if(zipcode.isNotEmpty()){
                textViewZipcode.editText!!.setText(zipcode)
                textViewZipcode.visibility = View.VISIBLE
            }
            if(city.isNotEmpty()){
                textViewCity.editText!!.setText(city)
                textViewCity.visibility = View.VISIBLE
            }
            if(street.isNotEmpty()){
                textViewStreet.editText!!.setText(street)
                textViewStreet.visibility = View.VISIBLE
            }
            if(phone.isNotEmpty()){
                textViewPhone.editText!!.setText(phone)
                textViewPhone.visibility = View.VISIBLE
            }
            if(customerId.isNotEmpty()){
                textViewCustomerID.editText!!.setText(customerId)
                textViewCustomerID.visibility = View.VISIBLE
            }
            if(IBAN.isNotEmpty()){
                val regex = ".{4}".toRegex()
                textViewIBAN.editText!!.setText(IBAN.replace(regex, "\$0 "))
                textViewIBAN.visibility = View.VISIBLE
            }
            if(BIC.isNotEmpty()){
                textViewBIC.editText!!.setText(BIC)
                textViewBIC.visibility = View.VISIBLE
            }
            if(date.isNotEmpty()){
                textViewDate.editText!!.setText(date)
                textViewDate.visibility = View.VISIBLE
            }
            if(amount.isNotEmpty() && (amount != "0.00")){
                textViewAmount.editText!!.setText(amount)
                textViewAmount.visibility = View.VISIBLE
            }
            if(invoiceId.isNotEmpty()){
                textViewInvoiceId.editText!!.setText(invoiceId)
                textViewInvoiceId.visibility = View.VISIBLE
            }
            if(reference.isNotEmpty()){
                textViewReference.editText!!.setText(reference)
                textViewReference.visibility = View.VISIBLE
            }
            if(paid.isNotEmpty()){
                textViewPaymentState.editText!!.setText(paid)
                textViewPaymentState.visibility = View.VISIBLE
            }
            findViewById<ProgressBar>(R.id.activity_indicator).visibility = View.GONE
        }
    }
}