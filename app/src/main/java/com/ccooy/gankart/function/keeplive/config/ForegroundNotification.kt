package com.ccooy.gankart.function.keeplive.config

import java.io.Serializable

/**
 * 默认前台通知样式
 */
class ForegroundNotification private constructor() : Serializable {

    private var title: String? = null
    private var description: String? = null
    private var iconRes: Int = 0
    private var foregroundNotificationClickListener: ForegroundNotificationClickListener? = null

    constructor(
        title: String,
        description: String,
        iconRes: Int,
        foregroundNotificationClickListener: ForegroundNotificationClickListener
    ) : this() {
        this.title = title
        this.description = description
        this.iconRes = iconRes
        this.foregroundNotificationClickListener = foregroundNotificationClickListener
    }

    constructor(title: String, description: String, iconRes: Int) : this() {
        this.title = title
        this.description = description
        this.iconRes = iconRes
    }

    fun setTitle(title: String): ForegroundNotification {
        this.title = title
        return this
    }

    fun setDescription(description: String): ForegroundNotification {
        this.description = description
        return this
    }

    fun setIconRes(iconRes: Int): ForegroundNotification {
        this.iconRes = iconRes
        return this
    }

    fun setNotificationClickListener(listener: ForegroundNotificationClickListener) {
        this.foregroundNotificationClickListener = listener
    }

    fun getTitle(): String {
        return if (title == null) "" else title!!
    }

    fun getIconRes(): Int {
        return iconRes
    }

    fun getForegroundNotificationClickListener(): ForegroundNotificationClickListener? {
        return foregroundNotificationClickListener
    }

    fun getDescription(): String {
        return if (description == null) "" else description!!
    }

}