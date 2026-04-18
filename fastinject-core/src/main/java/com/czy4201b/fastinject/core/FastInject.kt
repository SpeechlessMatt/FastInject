package com.czy4201b.fastinject.core

import com.czy4201b.fastinject.core.dsl.FastInjectScope

fun fastInject(block: FastInjectScope.() -> Unit): String {
    val scope = FastInjectScope()

    scope.block()
    val body = scope.build()

    return """
        (function() {
            try {
                $body
            } catch (e) {
                console.error('[FastInject] Error:', e);
            }
        })();
    """.trimIndent()
}