package com.noble.activity.RAZA_3.etc_process

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class content_RecyclerVier_ClickEvent : RecyclerView.OnItemTouchListener {


    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    private var mListener: OnItemClickListener? = null
    private var mGestureDetector: GestureDetector? = null

    constructor(context: Context, rView: RecyclerView, clickListener: OnItemClickListener){

        mListener = clickListener
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val childView = rView.findChildViewUnder(e.x, e.y)

                if (childView != null && mListener != null) {
                    mListener!!.onItemLongClick(childView, rView.getChildPosition(childView))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
        val childView = rv!!.findChildViewUnder(e!!.getX(), e.getY())

        if (childView != null && mListener != null && mGestureDetector!!.onTouchEvent(e)) {
            mListener!!.onItemClick(childView, rv.getChildPosition(childView))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {

    }


    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}