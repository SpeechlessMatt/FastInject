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
}