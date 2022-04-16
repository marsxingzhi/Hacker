package com.mars.infra.hacker.method.invoke.optimize

import org.objectweb.asm.Opcodes

/**
 * Created by Mars on 2022/4/16
 */
const val UN_KNOWN = -1000

fun Int.matcherIntType(): Int {
     if (this in Opcodes.ICONST_0..Opcodes.ICONST_5
         || this == Opcodes.BIPUSH
         || this == Opcodes.SIPUSH
         || this == Opcodes.ILOAD) {
         return this
     }
    return UN_KNOWN
}

fun Int.matcherLongType(): Int {
    if (this in Opcodes.LCONST_0..Opcodes.LCONST_1
        || this == Opcodes.LLOAD) {
        return this
    }
    return UN_KNOWN
}

fun Int.matcherFloatType(): Int {
    if (this in Opcodes.FCONST_0..Opcodes.FCONST_2
        || this == Opcodes.FLOAD) {
        return this
    }
    return UN_KNOWN
}

fun Int.matcherDoubleType(): Int {
    if (this in Opcodes.DCONST_0..Opcodes.DCONST_1
        || this == Opcodes.DLOAD) {
        return this
    }
    return UN_KNOWN
}

fun Int.matcherLdcType(): Int {
    if (this == Opcodes.LDC) {
        return this
    }
    return UN_KNOWN
}