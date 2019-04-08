package com.ccooy.gankart.function.keeplive.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

/**
 * 整点回调广播接收器
 */
class TimeReceiver : BroadcastReceiver() {

    interface IIntegerTimeListener {
        fun integer(hour: Int)
        fun minute(minute: Int)
    }

    private var listener: IIntegerTimeListener? = null

    /**
     * 设置整点回调监听
     * @param listener IIntegerTimeListener
     */
    fun setIntegerTimeListener(listener: IIntegerTimeListener) {
        this.listener = listener
    }

    override fun onReceive(context: Context, intent: Intent) {
        val cal = Calendar.getInstance()
        val min = cal.get(Calendar.MINUTE)
        if (listener != null) {
            if (min == 0) {
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                listener?.integer(hour)
            } else {
                listener?.minute(min)
            }
        }

    }

}