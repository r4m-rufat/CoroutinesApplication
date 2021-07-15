package com.codingwithrufat.coroutinesapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"
    private lateinit var _txt_result: TextView
    private lateinit var _btn_result: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _txt_result = findViewById(R.id._txt_result)
        _btn_result = findViewById(R.id._button_result)

        _btn_result.setOnClickListener{

            _txt_result.text = "Clicked"
            CoroutineScope(IO).launch {
                apiRequest()
            }

        }

    }

    private suspend fun apiRequest(){
        withContext(IO){
            val job = withTimeoutOrNull(2100){
                launch{ // there is no difference between withContext(Main) and CoroutineScope(Main).launch
                    setTextOnMainThread()
                }
            }

            if (job == null){
                withContext(Main){
                    _txt_result.text = "${_txt_result.text} \nCancelling"
                }
            }

        }
    }

    private suspend fun setTextOnMainThread(){
        withContext(Main){ // there is no difference between withContext(Main) and CoroutineScope(Main).launch
            val text1 = getResultString1()
            _txt_result.text = "${_txt_result.text} \n${text1} "
        }

        withContext(Main){
            val text2 = getResultString2()
            _txt_result.text = "${_txt_result.text} \n${text2}"
        }

    }

    private suspend fun getResultString1(): String{
        delay(1000L)
        return RESULT_1
    }

    private suspend fun getResultString2(): String{
        delay(1000)
        return RESULT_2
    }

}