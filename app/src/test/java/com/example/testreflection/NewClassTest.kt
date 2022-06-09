package com.example.testreflection

import org.junit.Assert.*
import org.junit.Test
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

class NewClassTest {

    @Test
    fun testReflection() {
        val kOldClass = OldClass::class
        val kNewClass = NewClass::class

        // prevent changing the class name and its location.
        // Note: use the Copy Reference from the IDE menu for your convenient.
        assertEquals(kOldClass.qualifiedName, "com.example.testreflection.OldClass")
        assertEquals(kNewClass.qualifiedName, "com.example.testreflection.NewClass")

        // basic check number of functions
        assertEquals(kOldClass.functions.size, kNewClass.functions.size)

        // basic check number of variables + functions
        assertEquals(kOldClass.members.size, kNewClass.members.size)

        // optionally check for the type of class and its basic properties
        assertTrue(kOldClass.isFinal)
        // such as checking if it is still the final class with the default constructor for example
        assertTrue(kOldClass.constructors.size == 1)
        assertTrue(kOldClass.constructors.first().parameters.isEmpty())

        // optional check for a reference that you can check these properties too
        assertFalse(kOldClass.isData)
        assertFalse(kOldClass.isCompanion)
        assertFalse(kOldClass.isAbstract)
        assertFalse(kOldClass.isSealed)

        // check the list of variables and its type if they are match
        val oldProps = kOldClass.memberProperties.toList()
        val newProps = kNewClass.memberProperties.toList()
        assertEquals(oldProps.size, newProps.size)
        for (i in oldProps.indices) {
            assertEquals(oldProps[i].name, newProps[i].name)
            assertEquals(oldProps[i].returnType, newProps[i].returnType)
        }

        // check the list of functions and its return type and parameters if they are match
        val oldFunc = kOldClass.functions.toList()
        val newFunc = kNewClass.functions.toList()
        assertEquals(oldFunc.size, newFunc.size)
        for (i in oldFunc.indices) {
            assertEquals(oldFunc[i].parameters.size, newFunc[i].parameters.size)
            assertEquals(oldFunc[i].returnType, newFunc[i].returnType)
            assertEquals(oldFunc[i].name, newFunc[i].name)
            // can also optional check for the type of each parameters but
            // I think it is unnecessary
        }
    }
}
