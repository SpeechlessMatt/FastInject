package com.czy4201b.fastinject.core.samples

import com.czy4201b.fastinject.core.fastInject


/**
 * 在整个页面范围内查找第一个符合选择器的元素例子
 */
fun sampleFindUsage() {
    fastInject {
        val container = find("css-main-container")
        container.click()
    }
}


/**
 * 在整个页面范围内查找所有符合条件的元素集合例子
 */
fun sampleFindAllUsage() {
    fastInject {
        val container = findAll("css-main-container")
        container.forEach {
            it.click()
        }
    }
}

/**
 * 在当前元素范围内进行局部查找元素例子
 */
fun sampleElementFindUsage() {
    fastInject {
        val container = find("css-main-container")
        // 这就是 ElementRef.find() 的用法
        val subContainer = container.find("css-sub-container")
        subContainer.click()
    }
}

/**
 * 在当前元素范围内进行局部查找元素集合例子
 */
fun sampleElementFindAllUsage() {
    fastInject {
        val container = find("css-main-container")
        // 这就是 ElementRef.findAll() 的用法
        val subContainer = container.findAll("css-sub-container")
        subContainer.forEach {
            it.click()
        }
    }
}

/**
 * 模拟人手输入例子：使用 ValueRef 作为参数
 */
fun sampleElementsSimulateInputRefUsage() {
    fastInject {
        val container = findAll("css-main-container")
        container.forEachIndexed { elementRef, valueRef ->
            elementRef.simulateInput(valueRef)
        }
    }
}

/**
 * 模拟人手输入例子：使用 text 作为参数
 */
fun sampleElementsSimulateInputTextUsage() {
    fastInject {
        val container = findAll("css-main-container")
        container.forEach {
            it.simulateInput("hello")
        }
    }
}

/**
 * 检查包存在例子
 */
fun sampleElementsExistsUsage() {
    fastInject {
        val container = find("css-main-container")
        container.ifExists {
            it.click()
        }
    }
}

/**
 * wrapElement例子
 */
fun sampleWrapElementUsage() {
    fastInject {
        // 假设你在上文手动注入了一个名字为url的dom元素
        execJs("const url = document.querySelector('css-main-container')")
        val container = wrapElement("url", "这是一个名字为的dom元素")
        container.click()
    }
}

/**
 * wrapElements例子
 */
fun sampleWrapElementsUsage() {
    fastInject {
        // 假设你在上文手动注入了一个名字为url的dom元素集合
        execJs("const urls = document.querySelectorAll('css-main-container')")
        val containers = wrapElements("urls", "这是一个名字为的dom元素集合")
        containers.forEach {
            it.click()
        }
    }
}