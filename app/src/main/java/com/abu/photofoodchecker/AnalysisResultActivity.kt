package com.abu.photofoodchecker

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.jetbrains.anko.*

class AnalysisResultActivity : Activity() {

    var outputPath = "unknown"
    var markView: TextView? = null
    var scroll: ScrollView? = null
    var list: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var imageUrl = "unknown"

        val extras = intent.extras
        if (extras != null) {
            imageUrl = extras.getString("IMAGE_PATH")
            outputPath = extras.getString("RESULT_PATH")
        }
        // Starting recognition process
        AsyncProcessTask(this).execute(imageUrl, outputPath)

        val scale = resources.displayMetrics.density

        frameLayout {
            backgroundColor = Color.parseColor("#B2DFDB")

            scroll = scrollView {
                visibility = View.INVISIBLE
                verticalLayout {
                    textView("Безопасность: ") {
                        textSize = sp(10f).toFloat()
                        textColor = Color.parseColor("#37474F")
                        gravity = Gravity.CENTER_HORIZONTAL
                        setTypeface(null, Typeface.BOLD_ITALIC)
                    }.lparams {
                        topMargin = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    markView = textView {
                        textSize = sp(18f).toFloat()
                        textColor = Color.BLACK
                        gravity = Gravity.CENTER_HORIZONTAL
                        setTypeface(null, Typeface.BOLD)
                    }
                    textView("Обнаружены добавки:") {
                        textSize = sp(8f).toFloat()
                        textColor = Color.parseColor("#455A64")
                        setTypeface(null, Typeface.ITALIC)
                    }.lparams {
                        gravity = Gravity.CENTER_HORIZONTAL
                        bottomMargin = dip(16)
                    }

                    list = verticalLayout()
                }.lparams {
                    width = matchParent
                }
            }
        }
    }

    fun updateResults(result: List<Additive>) {
        val mark = mark(result)
        markView?.text = mark.toString()
        val listAdapter = AdditiveAdapter(this, 0, result)
        for (i in 0 until listAdapter.count) {
            list?.addView(listAdapter.getView(i, null, list))
        }

        scroll?.visibility = View.VISIBLE
    }
}
