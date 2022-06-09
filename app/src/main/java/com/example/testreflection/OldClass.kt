package com.example.testreflection

class OldClass {
    private val i = 0
    private val s = ""

    fun method1() {
    }

    fun method2() {
    }

    fun method3() {
    }

    fun OldClass.eFunc(): Int = 0
}

class NewClass {
    private val s = ""
    private val i = 0

    fun method1() {
    }

    fun method2() {
    }

    fun method3() {
    }

    fun NewClass.eFunc(): Int = 0
}
