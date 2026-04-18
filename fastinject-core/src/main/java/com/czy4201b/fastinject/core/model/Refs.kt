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
package com.czy4201b.fastinject.core.model

sealed class JsRef(open val varName: String)

/**
 * 使用ElementRef封装value对象
 */
class ValueRef(varName: String) : JsRef(varName)

sealed class DomRef(
    override val varName: String,
    val selector: String
) : JsRef(varName)

/**
 * 使用ElementRef封装Dom对象
 */
class ElementRef(
    varName: String,
    selector: String
) : DomRef(varName, selector)

/**
 * 使用ElementRefs封装Dom对象数组
 */
class ElementsRef(
    varName: String,
    selector: String
) : DomRef(varName, selector)