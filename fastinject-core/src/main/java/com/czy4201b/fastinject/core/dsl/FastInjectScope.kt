package com.czy4201b.fastinject.core.dsl

import com.czy4201b.fastinject.core.model.DelayRef
import com.czy4201b.fastinject.core.model.DomRef
import com.czy4201b.fastinject.core.model.JsRef
import com.czy4201b.fastinject.core.model.ValueRef
import com.czy4201b.fastinject.core.model.ElementRef
import com.czy4201b.fastinject.core.model.ElementsRef
import com.czy4201b.fastinject.core.model.TimeRef
import com.czy4201b.fastinject.core.utils.toJsLiteral

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class FastInjectDsl

@FastInjectDsl
class FastInjectScope {
    internal val jsFunc = mutableListOf<String>()
    internal val elementCache = mutableMapOf<String, ElementRef>()
    private var valueId = 0
    private var elementId = 0

    internal fun nextValueId() = "_val_fi_${valueId++}"
    internal fun nextElementId() = "_el_fi_${elementId++}"

    private val injectedHelpers = mutableSetOf<String>()

    internal fun ensureHelper(helperName: String, jsCode: String) {
        if (injectedHelpers.contains(helperName)) return

        execJs(jsCode)
        injectedHelpers.add(helperName)
    }

    /**
     * 创建js变量，并返回可操作的 `FastInject` 可操作的 [ValueRef] 对象
     *
     * 如果需要使用对象，请使用 [execJs] 自定义
     * @param jsExpression js 变量表达式
     * @sample com.czy4201b.fastinject.core.samples.sampleCreateVarUsage
     */
    fun createVar(jsExpression: String): ValueRef {
        val newVar = nextValueId()
        jsFunc += "let $newVar = $jsExpression;\n"
        return ValueRef(newVar)
    }

    /**
     * [createVar] 的别名
     *
     * 创建js变量，并返回可操作的 `FastInject` 可操作的 [ValueRef] 对象
     *
     * 注意：暂时只能支持基本类型 [String]、[Int]、[Boolean]、数组类型 [Map] ( [Map] 里面的类型也只能基本类型 )
     * 如果需要使用对象，请使用 [execJs] 自定义
     * @param jsExpression js 变量表达式
     * @sample com.czy4201b.fastinject.core.samples.sampleCreateVarUsage
     */
    fun valOf(jsExpression: String) = createVar(jsExpression)

    /**
     * 创建js变量，并返回可操作的 `FastInject` 可操作的 [ValueRef] 对象
     *
     * 注意：暂时只能支持基本类型 [String]、[Int]、[Boolean]、数组类型 [Map] ( [Map] 里面的类型也只能基本类型 )
     *
     * 如果需要使用对象，请使用 [execJs] 自定义
     * @param kValue kotlin 变量
     * @sample com.czy4201b.fastinject.core.samples.sampleCreateKValueVarUsage
     */
    fun createKVar(kValue: Any?): ValueRef {
        val jsLiteral = kValue.toJsLiteral()
        return createVar(jsLiteral)
    }

    /**
     * [createKVar] 的别名
     *
     * 创建js变量，并返回可操作的 `FastInject` 可操作的 [ValueRef] 对象
     *
     * 注意：暂时只能支持基本类型 [String]、[Int]、[Boolean]、数组类型 [Map] ( [Map] 里面的类型也只能基本类型 )
     * 如果需要使用对象，请使用 [execJs] 自定义
     * @param kValue kotlin 变量
     * @sample com.czy4201b.fastinject.core.samples.sampleCreateVarUsage
     */
    fun kValOf(kValue: Any?) = createKVar(kValue)

    /**
     * 打印日志 (log 级别)
     * @param messages 可以是 Kotlin 的 [String]、[Int]，也可以是 [ElementRef] 或 [ValueRef] 等 [JsRef] 的子类
     * @sample com.czy4201b.fastinject.core.samples.sampleLogUsage
     */
    fun log(vararg messages: Any) =
        performLog(this@FastInjectScope, messages, "log")

