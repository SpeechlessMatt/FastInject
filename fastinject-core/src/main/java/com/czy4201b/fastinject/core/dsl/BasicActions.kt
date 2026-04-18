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