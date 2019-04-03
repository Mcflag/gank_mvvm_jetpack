package com.ccooy.gankart.ui.main

import android.content.Context
import android.content.Intent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.MutableLiveData
import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.ActivityWebviewBinding
import com.ccooy.mvvm.base.view.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {

    override val layoutId: Int = R.layout.activity_webview

    lateinit var urlString: String
    val title: MutableLiveData<String> = MutableLiveData()

    override fun initView() {
        urlString = intent.getStringExtra(URL_EXTRA)
        title.postValue(intent.getStringExtra(TITLE_EXTRA))

        val settings = web_view.settings
        settings.loadWithOverviewMode = true
        settings.javaScriptEnabled = true
        settings.setAppCacheEnabled(true)
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.setSupportZoom(true)

        web_view.webChromeClient = MyWebChrome()
        web_view.webViewClient = MyWebClient()

        web_view.loadUrl(urlString)
    }

    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            finish()
        }
    }

    private inner class MyWebChrome : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progress_bar.visibility = View.VISIBLE
            progress_bar.progress = newProgress
        }
    }

    private inner class MyWebClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            progress_bar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "WebViewActivity"
        private const val URL_EXTRA = "urlExtra"
        private const val TITLE_EXTRA = "titleExtra"

        fun launch(mContext: Context, url: String) =
            mContext.apply {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(URL_EXTRA, url)
                startActivity(intent)
            }

        fun launch(mContext: Context, url: String, title: String) =
            mContext.apply {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(URL_EXTRA, url)
                intent.putExtra(TITLE_EXTRA, title)
                startActivity(intent)
            }
    }

}