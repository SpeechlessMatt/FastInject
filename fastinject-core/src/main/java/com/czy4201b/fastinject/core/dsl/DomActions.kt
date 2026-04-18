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
        scope.jsFunc += """
            const $childVar = ${parent.varName}[$index];
            if (!$childVar) {
                console.warn('[FastInject] Index $index out of bounds for "${parent.selector}", length: ${parent.varName}.length');
            }
        """.trimIndent()
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
        scope.jsFunc += """
            const $childVar = ${parent.varName}[${indexRef.varName}];
            if (!$childVar) {
                console.warn('[FastInject] Index ' + ${indexRef.varName} + ' out of bounds for "${parent.selector}", length: ${parent.varName}.length');
            }
        """.trimIndent()
        ElementRef(childVar, "${parent.selector}[${indexRef.varName}]")
    }
}

/**
 * 获取数组长度
 */
internal fun getElementsSize(scope: FastInjectScope, parent: ElementsRef): ValueRef {
    val sizeVar = scope.nextValueId()
    scope.jsFunc += """
        const $sizeVar = ${parent.varName}.length;
    """.trimIndent()

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
    scope.jsFunc += """
        const $elementVar = ${parent.varName}.querySelector("$selector");
        if (!$elementVar) {
            console.warn('[FastInject] Element ${parent.varName}.querySelector("$selector") does not exist in JS context!');
        }
    """.trimIndent()

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
    scope.jsFunc += """
        const $elementVar = document.querySelector("$selector");
        if (!$elementVar) {
            console.warn('[FastInject] Element document.querySelector("$selector") does not exist in JS context!');
        }
    """.trimIndent()

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
    scope.jsFunc += """
        const $elementVar = ${parent.varName}.querySelectorAll("$selector");
        if (!$elementVar) {
            console.warn('[FastInject] Element ${parent.varName}.querySelectorAll("$selector") does not exist in JS context!');
        }
    """.trimIndent()

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
    scope.jsFunc += """
        const $elementVar = document.querySelectorAll("$selector");
        if (!$elementVar) {
            console.warn('[FastInject] Element document.querySelectorAll("$selector") does not exist in JS context!');
        }
    """.trimIndent()

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
    scope.jsFunc += """
        (function(){
            try {
                if (typeof $varName === 'undefined') {
                    console.error('[FastInject] Variable "$varName" does not exist in JS context!');
                }
            } catch (e) {
                console.error('[FastInject] wrapElement("$varName") failed: ' + e);
            }
        })();
    """.trimIndent()

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
    scope.jsFunc += """
        (function(){
            try {
                if (typeof $varName === 'undefined') {
                    console.error('[FastInject] Variable "$varName" does not exist in JS context!');
                }
            } catch (e) {
                console.error('[FastInject] wrapElement("$varName") failed: ' + e);
            }
        })();
    """.trimIndent()

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
    val countVar = scope.nextValueId()
    val elementVar = scope.nextElementId()
    val indexVar = "_fi_idx_${scope.nextValueId()}"

    scope.jsFunc += """
        const $countVar = ${parent.varName}.length;
        for (let $indexVar = 0; $indexVar < $countVar; $indexVar++) {
            const $elementVar = ${parent.varName}[$indexVar];
    """.trimIndent()

    val loopRef = ElementRef(elementVar, "${parent.selector}[$indexVar]")
    scope.block(loopRef)

    scope.jsFunc += "}\n"
}

/**
 * 遍历数组，每个元素执行 block
 */
internal fun performForEachIndexed(
    scope: FastInjectScope,
    parent: ElementsRef,
    block: FastInjectScope.(ElementRef, ValueRef) -> Unit
) {
    val countVar = scope.nextValueId()
    val elementVar = scope.nextElementId()
    val indexVar = "_fi_idx_${scope.nextValueId()}"

    scope.jsFunc += """
        const $countVar = ${parent.varName}.length;
        for (let $indexVar = 0; $indexVar < $countVar; $indexVar++) {
            const $elementVar = ${parent.varName}[$indexVar];
    """.trimIndent()

    val loopRef = ElementRef(elementVar, "${parent.selector}[$indexVar]")
    val indexRef = ValueRef(indexVar)
    scope.block(loopRef, indexRef)

    scope.jsFunc += "}\n"
}

