package com.czy4201b.fastinject.core.samples

import com.czy4201b.fastinject.core.fastInject

fun sampleDisableTimeCheckUsage() {
    fastInject {
        disableTimeCheck(1779206400)

        // 做一些事情？例如...
        val container = find("css-main-container")
        container.click()

        // 这里可选的恢复原来的时间
        restoreTimeCheck()
    }
}