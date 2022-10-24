package com.mhmdjalal.newsapp.ui.screens.article

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mhmdjalal.newsapp.data.models.Article
import com.mhmdjalal.newsapp.databinding.ActivityDetailNewsArticleBinding
import com.mhmdjalal.newsapp.utils.viewBinding

class DetailNewsArticleActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityDetailNewsArticleBinding::inflate)

    private var article: Article? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        article = intent.getParcelableExtra(EXTRA_ARTICLE)

        supportActionBar?.title = article?.title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webView.isScrollbarFadingEnabled = true
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webView.isVerticalScrollBarEnabled = false

        article?.url?.let { binding.webView.loadUrl(it) }
        binding.webView.webChromeClient = MyWebChromeClient()
        binding.webView.webViewClient = WebClient()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    inner class MyWebChromeClient: WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.progressHorizontal.progress = newProgress
            if (newProgress == 100) {
                binding.progressHorizontal.isVisible = false
            }
        }
    }

    inner class WebClient: WebViewClient() {
        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            binding.webView.isVisible = false
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            view?.loadUrl(request?.url.toString())
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressHorizontal.isVisible = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressHorizontal.isVisible = false

            val pageTitle = binding.webView.title ?: ""
            if (pageTitle == "") {
                binding.webView.isVisible = false
            }

            if (pageTitle == "404 Page Not Found") {
                binding.webView.isVisible = false
            }

            if (pageTitle == "404 Not Found") {
                binding.webView.isVisible = false
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ARTICLE = "article"
    }
}