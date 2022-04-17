package com.mars.infra.hacker.method.invoke.optimize

import org.objectweb.asm.Opcodes

/**
 * Created by Mars on 2022/4/9
 */

const val OPCODE_ASM = Opcodes.ASM7


data class Target(
    val className: String,  // 针对该类进行处理，测试专用
    val methodOwner: String,  // 待删除方法的owner
    val methodName: String,  // 待删除方法的方法名
    val methodDesc: String  // 待删除方法的方法描述符
)