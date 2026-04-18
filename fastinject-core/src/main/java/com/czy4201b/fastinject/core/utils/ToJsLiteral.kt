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