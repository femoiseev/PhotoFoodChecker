package com.abu.photofoodchecker

import android.app.ProgressDialog

import com.abu.photofoodchecker.ocrsdk.Client
import com.abu.photofoodchecker.ocrsdk.ProcessingSettings
import com.abu.photofoodchecker.ocrsdk.Task
import com.abu.photofoodchecker.match

import android.content.Context
import android.os.AsyncTask
import java.io.ByteArrayOutputStream
import java.io.File

class AsyncProcessTask(private val activity: AnalysisResultActivity) : AsyncTask<String, String, String>() {

    private val dialog: ProgressDialog = ProgressDialog(activity)

    override fun onPreExecute() {
        dialog.setMessage("Processing")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onPostExecute(result: String) {
        if (dialog.isShowing) {
            dialog.dismiss()
        }

        activity.updateResults(result)
    }

    override fun doInBackground(vararg args: String): String {

        val inputFile = args[0]
        val outputFile = args[1]

        try {
            val restClient = Client()

            /*!!! Please provide application id and password and remove this line. !!!
			// To create an application and obtain a password,
			// register at http://cloud.ocrsdk.com/Account/Register
			// More info on getting your application id and password at
			// http://ocrsdk.com/documentation/faq/#faq3

			// Name of application you created*/
            restClient.applicationId = "Photo Food Checker"
            // You should get e-mail from ABBYY Cloud OCR SDK service with the application password
            restClient.password = "ewIYzp+Jnt9W5Mni7j4fRZQ+"

            publishProgress("Uploading image...")

            val language = "Russian" // Comma-separated list: Japanese,English or German,French,Spanish etc.

            val processingSettings = ProcessingSettings()
            processingSettings.outputFormat = ProcessingSettings.OutputFormat.txt
            processingSettings.language = language

            publishProgress("Uploading..")

            // If you want to process business cards, uncomment this
            /*
			BusCardSettings busCardSettings = new BusCardSettings();
			busCardSettings.setLanguage(language);
			busCardSettings.setOutputFormat(BusCardSettings.OutputFormat.xml);
			Task task = restClient.processBusinessCard(filePath, busCardSettings);
			*/
            var task = restClient.processImage(inputFile, processingSettings)

            while (task.isTaskActive) {
                // Note: it's recommended that your application waits
                // at least 2 seconds before making the first getTaskStatus request
                // and also between such requests for the same task.
                // Making requests more often will not improve your application performance.
                // Note: if your application queues several files and waits for them
                // it's recommended that you use listFinishedTasks instead (which is described
                // at http://ocrsdk.com/documentation/apireference/listFinishedTasks/).

                Thread.sleep(5000)
                publishProgress("Recognizing..")
                task = restClient.getTaskStatus(task.Id)
            }

            if (task.Status == Task.TaskStatus.Completed) {
                publishProgress("Downloading...")

                //val fos = activity.openFileOutput(outputFile, Context.MODE_PRIVATE)
                val baos = ByteArrayOutputStream()

                var recognitionResult : String = ""
                baos.use { baos ->
                    restClient.downloadResult(task, baos)
                    recognitionResult = baos.toString()
                }

                publishProgress("Analyzing...")
                val eNames = getAdditions(activity.applicationContext)
                val components = getComponents(recognitionResult)

                val text: StringBuilder = StringBuilder()
                for (component in components) {
                    val (e, score) = getECode(eNames, component)
                    if (score > 90) {
                        text.append("Matched «$component» to $e (${eNames[e]}) with score $score\n")
                    }
                }
                return text.toString()
            } else if (task.Status == Task.TaskStatus.NotEnoughCredits) {
                throw Exception("Not enough credits to process task. Add more pages to your application's account.")
            } else {
                throw Exception("Task failed")
            }
        } catch (e: Exception) {
            val message = "Error: " + e.message
            publishProgress(message)
            activity.displayMessage(message)
            return ""
        }

    }

    override fun onProgressUpdate(vararg values: String) {
        // TODO Auto-generated method stub
        val stage = values[0]
        dialog.setMessage(stage)
        // dialog.setProgress(values[0]);
    }
}