    /**
     * 打印日志 (warn 级别)
     * @param messages 可以是 Kotlin 的 [String]、[Int]，也可以是 [ElementRef] 或 [ValueRef] 等 [JsRef] 的子类
     * @sample com.czy4201b.fastinject.core.samples.sampleLogUsage
     */
    fun warn(vararg messages: Any) =
        performLog(this@FastInjectScope, messages, "warn")

    /**
     * 打印日志 (info 级别)
     * @param messages 可以是 Kotlin 的 [String]、[Int]，也可以是 [ElementRef] 或 [ValueRef] 等 [JsRef] 的子类
     * @sample com.czy4201b.fastinject.core.samples.sampleLogUsage
     */
    fun info(vararg messages: Any) =
        performLog(this@FastInjectScope, messages, "info")

    /**
     * 打印日志 (trace 级别)
     * @param messages 可以是 Kotlin 的 [String]、[Int]，也可以是 [ElementRef] 或 [ValueRef] 等 [JsRef] 的子类
     * @sample com.czy4201b.fastinject.core.samples.sampleLogUsage
     */
    fun trace(vararg messages: Any) =
        performLog(this@FastInjectScope, messages, "trace")

    /**
     * 插入一段原生 JavaScript 语句
     *
     * 用于直接注入不带逻辑块的单行或多行 JS 代码
     * @param js 要插入的原始 JavaScript 字符串
     */
    fun execJs(js: String) = performExecuteJs(this, js)

    /**
     * 插入一段带大括号 `{ ... }` 的 JavaScript 代码块
     *
     * 生成的结构为：`prefix { block }`
     *
     * 常用于生成 `if`、`while` 或简单的匿名函数
     * @param prefix 代码块的前缀，例如 if (condition)
     * @param block 在大括号内部执行的 DSL 逻辑
     */
    fun execJs(prefix: String, block: FastInjectScope.() -> Unit) =
        performExecuteJs(this, prefix, block)

    /**
     * 插入一段被前缀和后缀包裹的 JavaScript 代码块
     *
     * 生成的结构为：`prefix { block } suffix`
     *
     * 这是处理异步回调（如 `setTimeout` 或 `Promise.then`）的最通用方式
     * @param prefix 块的前缀，例如 "setTimeout(function()"
     * @param suffix 块的后缀，例如 ", 500);"
     * @param block 在大括号内部执行的 DSL 逻辑
     */
    fun execJs(prefix: String, suffix: String, block: FastInjectScope.() -> Unit) =
        performExecuteJs(this, prefix, suffix, block)

    /**
     * 运行在匿名函数中的js，不会污染全局变量，相对更加安全
     * @param js 原生代码
     * @sample com.czy4201b.fastinject.core.samples.sampleExecuteIsolatedJsUsage
     */
    fun execIsolatedJs(js: String) = performExecuteIsolatedJs(this, js)


    /* --------------------------------- DomActions ---------------------------------
     * 操作 Dom 元素的方法合集
     */

    /**
     * 获取元素集合的长度
     * @receiver [ElementsRef] 目标集合引用
     * @return [ValueRef] 指向 JS 侧 `length` 属性的实时变量引用
     * @sample com.czy4201b.fastinject.core.samples.sampleAttributionUsage
     */
    val ElementsRef.size: ValueRef get() = getElementsSize(this@FastInjectScope, this)

    /**
     * 获取元素的文本内容 (含 HTML 标签内的纯文字)
     * @receiver [ElementsRef] 目标集合引用
     * @return [ValueRef] 指向 JS 侧 `length` 属性的实时变量引用
     * @sample com.czy4201b.fastinject.core.samples.sampleAttributionUsage
     */
    val ElementRef.text: ValueRef get() = getElementText(this@FastInjectScope, this)

    /**
     * `text` 的别名，符合js开发者的习惯
     * 获取元素的文本内容 (含 HTML 标签内的纯文字)
     * @receiver [ElementsRef] 目标集合引用
     * @return [ValueRef] 指向 JS 侧 `length` 属性的实时变量引用
     * @sample com.czy4201b.fastinject.core.samples.sampleAttributionUsage
     */
    val ElementRef.innerText: ValueRef get() = text


