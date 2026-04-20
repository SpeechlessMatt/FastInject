package com.czy4201b.example

import com.czy4201b.fastinject.core.fastInject
import java.io.File

fun main() {
    val js = fastInject {
        disableTimeCheck(1779201313000)
        find(".navbar-toggler").click()
        waitElement("#docsearch-1 > button", 5000)
            .then { button ->
                button.click()
            }
            .waitElement("#docsearch-input", 5000)
            .then { inputField ->
                inputField.simulateInput("Driver Sessions")
                setTimeOut(2000).then {
                    inputField.enter()
                }
            }
            .setTimeOut(500)
            .then {
                restoreTimeCheck()
            }
            .catch {
                log("错误了:", it)
            }
            .finally {
                log("必然执行的地方")
            }
    }

    val outputFile = File("example/src/main/java/com/czy4201b/example/generated_script.js")
    outputFile.writeText(js)
    println("✅ JS 脚本已保存至: ${outputFile.absolutePath}")
}