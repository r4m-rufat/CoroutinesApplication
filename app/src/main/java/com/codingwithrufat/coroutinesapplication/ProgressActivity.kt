package com.codingwithrufat.coroutinesapplication

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class ProgressActivity : AppCompatActivity() {

    private lateinit var progressbar: ProgressBar
    private lateinit var btnsituation: Button
    private lateinit var txtsituation: TextView
    private lateinit var job: CompletableJob
    private val JOB_TIME: Long = 5000
    private val PROGRESS_START: Int = 0
    private val PROGRESS_MAX: Int = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        progressbar = findViewById(R.id.startHorizontal)
        btnsituation = findViewById(R.id.button)
        txtsituation = findViewById(R.id.txt_situation)

        btnsituation.setOnClickListener {

            if (!::job.isInitialized){
                initJob()
            }

            progressbar.startProgressJobOrCancel()

        }

    }

    private fun initJob() {
        btnsituation.text = "Start Job"
        txtsituation.text = ""
        job = Job()

        job.invokeOnCompletion {
            it?.message.let {
                var message = it
                if (message.isNullOrBlank()) {
                    message = "Something went wrong"
                }
                showToast(message)
            }
        }

        progressbar.progress  = PROGRESS_START
        progressbar.max = PROGRESS_MAX
    }

    private fun showToast(text: String) {

        GlobalScope.launch(Main) {

            Toast.makeText(this@ProgressActivity, text, Toast.LENGTH_SHORT).show()

        }

    }

    private fun ProgressBar.startProgressJobOrCancel(){

        if (this.progress > 0){
            btnsituation.text = "Start Job"
            resetJob()
        }else{

            btnsituation.text = "Cancel Job"
            CoroutineScope(IO + job).launch {
                for (i in PROGRESS_START .. PROGRESS_MAX){
                    delay(JOB_TIME/PROGRESS_MAX)
                    this@startProgressJobOrCancel.progress = i
                }

                withContext(Main){
                    txtsituation.text = "Complete the job"
                }

            }

        }

    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted){
            job.cancel(CancellationException("Reset Job"))
        }
        initJob()
    }

}