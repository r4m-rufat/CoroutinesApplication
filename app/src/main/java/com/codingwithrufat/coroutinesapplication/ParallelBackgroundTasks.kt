package com.codingwithrufat.coroutinesapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class ParallelBackgroundTasks : AppCompatActivity() {

    private lateinit var txtBackgroundTasks: TextView
    private lateinit var buttonClick: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paralel_background_tasks)

        txtBackgroundTasks = findViewById(R.id.txtBackgroundTask)
        buttonClick = findViewById(R.id.clickButton)

        buttonClick.setOnClickListener{
            setText("Clicked")
            /**
             * there is no difference between these two functions but async and await is quicker (approximately 40-50 ms)
             */
            apiRequest3() // async task happens sequentially but apiRequest1() and apiRequest2() are parallel background task

        }

    }

    private fun apiRequest1(){

        val startTime = System.currentTimeMillis()
        val parentJob  = CoroutineScope(IO).launch {

            val job1 = launch {

                val time1 = measureTimeMillis {
                    setTextOnMainThread(getResultFromApi1())
                }

                println("debug: Completed job #1 in $time1 ms")

            }

            val job2 = launch {

                val time2 = measureTimeMillis {
                    setTextOnMainThread(getResultFromApi2())
                }

                println("debug: Completed job #2 in $time2 ms")

            }

        }

        parentJob.invokeOnCompletion {
            println("debug: All operation ends in ${System.currentTimeMillis() - startTime} ms")
        }

    }

    private fun apiRequest2(){

        CoroutineScope(IO).launch {

            val time = measureTimeMillis {

                val result1: Deferred<String> = async {
                    getResultFromApi1()
                }

                val result2: Deferred<String> = async {
                    getResultFromApi2()
                }

                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")

            }

            println("debug: All staff happens in $time ms")

        }

    }

    private fun apiRequest3(){

        CoroutineScope(IO).launch {

            val time = measureTimeMillis {

                val result1: Unit = async {
                    setTextOnMainThread(getResultFromApi1())
                }.await()

                val result2: Unit = async {
                    setTextOnMainThread(getResultFromApi2())
                }.await()

            }

            println("debug: All staff happens in $time ms") // sequential background tasks

        }

    }


    private fun setText(input: String){

        val txt = txtBackgroundTasks.text.toString() + "\n$input"
        txtBackgroundTasks.text = txt

    }

    private suspend fun setTextOnMainThread(text: String){

        withContext(Main){
            setText(text)
        }

    }

    private suspend fun getResultFromApi1(): String{
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResultFromApi2(): String{
        delay(2000)
        return "Result #2"
    }

}

