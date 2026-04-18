/*
 * Copyright 2026 Czy4201_b
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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