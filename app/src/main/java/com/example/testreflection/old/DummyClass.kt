package com.example.testreflection.old

@Deprecated(
    message = "This class has been replaced with the Core implementation and will be removed in the next release. Please add any new code inside the Core module.",
    replaceWith = ReplaceWith(
        "DummyClass()",
        "com.example.mylibrary.dup.DummyClass"
    ),
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
