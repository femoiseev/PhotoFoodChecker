package com.abu.photofoodchecker

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView


class AdditiveAdapter(context: Context, resource: Int, val objects: List<Additive>) :
        ArrayAdapter<Additive>(context, resource, objects) {

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val elem = objects[position]
        val scale = context.resources.displayMetrics.density
        return with(parent!!.context) {
            frameLayout {
                cardView {
                    cardElevation = dip(5f).toFloat()
                    setContentPadding((scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt(),
                            (scale * 8 + 0.5).toInt(), (scale * 8 + 0.5).toInt())
                    setCardBackgroundColor(Color.parseColor(when(elem.rank) {
                        0 -> "#A5D6A7"
                        4, 5 -> "#EF9A9A"
                        else -> "#EF9A9A"
                    }))

                    verticalLayout {
                        textView(elem.eCode) {
                            textSize = sp(6).toFloat()
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        val names = elem.names.joinToString(separator = ", ")
                        textView(names) {
                            textSize = sp(5).toFloat()
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        textView(elem.description)
                    }
                }.lparams {
                    width = matchParent
                    height = wrapContent
                    gravity = Gravity.CENTER_HORIZONTAL
                    horizontalMargin = 32
                    bottomMargin = 32
                }
            }
        }
    }
}