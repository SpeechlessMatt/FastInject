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