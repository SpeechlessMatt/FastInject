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

import com.czy4201b.fastinject.core.dsl.JsHelpers.MEGA_CLICK_HELPER
import com.czy4201b.fastinject.core.dsl.JsHelpers.SIMULATE_INPUT_HELPER
import com.czy4201b.fastinject.core.model.ElementRef
import com.czy4201b.fastinject.core.model.ElementsRef
import com.czy4201b.fastinject.core.model.ValueRef
import com.czy4201b.fastinject.core.utils.toJsLiteral

/**
 * 获取数组元素
 */
internal fun performGetElement(
    scope: FastInjectScope,
    parent: ElementsRef,
    index: Int
): ElementRef {
    // 生成 cacheKey 如果缓存有 就不创建了
    val cacheKey = "${parent.varName}_fi_idx_$index"

    return scope.elementCache.getOrPut(cacheKey) {
        val childVar = scope.nextElementId()
        scope.execJs("const $childVar = ${parent.varName}[$index];")
        scope.condition("!$childVar") {
            warn(
                "Index $index out of bounds for",
                parent.selector.toJsLiteral(),
                ", length:",
                "${parent.varName}.length"
            )
        }
        ElementRef(childVar, "${parent.selector}[$index]")
    }
}

/**
 * 获取数组元素
 */
internal fun performGetElement(
    scope: FastInjectScope,
    parent: ElementsRef,
    indexRef: ValueRef
): ElementRef {
    // 生成 cacheKey 如果缓存有 就不创建了
    val cacheKey = "${parent.varName}_fi_idx_${indexRef.varName}"

    return scope.elementCache.getOrPut(cacheKey) {
        val childVar = scope.nextElementId()
        scope.execJs("const $childVar = ${parent.varName}[${indexRef.varName}];")
        scope.condition("!$childVar") {
            warn(
                "Index",
                indexRef,
                "out of bounds for",
                parent.selector.toJsLiteral(),
                "length: ${parent.varName}.length"
            )
        }
        ElementRef(childVar, "${parent.selector}[${indexRef.varName}]")
    }
}

/**
 * 获取数组长度
 */
internal fun getElementsSize(scope: FastInjectScope, parent: ElementsRef): ValueRef {
    val sizeVar = scope.nextValueId()
    scope.execJs("const $sizeVar = ${parent.varName}.length;")

    return ValueRef(sizeVar)
}

/**
 * 在局部范围内查找 Dom 元素，相当于 element.querySelector(selector)
 */
internal fun performFindElement(
    scope: FastInjectScope,
    parent: ElementRef,
    selector: String
): ElementRef {
    val elementVar = scope.nextElementId()
    scope.execJs("const $elementVar = ${parent.varName}.querySelector(${selector.toJsLiteral()});")
    scope.condition("!$elementVar") {
        warn(
            "Element ${parent.varName}.querySelector(",
            selector,
            ") does not exist in JS context!"
        )
    }

    return ElementRef(elementVar, selector)
}

/**
 * 在全局范围内查找 Dom 元素，相当于 document.querySelector(selector)
 */
internal fun performFindElement(
    scope: FastInjectScope,
    selector: String
): ElementRef {
    val elementVar = scope.nextElementId()
    scope.execJs("const $elementVar = document.querySelector(${selector.toJsLiteral()});")
    scope.condition("!$elementVar") {
        warn("Element document.querySelector(", selector, ") does not exist in JS context!")
    }

    return ElementRef(elementVar, selector)
}

/**
 * 在局部范围内查找 Dom 元素，相当于 element.querySelectorAll(selector)
 */
internal fun performFindAllElements(
    scope: FastInjectScope,
    parent: ElementRef,
    selector: String
): ElementsRef {
    val elementVar = scope.nextElementId()
    scope.execJs("const $elementVar = ${parent.varName}.querySelectorAll(${selector.toJsLiteral()});")
    scope.condition("!$elementVar") {
        warn(
            "Element ${parent.varName}.querySelectorAll(",
            selector,
            ") does not exist in JS context!"
        )
    }

    return ElementsRef(elementVar, selector)
}

/**
 * 在全局范围内查找 Dom 元素，相当于 document.querySelectorAll(selector)
 */
internal fun performFindAllElements(
    scope: FastInjectScope,
    selector: String
): ElementsRef {
    val elementVar = scope.nextElementId()
    scope.execJs("const $elementVar = document.querySelectorAll(${selector.toJsLiteral()});")
    scope.condition("!$elementVar") {
        warn("Element document.querySelectorAll(", selector, ") does not exist in JS context!")
    }

    return ElementsRef(elementVar, selector)
}

/**
 * 将自定义的变量名转换为FastInject可操作的ElementRef对象
 * @param varName 变量名字
 * @param selectorHint 可选的变量注释
 */
internal fun performWrapElement(
    scope: FastInjectScope,
    varName: String,
    selectorHint: String? = null
): ElementRef {
    scope.execIsolatedJs {
        // try
        execJs("try") {
            condition("typeof $varName === 'undefined' || $varName === null") {
                error("Variable", varName, "does not exist in JS context!")
            }
        }
        execJs("catch (e)") {
            error("wrapElement(", varName, ") failed: ", wrapVar("e"))
        }
    }

    return ElementRef(varName, selectorHint ?: "[External Elements]")
}

/**
 * 将自定义的变量名转换为 FastInject 可操作的 ElementsRef 对象
 * @param varName 变量名字
 * @param selectorHint 可选的变量注释
 */
