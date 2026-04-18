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

import com.czy4201b.fastinject.core.dsl.FastInjectScope
import com.czy4201b.fastinject.core.model.JsRef

/**
 * 将 JS 侧的数据异步传回 Android 原生层
 *
 * 无论在多么深的异步闭包（如 waitElement 或 setTimeOut）中，
 * 调用此方法都能将数据发送给 WebView.fastInject 的 onMessage 回调
 *
 * @param args 传递的参数，支持：
 * 1. 字符串 (String) -> 自动转为 JS 字符串
 * 2. 引用对象 (JsRef/ElementRef) -> 自动读取其在 JS 内存中的变量值
 * 3. 基本类型 (数字/布尔) -> 原样传递
 *
 * @sample com.czy4201b.fastinject.android.samples.sampleEmitUsage
 * @see com.czy4201b.fastinject.android.bridge.fastInject
 */
fun FastInjectScope.emit(vararg args: Any?) {
    val jsArgs = args.joinToString(", ") { arg ->
        when (arg) {
            is JsRef -> arg.varName

            is String -> "'$arg'"

            else -> arg.toString()
        }
    }
    this.execJs("window.$BRIDGE_NAME.post($jsArgs);")
}