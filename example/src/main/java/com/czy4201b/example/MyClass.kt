package com.czy4201b.example

import com.czy4201b.fastinject.core.fastInject
import java.io.File

fun main() {
    val js = fastInject {
        disableTimeCheck(1779201313000)
        waitElement("container", 5000).then {
            it.megaClick()
        }
    }

    val outputFile = File("example/src/main/java/com/czy4201b/example/generated_script.js")
    outputFile.writeText(js)
    println("✅ JS 脚本已保存至: ${outputFile.absolutePath}")
}