package com.abu.photofoodchecker

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.abu.photofoodchecker.android.AsyncProcessTask
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import java.io.BufferedReader
import java.io.InputStreamReader

class AnalysisResultActivity : Activity() {

    var outputPath = "unknown"
    var description: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scale = resources.displayMetrics.density

        frameLayout {
            backgroundColor = Color.parseColor("#B2DFDB")
            scrollView {
                verticalLayout {
                    textView("Полезность: ") {
                        textSize = sp(10f).toFloat()
                        textColor = Color.parseColor("#37474F")
                        gravity = Gravity.CENTER_HORIZONTAL
                        setTypeface(null, Typeface.BOLD_ITALIC)
                    }
                    val mark = textView("2") {
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
                    }

                    val list = verticalLayout {
                        cardView {
                            cardElevation = dip(3f).toFloat()
                            radius = dip(5f).toFloat()
                            setCardBackgroundColor(Color.parseColor("#EF9A9A"))
                            setContentPadding((scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt(),
                                    (scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt())
                            verticalLayout {
                                textView("Е161а") {
                                    textSize = sp(6).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("ФЛАВОКСАНТИН") {
                                    textSize = sp(5).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("Заболевания желудочно-кишечного тракта. Запрещен в большинстве стран")
                            }
                        }.lparams {
                            width = matchParent
                            height = wrapContent
                            gravity = Gravity.CENTER_HORIZONTAL
                            horizontalMargin = 16
                            verticalMargin = 8
                        }

                        cardView {
                            cardElevation = dip(5f).toFloat()
                            radius = dip(5f).toFloat()
                            setContentPadding((scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt(),
                                    (scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt())
                            setCardBackgroundColor(Color.parseColor("#81C784"))

                            verticalLayout {
                                textView("Е284") {
                                    textSize = sp(6).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("БОРНАЯ КИСЛОТА") {
                                    textSize = sp(5).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("Аллергические реакции. Разрешен")
                            }
                        }.lparams {
                            width = matchParent
                            height = wrapContent
                            gravity = Gravity.CENTER_HORIZONTAL
                            horizontalMargin = 16
                            verticalMargin = 8
                        }

                        cardView {
                            cardElevation = dip(5f).toFloat()
                            radius = dip(5f).toFloat()
                            setContentPadding((scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt(),
                                    (scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt())
                            setCardBackgroundColor(Color.parseColor("#EF9A9A"))

                            verticalLayout {
                                textView("Е280") {
                                    textSize = sp(6).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("ПРОПИОНОВАЯ КИСЛОТА") {
                                    textSize = sp(5).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("Раковые опухоли. Негативное влияние на детей. Разрешен")
                            }
                        }.lparams {
                            width = matchParent
                            height = wrapContent
                            gravity = Gravity.CENTER_HORIZONTAL
                            horizontalMargin = 16
                            verticalMargin = 8
                        }

                        cardView {
                            cardElevation = dip(5f).toFloat()
                            radius = dip(5f).toFloat()
                            setContentPadding((scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt(),
                                    (scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt())
                            setCardBackgroundColor(Color.parseColor("#B0BEC5"))

                            verticalLayout {
                                textView("Е171") {
                                    textSize = sp(6).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("ДИОКСИД ТИТАНА") {
                                    textSize = sp(5).toFloat()
                                    gravity = Gravity.CENTER_HORIZONTAL
                                }
                                textView("Подозрителен. Негативное влияние на детей. Разрешен")
                            }
                        }.lparams {
                            width = matchParent
                            height = wrapContent
                            gravity = Gravity.CENTER_HORIZONTAL
                            horizontalMargin = 16
                            verticalMargin = 8
                            bottomMargin = 16
                        }
                    }
                }.lparams {
                    width = matchParent
                }
            }
        }

        var imageUrl = "unknown"

        val extras = intent.extras
        if (extras != null) {
            imageUrl = extras.getString("IMAGE_PATH")
            outputPath = extras.getString("RESULT_PATH")
        }

        // Starting recognition process
        //AsyncProcessTask(this).execute(imageUrl, outputPath)
    }

    fun updateResults(text: String) {
        displayMessage(text)
    }

    fun displayMessage(text: String) {
        description?.text = text
    }
}
