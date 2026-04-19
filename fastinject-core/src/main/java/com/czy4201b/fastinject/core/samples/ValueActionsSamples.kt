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


fun sampleConditionUsage() {
    fastInject {
        val num = kVarOf(10)
        val num2 = kVarOf(50)
        condition(num gt num2) {
            log(num, "大于", num2)
        }.otherWiseIf(num eq num2) {
            log(num, "等于", num2)
        }.otherWise {
            log(num, "小于", num2)
        }

        condition("1 < 2") {
            log("1 < 2")
        }.otherWiseIf("1 === 2") {
            log("1 === 2")
        }.otherWise {
            log("1 > 2")
        }
    }
}

fun sampleOperationUsage() {
    fastInject {
        val num = kVarOf(10)
        val num2 = kVarOf(20)
        val num3 = num + num2 - num * num2 / num % num2
        val bool1 = num neq num3
        val bool2 = num ge num3
        val bool3 = num lt num3
        val bool4 = num le num3
        condition(bool1 or bool2 or bool3 or bool4) {
            log("wow~")
        }
    }
}