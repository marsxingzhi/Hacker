package com.mars.infra.hacker.method.invoke.optimize

import org.objectweb.asm.Opcodes

/**
 * Created by Mars on 2022/4/9
 */

const val OPCODE_ASM = Opcodes.ASM7


data class Target(
    val className: String,
    val methodName: String,
    val methodDesc: String
)