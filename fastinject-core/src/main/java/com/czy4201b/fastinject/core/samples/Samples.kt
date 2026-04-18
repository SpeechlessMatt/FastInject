package com.czy4201b.fastinject.core.samples

import com.czy4201b.fastinject.core.fastInject

/**
 * 创建一个 js 变量例子
 */
fun sampleCreateVarUsage() {
    fastInject {
        val valueRef = createVar("(100 + 10) / 50 + 1")
        val valueRef2 = valOf("1 + 1")
        log("这是js的计算结果: ", valueRef)
        log("这是js的计算结果: ", valueRef2)
    }
}

/**
 * 创建一个 js 变量例子
 */
fun sampleCreateKValueVarUsage() {
    fastInject {
        val kString = "你好呀~"
        val kInt = 100 + 10 - 60
        // 两种写法是等价的
        val valueRef = createKVar(kString)
        val valueRef2 = kValOf(kInt)
        log(valueRef, "这是kotlin的计算结果：", valueRef2)
    }
}

fun sampleLogUsage() {
    fastInject {
        val valueRef = createVar("(100 + 10) / 50 + 1")
        log("这是一个日志，这是我的计算结果：", valueRef)
        warn("这是一个日志，这是我的计算结果：", valueRef)
        info("这是一个日志，这是我的计算结果：", valueRef)
        trace("这是一个日志，这是我的计算结果：", valueRef)
    }
}

fun sampleAttributionUsage() {
    fastInject {
        val container = findAll("css-main-container")
        log("这是一个日志，这是 container 的长度", container.size)
        container.forEach {
            // 以下两者是等价的
            log("这是一个日志，这是 container 的 innerText：", it.text)
            log("这是一个日志，这是 container 的 innerText：", it.innerText)
        }
    }
}

fun sampleExecuteIsolatedJsUsage() {
    fastInject {
        execIsolatedJs("let hello_world = 1)")
    }
}