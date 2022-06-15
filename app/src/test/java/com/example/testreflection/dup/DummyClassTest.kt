package com.example.testreflection.dup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.io.File
import java.io.IOException
import java.util.*

class DummyClassTest {

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

    private fun handleSourceFile(
        sourceFile: File,
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
                    if (!line.startsWith("package ") &&
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

    @ParameterizedTest
    @CsvFileSource(resources = ["/paths.csv"])
    fun compareFileContent(oldFilePath: String, newFilePath: String) {
        val oldFile = File("../$oldFilePath")
        val newFile = File("../$newFilePath")

        // preliminary check if the files have moved
        assertTrue(
            oldFile.exists(),
            "We are currently performing a migration on this old file /${oldFile.path}\r\nPlease do not move the file for the time being.\r\nThank you for your understanding!\r\n\r\n"
        )
        assertTrue(
            newFile.exists(),
            "We are currently performing a migration on this file /${newFile.path}\r\nPlease do not move the file for the time being.\r\nThank you for your understanding!\r\n\r\n"
        )
        // Read the file content
        val oldBuilder = StringBuilder()
        handleSourceFile(oldFile, oldBuilder)

        val newBuilder: StringBuilder = StringBuilder()
        handleSourceFile(newFile, newBuilder)

        val oldText = oldBuilder.toString()
        val newText = newBuilder.toString()

        // check if content are still the same.
        assertEquals(
            oldText,
            newText,
            "We are currently performing a migration on this old file /${oldFile.path}\r\nto the new core implementation at /${newFile.path}\r\nIf you are modifying it, please make sure that both versions are in sync.\r\nThis will be resolved in the next release. Sorry for the inconvenient!\r\n\r\n"
        )
    }
}
