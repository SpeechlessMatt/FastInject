package com.czy4201b.fastinject.core.dsl

fun performDisableTimeCheck(scope: FastInjectScope, toDate: Long) {
    scope.ensureHelper("TimeCheckHelper", JsHelpers.TIME_CHECK_HELPER)
    scope.execJs("window.fastHookDate($toDate);")
}

fun performRestoreTimeCheck(scope: FastInjectScope) {
    scope.execJs("""
        (function(){
            if (window.fastInjectRestoreDate) {
                window.fastInjectRestoreDate();
            } else {
                console.warn('[FastInject] No Date hack found to restore');
            }
        })();
    """.trimIndent())
}