    /**
     * 在整个页面范围内查找第一个符合选择器的元素
     * @param selector CSS 选择器
     * @sample com.czy4201b.fastinject.core.samples.sampleFindUsage
     */
    fun find(selector: String): ElementRef =
        performFindElement(this, selector)

    /**
     * 在整个页面范围内查找所有符合条件的元素集合
     * @param selector CSS 选择器
     * @sample com.czy4201b.fastinject.core.samples.sampleFindAllUsage
     */
    fun findAll(selector: String): ElementsRef =
        performFindAllElements(this, selector)

    /**
     * 获取元素集合中第 `index` 个元素
     * @return [ElementRef] 查找到的第一个子元素引用
     */
    operator fun ElementsRef.get(index: Int): ElementRef =
        performGetElement(this@FastInjectScope, this, index)

    /**
     * 在当前元素范围内进行局部查找
     * @param selector CSS 选择器字符串
     * @return [ElementRef] 查找到的第一个子元素引用
     * @sample com.czy4201b.fastinject.core.samples.sampleElementFindUsage
     */
    fun ElementRef.find(selector: String): ElementRef =
        performFindElement(this@FastInjectScope, this, selector)

    /**
     * 在当前元素范围内进行局部查找
     * @param selector CSS 选择器字符串
     * @return [ElementRef] 查找到的子元素集合
     * @sample com.czy4201b.fastinject.core.samples.sampleElementFindAllUsage
     */
    fun ElementRef.findAll(selector: String): ElementsRef =
        performFindAllElements(this@FastInjectScope, this, selector)

    /**
     * 将自定义的变量名转换为 FastInject 可操作的 ElementsRef 对象
     * @param varName 变量名字
     * @param selectorHint 可选的变量注释
     * @sample com.czy4201b.fastinject.core.samples.sampleWrapElementUsage
     */
    fun wrapElement(varName: String, selectorHint: String? = null) =
        performWrapElement(this@FastInjectScope, varName, selectorHint)

    /**
     * 将自定义的变量名转换为 FastInject 可操作的 ElementRef 对象
     * @param varName 变量名字
     * @param selectorHint 可选的变量注释
     * @sample com.czy4201b.fastinject.core.samples.sampleWrapElementsUsage
     */
    fun wrapElements(varName: String, selectorHint: String? = null) =
        performWrapElements(this@FastInjectScope, varName, selectorHint)


    /**
     * 模拟用户点击行为
     * @sample com.czy4201b.fastinject.core.samples.sampleFindUsage
     */
    fun ElementRef.click() = performClick(this@FastInjectScope, this)

    /**
     * 模拟人手输入：可以解决大部分普通 `input` 无法解决的问题
     * @param text 输入的文本
     * @sample com.czy4201b.fastinject.core.samples.sampleElementsSimulateInputTextUsage
     */
    fun ElementRef.simulateInput(text: String) =
        performSimulateInput(this@FastInjectScope, this, text)

    /**
     * 模拟人手输入：可以解决大部分 `input` 无法解决的问题
     * @param textRef 指向js字符串的 [ValueRef]
     * @sample com.czy4201b.fastinject.core.samples.sampleElementsSimulateInputRefUsage
     */
    fun ElementRef.simulateInput(textRef: ValueRef) =
        performSimulateInput(this@FastInjectScope, this, textRef)


    /**
     * 检查元素是否存在，存在就执行 block
     * @param block 执行的代码块
     * @sample com.czy4201b.fastinject.core.samples.sampleElementsExistsUsage
     */
    fun ElementRef.ifExists(block: FastInjectScope.(ElementRef) -> Unit) =
        performIfElementExists(this@FastInjectScope, this, block)

    /**
     * 遍历元素集合，每个元素执行 block
     * @param block 遍历逻辑，`it` 为当前的 [ElementRef]
     * @sample com.czy4201b.fastinject.core.samples.sampleFindAllUsage
     */
    fun ElementsRef.forEach(block: FastInjectScope.(ElementRef) -> Unit) =
        performForEach(this@FastInjectScope, this, block)

