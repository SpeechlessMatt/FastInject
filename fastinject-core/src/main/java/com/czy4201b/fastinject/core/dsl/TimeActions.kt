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


internal fun performSetTimeOut(ms: Int): DelayRef {
    return DelayRef(ms)
}

internal fun DelayRef.performSetTimeOutThen(
    scope: FastInjectScope,
    block: FastInjectScope.() -> Unit
) {
    scope.execJs("setTimeout(function()", ", ${this.ms});") {
        scope.block()
    }
}

internal fun performWaitElement(scope: FastInjectScope, selector: String, ms: Int): TimeRef {
    val varName = scope.nextElementId()
    scope.ensureHelper("TimeHelper", TIME_HELPER)
    return TimeRef(varName, selector, ms)
}

internal fun TimeRef.performWaitElementThen(
    scope: FastInjectScope,
    block: FastInjectScope.(ElementRef) -> Unit
) {
    val elementRef = ElementRef(varName, selector)

    scope.execJs(
        prefix = "window.fastInjectWaitForElement(\"$selector\", $ms).then(el =>",
        suffix = ").catch(err => console.error('[FastInject] ' + err));"
    ) {
        execJs("const $varName = el;")
        scope.block(elementRef)
    }
}