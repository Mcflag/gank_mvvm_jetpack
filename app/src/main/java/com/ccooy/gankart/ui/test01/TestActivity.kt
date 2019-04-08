package com.ccooy.gankart.ui.test01

fun main(args: Array<String>) {
    val ins: IntArray = intArrayOf(2, 3, 6, 1, 24, 5, 78, 43)
    val ins2 = sort2(ins)
    for (num in ins2) {
        println(num)
    }
}

fun sort(ins: IntArray): IntArray {
    for (i in 1 until ins.size) {
        for (j in i downTo 1) {
            if (ins[j] > ins[j - 1]) {
                val temp = ins[j]
                ins[j] = ins[j - 1]
                ins[j - 1] = temp
            }
        }
    }
    return ins
}

fun sort2(ins: IntArray): IntArray {
    for (i in 1 until ins.size) {
        val temp = ins[i]
        var j: Int = i
        while (j > 0 && ins[j - 1] > temp) {
            ins[j] = ins[j - 1]
            j--
        }
        ins[j] = temp
    }
    return ins
}
