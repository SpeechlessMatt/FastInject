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

import com.czy4201b.fastinject.core.model.DelayRef
import com.czy4201b.fastinject.core.model.ElementRef
import com.czy4201b.fastinject.core.model.TimeRef
import com.czy4201b.fastinject.core.dsl.JsHelpers.TIME_HELPER
import com.czy4201b.fastinject.core.model.ChainRef
import com.czy4201b.fastinject.core.model.ValueRef
import com.czy4201b.fastinject.core.utils.toJsLiteral


internal fun performSetTimeOut(ms: Int): DelayRef = DelayRef(ms)

internal fun ChainRef.performChainSetTimeOut(ms: Int): DelayRef {
    return performSetTimeOut(ms).apply { isChained = true }
}

internal fun DelayRef.performSetTimeOutThen(
    scope: FastInjectScope,
    block: FastInjectScope.() -> Unit
): ChainRef {
    if (isChained) {
        removeLastSemicolon(scope)
        scope.execJs(
            prefix = ".then(() => new Promise(res => setTimeout(() => res(), $ms))).then(() =>",
            suffix = ");"
        ) {
            block()
        }
    } else {
        scope.execJs("setTimeout(() =>", ", ${this.ms});") {
            block()
        }
    }
    return ChainRef()
}

internal fun performWaitElement(scope: FastInjectScope, selector: String, ms: Int): TimeRef {
    val varName = scope.nextElementId()
    scope.ensureHelper("TimeHelper", TIME_HELPER)
    return TimeRef(varName, selector, ms)
}

internal fun ChainRef.performChainWaitElement(
    scope: FastInjectScope,
    selector: String,
    ms: Int
): TimeRef {
    return performWaitElement(scope, selector, ms).apply { isChained = true }
}

internal fun TimeRef.performWaitElementThen(
    scope: FastInjectScope,
    block: FastInjectScope.(ElementRef) -> Unit
): ChainRef {
    val elementRef = ElementRef(varName, selector)
    if (isChained) {
        removeLastSemicolon(scope)

        scope.execJs(
            prefix = ".then(() => fi_ctx.fastInjectWaitForElement(${selector.toJsLiteral()}, $ms).then($varName =>",
            suffix = "));"
        ) {
            block(elementRef)
        }
    } else {
        scope.execJs(
            prefix = "fi_ctx.fastInjectWaitForElement(${selector.toJsLiteral()}, $ms).then($varName =>",
            suffix = ");"
        ) {
            block(elementRef)
        }
    }
    return ChainRef()
}

internal fun ChainRef.performChainThen(
    scope: FastInjectScope,
    block: FastInjectScope.() -> Unit
): ChainRef {
    removeLastSemicolon(scope)

    scope.execJs(
        prefix = ".then(() =>",
        suffix = ");"
    ) {
        block()
    }

    return this
}

internal fun ChainRef.performChainCatch(
    scope: FastInjectScope,
    block: FastInjectScope.(ValueRef) -> Unit
): ChainRef {
    removeLastSemicolon(scope)

    val errId = scope.nextValueId()
    scope.execJs(
        prefix = ".catch($errId =>",
        suffix = ");"
    ) {
        block(ValueRef(errId))
    }
    return ChainRef()
}

internal fun ChainRef.performChainFinally(
    scope: FastInjectScope,
    block: FastInjectScope.() -> Unit
): ChainRef {
    removeLastSemicolon(scope)

    scope.execJs(
        prefix = ".finally(() =>",
        suffix = ");"
    ) {
        block()
    }
    return ChainRef()
}

private fun removeLastSemicolon(scope: FastInjectScope) {
    if (scope.jsFunc.isNotEmpty()) {
        val lastIdx = scope.jsFunc.size - 1
        val lastLine = scope.jsFunc[lastIdx].trimEnd()
        if (lastLine.endsWith(";")) {
            scope.jsFunc[lastIdx] = lastLine.substring(0, lastLine.lastIndexOf(";"))
        }
    }
}