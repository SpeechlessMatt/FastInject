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
package com.czy4201b.fastinject.core.samples

import com.czy4201b.fastinject.core.fastInject

/**
 * 创建一个 js 变量例子
 */
fun sampleCreateVarUsage() {
    fastInject {
        val valueRef = createVar("(100 + 10) / 50 + 1")
        val valueRef2 = varOf("1 + 1")
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
        val valueRef2 = kVarOf(kInt)
        log(valueRef, "这是kotlin的计算结果：", valueRef2)
    }
}

/**
 * 包装一个 js 变量例子
 */
fun sampleWrapVarUsage() {
    fastInject {
        execJs("let cc = 520")
        val valueRef = wrapVar("cc")
        log("这是kotlin的包装结果：", valueRef)
    }
}

fun sampleReturnUsage() {
    fastInject {
        val valueRef = createVar("(100 + 10) / 50 + 1")
        ret(valueRef)
    }
}

fun sampleRaiseUsage() {
    fastInject {
        raise("错误了")
    }
}

fun sampleLogUsage() {
    fastInject {
        val valueRef = createVar("(100 + 10) / 50 + 1")
        log("这是一个日志，这是我的计算结果：", valueRef)
        warn("这是一个日志，这是我的计算结果：", valueRef)
        error("这是一个日志，这是我的计算结果：", valueRef)
        info("这是一个日志，这是我的计算结果：", valueRef)
        trace("这是一个日志，这是我的计算结果：", valueRef)
    }
}

fun sampleAttributionUsage() {
    fastInject {
        val container = findAll("css-main-container")
        log("这是一个日志，这是 container 的长度", container.size)
        container.forEach {
            log("这是一个日志，这是 container 的 value：", it.value)
            // 以下两者是等价的
            log("这是一个日志，这是 container 的 innerText：", it.text)
            log("这是一个日志，这是 container 的 innerText：", it.innerText)
        }
    }
}

fun sampleExecuteJsUsage() {
    fastInject {
        execJs("let hello_world = 1)")
        execJs("if (window.isReady)") {
            val btn = find("#submit")
            btn.click()
        }
        execJs("setTimeout(() =>", ", 1000)") {
            val btn = find("#submit")
            btn.click()
        }
    }
}

fun sampleExecuteIsolatedJsUsage() {
    fastInject {
        execIsolatedJs("let hello_world = 1")
        execIsolatedJs {
            val btn = find("#submit")
            btn.click()
        }
    }
}