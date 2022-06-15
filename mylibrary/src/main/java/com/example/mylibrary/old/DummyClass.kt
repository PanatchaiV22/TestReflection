package com.example.mylibrary.old

import java.io.File

@Deprecated(
    message = "This class has been replaced with the Core implementation and will be removed in the next release. Please add any new code inside the Core module.",
    replaceWith = ReplaceWith("DummyClass()",
        "com.example.testreflection.dup"),
    level = DeprecationLevel.WARNING
)
class DummyClass {
    private val i = 0
    private val s = ""

    fun method1() {
    }

    fun method2() {
    }

    fun method3() {
    }
}
