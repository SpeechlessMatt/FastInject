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

import com.czy4201b.fastinject.core.model.ConditionRef
import com.czy4201b.fastinject.core.model.ValueRef


/**
 * 基础计算执行器
 */
internal fun ValueRef.performOp(scope: FastInjectScope, op: String, other: Any): ValueRef {
    val resultVar = scope.nextValueId()
    val otherValue = when (other) {
        is ValueRef -> other.varName
        is String -> "'$other'"
        else -> other.toString()
    }

    scope.jsFunc += "const $resultVar = ${this.varName} $op $otherValue;"

    return ValueRef(resultVar)
}

fun performCondition(
    scope: FastInjectScope,
    condition: ValueRef,
    block: FastInjectScope.() -> Unit
): ConditionRef {
    scope.execJs("if (${condition.varName})", block)
    return ConditionRef(true)
}

fun performCondition(
    scope: FastInjectScope,
    js: String,
    block: FastInjectScope.() -> Unit
): ConditionRef {
    scope.execJs("if ($js)", block)
    return ConditionRef(true)
}

fun ConditionRef.performOtherwise(
    scope: FastInjectScope,
    block: FastInjectScope.() -> Unit
): ConditionRef {
    check(isActive) {
        "[FastInject] Invalid 'otherwiseIf': The condition chain has been broken or already closed."
    }
    scope.execJs("else", block)
    return this
}

fun ConditionRef.performOtherwiseIf(
    scope: FastInjectScope,
    condition: ValueRef,
    block: FastInjectScope.() -> Unit
): ConditionRef {
    check(isActive) {
        "[FastInject] Invalid 'otherwise': The condition chain has been broken or already closed."
    }
    scope.execJs("else if (${condition.varName})", block)

    this.isActive = false
    return this
}

fun ConditionRef.performOtherwiseIf(
    scope: FastInjectScope,
    js: String,
    block: FastInjectScope.() -> Unit
): ConditionRef {
    check(isActive) {
        "[FastInject] Invalid 'otherwise': The condition chain has been broken or already closed."
    }
    scope.execJs("else if ($js)", block)

    this.isActive = false
    return this
}