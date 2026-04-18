# FastInject

[![](https://jitpack.io/v/SpeechlessMatt/FastInject.svg)](https://jitpack.io/#SpeechlessMatt/FastInject)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20JVM-green.svg)

🚀 FastInject 是一个轻量级 JavaScript 生成引擎。它允许你使用纯 Kotlin DSL 编写 Web 自动化逻辑，生成 JavaScript

FastInject-Core 是**纯 Kotlin 库**，可以轻易集成到 Kotlin/Java 项目中

FastInject-Android 扩展驱动支持 Android WebView、腾讯的 X5 WebView，可以通过扩展库的扩展函数 emit() 方法从 JavaScript 层获取数据

## 🌟 核心特性

- **类型引用**: ElementRef 和 ValueRef 确保了 DOM 节点与变量在 Kotlin 和 JS 之间的安全映射
- **声明式控制流**: 支持 forEach、ifExists 以及原生风格的集合操作
- **异步链**: 内置 waitElement、setTimeOut 及 then 回调，轻松应对动态加载的网页
- **自动化调试**: 集成 log、warn、info 等方法，自动对接浏览器控制台
- **环境隔离**: 自动生成 IIFE（立即执行函数），避免污染全局作用域

## 📦 安装

在你的根目录 settings.gradle 中添加 JitPack 仓库：

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

在模块级 build.gradle 中添加依赖：

```gradle
dependencies {
    // 核心 DSL 逻辑
    implementation "com.github.SpeechlessMatt.FastInject:fastinject-core:0.0.2"
    // Android WebView 扩展驱动
    implementation "com.github.SpeechlessMatt.FastInject:fastinject-android:0.0.2"
}
```

## 💻 示例代码

### 1. 基础 DOM 操作

```kotlin
fastInject {
    val container = find("#main-app")
    container.find(".submit-btn").click()
    
    // 模拟人手输入
    val userInput = createKVar("Hello FastInject!")
    find("#input-field").simulateInput(userInput)
}
```

### 2. 异步等待与回调

```kotlin
fastInject(onMessage = { data ->
    Log.d("FastInject", "收到回传: $data")
}) {
    // 等待元素出现（超时 5000ms）后执行逻辑
    waitElement(".dynamic-list", ms = 5000).then { list ->
        val firstItem = list.find(".item")
        firstItem.click()
    }
}
```

### 3. 循环处理

```kotlin
fastInject {
    findAll(".checkbox").forEach { check ->
        check.click()
    }
}
```

### 4. 条件处理

```kotlin
fastInject {
    val num = kValOf(10)
    val limit = kValOf(5)

    // 使用 condition 链式调用处理复杂的逻辑分支
    condition(num gt limit) {
        log("数值超限:", num)
    }.otherWiseIf(num eq limit) {
        log("数值刚好达标")
    }.otherWise {
        log("数值在安全范围内")
    }

    // 也可以直接使用原生 JS 表达式
    condition("window.innerWidth > 1080") {
        log("当前是桌面端布局")
    }
}
```

## 🛠 核心 API 概览

| 分类    | 常用方法 / 特性                                                |
|:------|:---------------------------------------------------------|
| 元素查找  | find, findAll, wrapElement, wrapElements                 |
| 变量管理  | kValOf, createVar, ValueRef                              |
| 算术运算  | +, -, *, /, % (支持 ValueRef 运算符重载)                        |
| 逻辑判断  | eq, neq, gt, ge, lt, le (中缀表达式)                          |
| 流程控制  | condition (if/else 链), ifExists, forEach, forEachIndexed |
| 属性获取  | size, text, innerText, value                             |
| 交互模拟  | click, simulateInput, scrollIntoView                     |
| 异步/定时 | waitElement, setTimeOut, then, disableTimeCheck          |
| 日志/注入 | log, execJs, execIsolatedJs                              |

## 📄 开源协议
本项目采用 Apache License 2.0