internal fun performWrapElements(
    scope: FastInjectScope,
    varName: String,
    selectorHint: String? = null
): ElementsRef {
    scope.execIsolatedJs {
        // try
        execJs("try") {
            condition("typeof $varName === 'undefined' || $varName === null") {
                error("Variable", varName, "does not exist in JS context!")
            }
        }
        execJs("catch (e)") {
            error("wrapElements(", varName, ") failed: ", wrapVar("e"))
        }
    }

    return ElementsRef(varName, selectorHint ?: "[External Elements]")
}

/**
 * 遍历数组，每个元素执行 block
 */
internal fun performForEach(
    scope: FastInjectScope,
    parent: ElementsRef,
    block: FastInjectScope.(ElementRef) -> Unit
) {
    val elementVar = scope.nextElementId()
    val indexVar = "_fi_idx_${scope.nextValueId()}"

    scope.execJs("for (let $indexVar = 0; $indexVar < ${parent.varName}.length; $indexVar++)") {
        execJs("const $elementVar = ${parent.varName}[$indexVar];")
        val loopRef = ElementRef(elementVar, "${parent.selector}[$indexVar]")
        block(loopRef)
    }
}

/**
 * 遍历数组，每个元素执行 block
 */
internal fun performForEachIndexed(
    scope: FastInjectScope,
    parent: ElementsRef,
    block: FastInjectScope.(ElementRef, ValueRef) -> Unit
) {
    val elementVar = scope.nextElementId()
    val indexVar = "_fi_idx_${scope.nextValueId()}"

    scope.execJs("for (let $indexVar = 0; $indexVar < ${parent.varName}.length; $indexVar++)") {
        execJs("const $elementVar = ${parent.varName}[$indexVar];")
        val loopRef = ElementRef(elementVar, "${parent.selector}[$indexVar]")
        val indexRef = ValueRef(indexVar)
        block(loopRef, indexRef)
    }
}

internal fun performInput(scope: FastInjectScope, parent: ElementRef, textRef: ValueRef) {
    scope.execIsolatedJs {
        execJs("const el = ${parent.varName};")
        condition("!el") {
            error("Element", parent.varName, "does not exist in JS context!")
            // return to end the (function() {})();
            execJs("return;")
        }
        execJs("el.value = ${textRef.varName};")
        execJs("el.dispatchEvent(new Event('input', { bubbles: true }));")
        execJs("el.dispatchEvent(new Event('change', { bubbles: true }));")
    }
}

internal fun performInput(scope: FastInjectScope, parent: ElementRef, text: String) {
    val textRef = scope.kVarOf(text)
    performInput(scope, parent, textRef)
}

/**
 * 模拟人手输入：可以解决大部分 input 无法解决的问题
 * @param textRef 拥有 String 的valueRef
 */
internal fun performSimulateInput(scope: FastInjectScope, parent: ElementRef, textRef: ValueRef) {
    scope.ensureHelper("SimulateInputHelper", SIMULATE_INPUT_HELPER)
    scope.execJs("fi_ctx.fastInjectSimulateInput(${parent.varName}, ${textRef.varName});")
}

/**
 * 模拟人手输入：可以解决大部分 input 无法解决的问题
 * @param text 输入的文本
 */
internal fun performSimulateInput(scope: FastInjectScope, parent: ElementRef, text: String) {
    val textRef = scope.kVarOf(text)
    performSimulateInput(scope, parent, textRef)
}

internal fun performEnter(scope: FastInjectScope, parent: ElementRef) {
    scope.execIsolatedJs {
        execJs("const el = ${parent.varName};")
        condition("!el") {
            error("Element", parent.varName, "does not exist in JS context!")
            // return to end the (function() {})();
            execJs("return;")
        }
        execJs("const enterEvent = new KeyboardEvent('keydown', {key: 'Enter', code: 'Enter', keyCode: 13, which: 13, bubbles: true, cancelable: true});")
        execJs("el.dispatchEvent(enterEvent);")
    }
}

internal fun performClick(scope: FastInjectScope, parent: ElementRef) {
    scope.execIsolatedJs {
        execJs("const el = ${parent.varName};")
        condition("!el") {
            error("Element", parent.varName, "does not exist in JS context!")
            // return to end the (function() {})();
            execJs("return;")
        }
        execJs("el.click();")
    }
}

internal fun performMegaClick(scope: FastInjectScope, parent: ElementRef) {
    scope.ensureHelper("MegaClickHelper", MEGA_CLICK_HELPER)
    scope.execJs("fi_ctx.fastInjectMegaClick(${parent.varName});")
}

/**
 * 获取元素文本，相当于document.querySelector(selector).textContent
 */
internal fun getElementText(scope: FastInjectScope, parent: ElementRef): ValueRef {
    val newVar = scope.createVar("${parent.varName}.textContent")
    return newVar
}

/**
 * 获取元素的值，相当于document.querySelector(selector).value
 */
internal fun getElementValue(scope: FastInjectScope, parent: ElementRef): ValueRef {
    val newVar = scope.createVar("${parent.varName}.value")
    return newVar
}

/**
 * 检查元素是否存在，存在就执行 block
 */
internal fun performIfElementExists(
    scope: FastInjectScope,
    parent: ElementRef,
    block: FastInjectScope.(ElementRef) -> Unit
) {
    scope.condition(parent.varName) {
        block(parent)
    }
}