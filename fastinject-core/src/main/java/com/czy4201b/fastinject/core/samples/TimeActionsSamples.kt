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
        setTimeOut(100)
            .then {
                log("你好吗？")
                condition("1 + 1 < 2") {
                    raise("我不好")
                }
            }
            .setTimeOut(100)
            .then {
                log("我好呀")
            }
            .catch {
                log(it)
            }
            .finally {
                log("一定会执行吧")
            }
    }
}