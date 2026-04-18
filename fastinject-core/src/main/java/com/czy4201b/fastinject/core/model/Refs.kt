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