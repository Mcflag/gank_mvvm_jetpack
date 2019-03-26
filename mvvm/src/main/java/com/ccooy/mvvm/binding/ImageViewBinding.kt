package com.ccooy.mvvm.binding

import androidx.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.ccooy.mvvm.image.GlideApp

@BindingAdapter("bind_imageView_res")
fun loadImage(imageView: ImageView, res: Int?) {
    GlideApp.with(imageView.context)
        .load(res)
        .into(imageView)
}

@BindingAdapter("bind_imageView_url")
fun loadImage(imageView: ImageView, url: String?) {
    GlideApp.with(imageView.context)
        .load(url)
        .into(imageView)
}

@BindingAdapter("bind_imageView_url_circle")
fun loadImageCircle(imageView: ImageView, url: String?) {
    GlideApp.with(imageView.context)
        .load(url)
        .apply(RequestOptions().circleCrop())
        .into(imageView)
}