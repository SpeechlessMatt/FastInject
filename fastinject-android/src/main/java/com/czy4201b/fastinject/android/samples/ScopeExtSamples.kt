package com.czy4201b.fastinject.android.samples

import com.czy4201b.fastinject.android.bridge.emit
import com.czy4201b.fastinject.core.fastInject


fun sampleEmitUsage() {
    fastInject {
        val value = valOf("1 + 1")
        emit("get result: ", value)
    }
}