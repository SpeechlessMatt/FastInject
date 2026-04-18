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
package com.czy4201b.fastinject.core.dsl

import com.czy4201b.fastinject.core.model.ElementRef
import com.czy4201b.fastinject.core.model.ValueRef
import com.czy4201b.fastinject.core.model.JsRef


internal fun performExecuteJs(scope: FastInjectScope, js: String) {
    scope.jsFunc += js
}

internal fun performExecuteJs(
    scope: FastInjectScope,
    prefix: String,
    block: FastInjectScope.() -> Unit
) {
    scope.jsFunc += "$prefix {\n"
    scope.block()
    scope.jsFunc += "}"
}

internal fun performExecuteJs(
    scope: FastInjectScope,
    prefix: String,
    suffix: String,
    block: FastInjectScope.() -> Unit
) {
    scope.jsFunc += "$prefix {\n"
    scope.block()
    scope.jsFunc += "\n}$suffix"
}

internal fun performExecuteIsolatedJs(scope: FastInjectScope, js: String) {
    scope.jsFunc += """
        (function() {$js})();
    """.trimIndent()
}

internal fun performExecuteIsolatedJs(
    scope: FastInjectScope,
    prefix: String,
    block: FastInjectScope.() -> Unit
) {
    scope.execJs("(function() ", ")();") {
        scope.execJs(prefix, block)
    }
}

internal fun performExecuteIsolatedJs(
    scope: FastInjectScope,
    prefix: String,
    suffix: String,
    block: FastInjectScope.() -> Unit
) {
    scope.execJs("(function() ", ")();") {
        scope.execJs(prefix, suffix, block)
    }
}

/**
 * 向浏览器控制台打印日志
 * @param messages 可以是 Kotlin 的 [String]、[Int]，也可以是 [ElementRef] 或 [ValueRef] 等 [JsRef] 的子类
 */
internal fun performLog(scope: FastInjectScope, messages: Array<out Any>, level: String = "log") {
    val jsArgs = messages.joinToString(", ") { msg ->
        when (msg) {
            is ElementRef -> msg.varName
            is ValueRef -> msg.varName
            else -> {
                val escaped = msg.toString().replace("'", "\\'")
                "'$escaped'"
            }
        }
    }
    scope.execJs("console.$level('[FastInject] ', $jsArgs);")
}