/**
 * 模拟人手输入：可以解决大部分 input 无法解决的问题
 * @param textRef 拥有 String 的valueRef
 */
internal fun performSimulateInput(scope: FastInjectScope, parent: ElementRef, textRef: ValueRef) {
    scope.jsFunc += """
        (function() {
            const el = ${parent.varName};
            if (!el) {
                console.error('[FastInject] Element ${parent.varName} does not exist in JS context!');
                return;
            }
            
            (function (el, value) {
                if (!el) return;
    
                el.focus(); // 保证组件内部 focus 逻辑生效
    
                // 清空并准备 tracker
                const lastValue = el.value;
                el.value = '';
    
                const tracker = el._valueTracker;
                if (tracker) tracker.setValue(lastValue);
    
                // 模拟中文输入（IME）
                el.dispatchEvent(new CompositionEvent('compositionstart', { bubbles: true, composed: true }));
    
                // 填入值
                el.value = value;
    
                el.dispatchEvent(new CompositionEvent('compositionend', { bubbles: true, composed: true, data: value }));
    
                // 触发 React/Vue 的 input 事件
                el.dispatchEvent(new InputEvent('input', { bubbles: true, composed: true, data: value, inputType: 'insertText' }));
    
                // 触发 change + blur 保证校验逻辑触发
                el.dispatchEvent(new Event('change', { bubbles: true, composed: true }));
                el.blur();
            })(el, ${textRef.varName});
        })();
    """.trimIndent()
}

/**
 * 模拟人手输入：可以解决大部分 input 无法解决的问题
 * @param text 输入的文本
 */
internal fun performSimulateInput(scope: FastInjectScope, parent: ElementRef, text: String) {
    val escaped = text.toJsLiteral()

    scope.jsFunc += """
        (function() {
            const el = ${parent.varName};
            if (!el) {
                console.error('[FastInject] Element ${parent.varName} does not exist in JS context!');
                return;
            }
            
            (function (el, value) {
                if (!el) return;
    
                el.focus(); // 保证组件内部 focus 逻辑生效
    
                // 清空并准备 tracker
                const lastValue = el.value;
                el.value = '';
    
                const tracker = el._valueTracker;
                if (tracker) tracker.setValue(lastValue);
    
                // 模拟中文输入（IME）
                el.dispatchEvent(new CompositionEvent('compositionstart', { bubbles: true, composed: true }));
    
                // 填入值
                el.value = value;
    
                el.dispatchEvent(new CompositionEvent('compositionend', { bubbles: true, composed: true, data: value }));
    
                // 触发 React/Vue 的 input 事件
                el.dispatchEvent(new InputEvent('input', { bubbles: true, composed: true, data: value, inputType: 'insertText' }));
    
                // 触发 change + blur 保证校验逻辑触发
                el.dispatchEvent(new Event('change', { bubbles: true, composed: true }));
                el.blur();
            })(el, $escaped);
        })();
    """.trimIndent()
}

internal fun performClick(scope: FastInjectScope, parent: ElementRef) {
    scope.jsFunc += """
        (function() {
            const el = ${parent.varName};
            if (!el) {
                console.error('[FastInject] Element ${parent.varName} does not exist in JS context!');
                return;
            }
    """.trimIndent()
}

/**
 * 获取元素文本，相当于document.querySelector(selector).textContent
 */
internal fun getElementText(scope: FastInjectScope, parent: ElementRef): ValueRef {
    val newVar = scope.createVar("${parent.varName}.textContent")
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
    scope.jsFunc += "if (${parent.varName}) {"
    scope.block(parent)
    scope.jsFunc += "\n}"
}