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

internal object JsHelpers {
    val TIME_CHECK_HELPER = """
        window.fastInjectTimeCheckHelper = function(targetTs){
            if (!window.__FastInject_originalDate) {
                window.__originalDate = Date;
            }
            
            const RealDate = window.__FastInject_originalDate;
            window.__FastInject_dateOffset = targetTs - RealDate.now();
            
            if (!window.__FastInject_dateHacked) {
                console.warn('[FastInject] Date already hacked in page, skipping...');
                function FakeDate(...args) {
                    // 构造时直接给 RealDate 注入偏移后的时间值
                    if (this instanceof FakeDate) {
                        if (args.length === 0) {
                            return new RealDate(RealDate.now() + window.__FastInject_dateOffset);
                        } else {
                            return new RealDate(...args);
                        }
                    }
                    // 当作普通函数调用，如 Date()
                    return new RealDate(RealDate.now() + window.__FastInject_dateOffset).toString();
                }
                
                // 保持原型链完整
                Object.setPrototypeOf(FakeDate, RealDate);
                FakeDate.prototype = RealDate.prototype;
                
                FakeDate.now = () => RealDate.now() + window.__FastInject_dateOffset;
                FakeDate.parse = RealDate.parse;
                FakeDate.UTC   = RealDate.UTC;
                
                // 保留 toString，以减少被工具检测
                FakeDate.toString = RealDate.toString.bind(RealDate);
                
                // Hack now
                window.Date = FakeDate;
                window.__FastInject_dateHacked = true;

                window.fastInjectRestoreDate = () => {
                    window.Date = window.__FastInject_originalDate;
                    delete window.__FastInject_dateHacked;
                    delete window.__FastInject_dateOffset;
                };
            }
        };
    """.trimIndent()

    val TIME_HELPER = """
        window.fastInjectWaitForElement = function(selector, ms) {
            return new Promise(function (resolve, reject) {
                var start = Date.now();

                var el = document.querySelector(selector);
                if (el) { resolve(el); return; }

                var ob = new MutationObserver(function () {
                    var el2 = document.querySelector(selector);
                    if (el2) {
                        ob.disconnect();
                        resolve(el2);
                    } else if (Date.now() - start > ms) {
                        ob.disconnect();
                        reject('超时未找到 ' + selector);
                    }
                });

                ob.observe(document.body, { childList: true, subtree: true });

                setTimeout(function () {
                    ob.disconnect();
                    reject('超时未找到 ' + selector);
                }, ms);
            });
        };
    """.trimIndent()

    val SIMULATE_INPUT_HELPER = """
        window.fastInjectSimulateInput = function(el, value) {
            if (!el) {
                console.error('[FastInject] Target Element does not exist in JS context!');
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
            })(el, value);
        };
    """.trimIndent()

    val MEGA_CLICK_HELPER = """
        window.fastInjectMegaClick = function (el) {
            if (!el) return;

            const originalStyle = el.style.pointerEvents;
            el.style.pointerEvents = 'auto';
            el.style.setProperty('pointer-events', 'auto', 'important');
            
            if (el.hasAttribute('disabled')) {
                el.removeAttribute('disabled');
                el.disabled = false;
            }

            const rect = el.getBoundingClientRect();
            const x = rect.left + rect.width / 2;
            const y = rect.top + rect.height / 2;

            const topEl = document.elementFromPoint(x, y) || el;

            const clickable = topEl.closest('button, a, [role="button"], [onclick], input, summary') || topEl;

            const common = {
                bubbles: true,
                cancelable: true,
                composed: true,
                view: window,
                clientX: x,
                clientY: y,
                buttons: 1
            };

            const sequence = [
                new PointerEvent('pointerdown', { ...common, pointerType: 'mouse' }),
                new MouseEvent('mousedown', common),
                new PointerEvent('pointerup', { ...common, pointerType: 'mouse' }),
                new MouseEvent('mouseup', common),
                new MouseEvent('click', common)
            ];

            if (typeof clickable.focus === 'function') clickable.focus();
            
            sequence.forEach(evt => {
                clickable.dispatchEvent(evt);
                if (topEl !== clickable) topEl.dispatchEvent(evt);
            });

            // el.style.pointerEvents = originalStyle;
        };
    """.trimIndent()
}