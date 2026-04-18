package com.czy4201b.fastinject.core.samples

import com.czy4201b.fastinject.core.fastInject

fun sampleWaitElementUsage() {
    fastInject {
        // 异步
        waitElement("css-click-box", 100).then {
            it.click()
        }
        val container = find("css-main-container")
        // 异步
        container.waitElement(100).then {
            it.click()
        }
    }
}

fun sampleSetTimeoutUsage() {
    fastInject {
        setTimeOut(100).then {
            log("你好呀")
        }
    }
}