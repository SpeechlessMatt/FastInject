package com.czy4201b.fastinject.core.utils


internal fun Any?.toJsLiteral(): String = when (this) {
    null -> "null"

    is Number, is Boolean -> this.toString()

    is String -> {
        val escaped = this
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        "\"$escaped\""
    }

    is Map<*, *> -> {
        val entries = this.entries.joinToString(",") { (k, v) ->
            // 递归处理 key 和 value
            "${k.toJsLiteral()}: ${v.toJsLiteral()}"
        }
        "{ $entries }"
    }

    is Iterable<*> -> {
        val items = this.joinToString(",") { it.toJsLiteral() }
        "[ $items ]"
    }

    is Array<*> -> this.toList().toJsLiteral()

    else -> {
        // 至少返回一个字符串
        "\"${this.toString().replace("\"", "\\\"")}\""
    }
}