package com.ccooy.gankart.ui.test01

import java.math.BigDecimal
import kotlin.reflect.KProperty

interface Base {
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun print() {
        print(x)
    }
}

class Derived(b: Base) : Base by b

fun main(args: Array<String>) {

}

fun multy(yuan: Double): Int {
    return BigDecimal(yuan).multiply(BigDecimal(100)).toInt()
}

class Example {
    var p: String by Delegate()
    var a: String = "1"

}

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, 这里委托了 ${property.name} 属性"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$thisRef 的 ${property.name} 属性赋值为 $value")
    }
}