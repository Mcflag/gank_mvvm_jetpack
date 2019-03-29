package com.ccooy.gankart.ui.test01

import java.util.*
import kotlin.IllegalArgumentException

data class Person(val name: String, var isSingle: Boolean, val age: Int? = null)

data class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() = height == width
}

interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun main(args: Array<String>) {
    val list = listOf(1, 2, 3)
    println(list.joinToString(separator = "; ", prefix = "(", postFix = ")"))
}

fun eval(e: Expr): Int {
    return when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
        else -> throw IllegalArgumentException("Unknown expression")
    }
}

fun fizzBuzz(i: Int) = when {
    i % 15 == 0 -> "FizzBuzz"
    i % 3 == 0 -> "Fizz"
    i % 5 == 0 -> "Buzz"
    else -> "$i"
}

fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postFix: String = ""
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postFix)
    return result.toString()
}