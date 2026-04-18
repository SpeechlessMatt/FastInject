package com.czy4201b.fastinject.android.bridge

import android.webkit.WebView as AndroidWebView
import com.tencent.smtt.sdk.WebView as X5WebView
import com.czy4201b.fastinject.core.dsl.FastInjectScope


const val BRIDGE_NAME = "FastInjectBridge"

fun AndroidWebView.fastInject(
    onMessage: (String) -> Unit,
    block: FastInjectScope.() -> Unit
) {
    this.addJavascriptInterface(object {
        @android.webkit.JavascriptInterface
        fun post(vararg args: Any?) {
            val message = args.joinToString(" ")
            this@fastInject.post { onMessage(message) }
        }
    }, BRIDGE_NAME)

    val jsCode = com.czy4201b.fastinject.core.fastInject(block)
    this.evaluateJavascript(jsCode, null)
}

fun X5WebView.fastInject(
    onMessage: (String) -> Unit,
    block: FastInjectScope.() -> Unit
) {
    this.addJavascriptInterface(object {
        @android.webkit.JavascriptInterface
        fun post(vararg args: Any?) {
            val message = args.joinToString(" ")
            this@fastInject.post { onMessage(message) }
        }
    }, BRIDGE_NAME)

    val jsCode = com.czy4201b.fastinject.core.fastInject(block)
    this.evaluateJavascript(jsCode, null)
}