    /**
     * 遍历元素集合，每个元素执行 block
     * @param block 遍历逻辑，提供元素引用 [ElementRef] 和下标引用 [ValueRef]
     */
    fun ElementsRef.forEachIndexed(block: FastInjectScope.(ElementRef, ValueRef) -> Unit) =
        performForEachIndexed(this@FastInjectScope, this, block)


    /* --------------------------------- TimeActions ---------------------------------
     * 与时间相关的方法合集
     */

    /**
     * 创建一个指定毫秒数的延迟引用
     *
     * 此函数不会立即产生阻塞效果，需配合 [.then] 使用来定义延迟后的操作
     *
     * 常用于等待页面动画完成、模拟人工操作间隔或处理简单的异步时序
     * @param ms 延迟的时间，单位为毫秒（1000ms = 1s）
     * @return 返回一个 [DelayRef] 句柄，用于链式调用 [then]
     * @sample com.czy4201b.fastinject.core.samples.sampleSetTimeoutUsage
     */
    fun setTimeOut(ms: Int) = performSetTimeOut(ms)

    /**
     * 注册延迟时间到达后的回调逻辑
     *
     * 生成的 JavaScript 底层使用 `setTimeout` 实现
     *
     * 在 [block] 内部，代码将运行在当前的 [FastInjectScope] 作用域下
     * @param block 延迟结束后需要执行的代码块
     * @sample com.czy4201b.fastinject.core.samples.sampleSetTimeoutUsage
     */
    fun DelayRef.then(block: FastInjectScope.() -> Unit) =
        performSetTimeOutThen(this@FastInjectScope, block)

    /**
     * 等待 Element 的出现，是异步操作
     * @param ms 等待的超时时间（毫秒）
     * @return 返回一个新的 [TimeRef]，用于链式调用或引用等待状态
     * @sample com.czy4201b.fastinject.core.samples.sampleWaitElementUsage
     */
    fun waitElement(selector: String, ms: Int) =
        performWaitElement(this@FastInjectScope, selector, ms)

    /**
     * 基于当前的 DomRef 实例等待元素出现，是异步操作
     * @param ms 等待的超时时间（毫秒）
     * @return 返回一个新的 [TimeRef]，用于链式调用或引用等待状态
     * @sample com.czy4201b.fastinject.core.samples.sampleWaitElementUsage
     */
    fun DomRef.waitElement(ms: Int) =
        performWaitElement(this@FastInjectScope, this.selector, ms)

    /**
     * 当元素成功加载/出现后的回调处理
     *
     * 此方法允许在 [TimeRef] 等待任务完成后，通过 DSL 块继续对找到的元素进行操作
     *
     * 在 [block] 内部，`this` 将指向当前的 [FastInjectScope]，并提供一个 [ElementRef] 参数代表捕获到的 DOM 元素
     * @param block 执行逻辑块，包含对目标元素的操作
     */
    fun TimeRef.then(block: FastInjectScope.(ElementRef) -> Unit) =
        performWaitElementThen(this@FastInjectScope, block)


    /* --------------------------------- UtilActions ---------------------------------
     * 有用的工具合集
     */

    /**
     * 通过伪造当前时间的方式禁用时间检查
     * @param toDate 设置伪造时间戳
     * @sample com.czy4201b.fastinject.core.samples.sampleDisableTimeCheckUsage
     */
    fun disableTimeCheck(toDate: Long) = performDisableTimeCheck(this@FastInjectScope, toDate)

    /**
     * 恢复所伪造的时间以重新启用时间检查
     * @sample com.czy4201b.fastinject.core.samples.sampleDisableTimeCheckUsage
     */
    fun restoreTimeCheck() = performRestoreTimeCheck(this@FastInjectScope)


    fun build(): String {
        if (jsFunc.isEmpty()) {
            return ""
        }

        // 将所有 DSL 收集的 JavaScript 拼成一个脚本
        val finalJs = "(function() {\n${jsFunc.joinToString("\n")}\n})();"
        return finalJs
    }
}