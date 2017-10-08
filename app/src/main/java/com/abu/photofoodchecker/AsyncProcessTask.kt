package com.abu.photofoodchecker

import android.app.ProgressDialog

import com.abu.photofoodchecker.ocrsdk.Client
import com.abu.photofoodchecker.ocrsdk.ProcessingSettings
import com.abu.photofoodchecker.ocrsdk.Task

import android.os.AsyncTask
import java.io.ByteArrayOutputStream

class AsyncProcessTask(private val activity: AnalysisResultActivity) : AsyncTask<String, String, List<Additive>>() {

    private val dialog: ProgressDialog = ProgressDialog(activity)

    override fun onPreExecute() {
        dialog.setMessage("Processing")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onPostExecute(result: List<Additive>) {
        if (dialog.isShowing) {
            dialog.dismiss()
        }

        activity.updateResults(result)
    }

    override fun doInBackground(vararg args: String): List<Additive> {

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

            publishProgress("Загрузка изображения...")

            val language = "Russian" // Comma-separated list: Japanese,English or German,French,Spanish etc.

            val processingSettings = ProcessingSettings()
            processingSettings.outputFormat = ProcessingSettings.OutputFormat.txt
            processingSettings.language = language

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
                publishProgress("Распознавание...")
                task = restClient.getTaskStatus(task.Id)
            }

            if (task.Status == Task.TaskStatus.Completed) {
                publishProgress("Скачивание...")

                //val fos = activity.openFileOutput(outputFile, Context.MODE_PRIVATE)
                val baos = ByteArrayOutputStream()

                var recognitionResult : String = ""
                baos.use {
                    restClient.downloadResult(task, baos)
                    recognitionResult = baos.toString()
                }

                publishProgress("Анализ...")
                val eNames = getAdditions(activity.applicationContext)
                val components = getComponents(recognitionResult)

                return match(components, eNames)
            } else if (task.Status == Task.TaskStatus.NotEnoughCredits) {
                throw Exception("Not enough credits to process task. Add more pages to your application's account.")
            } else {
                throw Exception("Task failed")
            }
        } catch (e: Exception) {
            val message = "Error: " + e.message
            publishProgress(message)
            return emptyList()
        }

    }

    override fun onProgressUpdate(vararg values: String) {
        // TODO Auto-generated method stub
        val stage = values[0]
        dialog.setMessage(stage)
    }
}
