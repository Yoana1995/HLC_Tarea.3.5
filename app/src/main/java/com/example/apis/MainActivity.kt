package com.example.apis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.apis.API.retrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var btnDetectLanguage:Button
    private lateinit var etDescription:EditText
    private lateinit var progressbar: ProgressBar
    var allLanguages = emptyList<Language>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initListener()
        getLanguages()
    }

    private fun initListener() {
        btnDetectLanguage.setOnClickListener {
            val text:String = btnDetectLanguage.text.toString()//
            if(text.isNotEmpty()){
                showLoading()
                getTextLanguage(text)
            }
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            progressbar.visibility=View.GONE
        }
    }

    private fun showLoading() {
        progressbar.visibility = View.VISIBLE
    }

    private fun getTextLanguage(text:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result: Response<DetectionResponse> = retrofitService.getTextLanguage(text)//
            if(result.isSuccessful){

                checkResult(result.body())
            }else{
                showError()
            }
            cleanText()
            hideLoading()
        }
    }

    private fun cleanText() {
        etDescription.setText("")
    }

    private fun checkResult(detectionResponse: DetectionResponse?) {
        if(detectionResponse != null && !detectionResponse.data.detections.isNullOrEmpty()){
            val correctLanguages: List<Detection> = detectionResponse.data.detections.filter { it.isReliable }//
            if(correctLanguages.isNotEmpty()){
                val languageName = allLanguages.find { it.code == correctLanguages.first().language}

                if(languageName != null){
                    runOnUiThread {
                        Toast.makeText(this,"El idioma es ${languageName.name}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getLanguages() {
        CoroutineScope(Dispatchers.IO).launch {
            val languages : Response<List<Language>> = retrofitService.getLanguages()//
            if (languages.isSuccessful){
                allLanguages = languages.body() ?: emptyList()
                showSuccess()
            }else{
                showError()
            }
        }
    }

    private fun showSuccess(){
        runOnUiThread {
            Toast.makeText(this, "Petición correcta", Toast.LENGTH_SHORT).show()
        }
    }

    private  fun showError(){
        runOnUiThread {
            Toast.makeText(this, "Error al hacer la llamada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView(){
        btnDetectLanguage = findViewById(R.id.btnDetectLanguage)
        etDescription = findViewById(R.id.etDescription)
        progressbar = findViewById(R.id.progressbar)
    }
}