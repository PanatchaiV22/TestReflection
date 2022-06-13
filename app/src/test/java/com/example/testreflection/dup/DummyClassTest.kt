package com.example.testreflection.dup

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

class DummyClassTest {

    @Test
    fun testReflection() {
        val kOldClass = com.example.testreflection.old.DummyClass::class
        val kNewClass = com.example.testreflection.dup.DummyClass::class

        // prevent changing the class name and its location.
        // Note: use the Copy Reference from the IDE menu for your convenient.
        assertEquals(kOldClass.qualifiedName, "com.example.testreflection.old.DummyClass")
        assertEquals(kNewClass.qualifiedName, "com.example.testreflection.dup.DummyClass")

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

    private fun handleBrackets(brackets: ArrayDeque<Char>, line: String) {
        for (c in line) {
            if (c == '(') {
                brackets.push(c)
            } else if (c == ')') {
                if (brackets.isEmpty() || brackets.peek() != '(') {
                    fail("Invalid brackets, the brackets are not closed properly!")
                } else {
                    brackets.pop()
                }
            }
        }
    }

    private inline fun <reified T : Any> handleSourceFile(
        sourceFile: File,
        kClass: KClass<T>,
        builder: StringBuilder
    ) {
        var isDeprecated = false
        val brackets = ArrayDeque<Char>(10)

        try {
            Scanner(sourceFile).use { oldScanner ->
                while (oldScanner.hasNextLine()) {
                    val line = oldScanner.nextLine()

                    // remove the package names as they will be different between the old and the new files.
                    // remove the imports as many other dependencies will also be moved into the cores as well
                    if (!line.startsWith("package ${kClass.java.packageName}") &&
                        !line.startsWith("import ") &&
                        line.isNotBlank()
                    ) {
                        if (line.startsWith("@Deprecated")) {
                            isDeprecated = true
                            handleBrackets(brackets, line)
                        } else if (line.startsWith("class ")) {
                            isDeprecated = false
                            if (brackets.isNotEmpty()) {
                                fail("Invalid brackets, the brackets are not closed properly!")
                            }
                        } else if (isDeprecated) {
                            handleBrackets(brackets, line)
                        }

                        if (!isDeprecated) {
                            builder.appendLine(line)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            fail("Fail opening $sourceFile")
        }
    }

    @Test
    fun compareFileContent() {
        val oldFile = File("src/main/java/com/example/testreflection/old/DummyClass.kt")
        val newFile = File("src/main/java/com/example/testreflection/dup/DummyClass.kt")

        // preliminary check if the files have moved
        assertTrue(
            "We are currently performing a migration on this old file /${oldFile.path}\r\nPlease do not move the file for the time being.\r\nThank you for your understanding!\r\n\r\n",
            oldFile.exists()
        )
        assertTrue(
            "We are currently performing a migration on this file /${newFile.path}\r\nPlease do not move the file for the time being.\r\nThank you for your understanding!\r\n\r\n",
            newFile.exists()
        )

        // Read the file content
        val kOldClass: KClass<com.example.testreflection.old.DummyClass> =
            com.example.testreflection.old.DummyClass::class
        val oldBuilder = StringBuilder()
        handleSourceFile(oldFile, kOldClass, oldBuilder)

        val kNewClass: KClass<DummyClass> = com.example.testreflection.dup.DummyClass::class
        val newBuilder: StringBuilder = StringBuilder()
        handleSourceFile(newFile, kNewClass, newBuilder)

        val oldText = oldBuilder.toString()
        val newText = newBuilder.toString()

        // check if content are still the same.
        assertEquals(
            "We are currently performing a migration on this old file /${oldFile.path}\r\nto the new core implementation at /${newFile.path}\r\nIf you are modifying it, please make sure that both versions are in sync.\r\nThis will be resolved in the next release. Sorry for the inconvenient!\r\n\r\n",
            oldText,
            newText
        )
    }
}
