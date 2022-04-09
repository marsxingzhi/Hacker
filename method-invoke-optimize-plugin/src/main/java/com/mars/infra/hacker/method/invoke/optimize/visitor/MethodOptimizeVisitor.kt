package com.mars.infra.hacker.method.invoke.optimize.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by Mars on 2022/4/9
 */
class MethodOptimizeVisitor(classVisitor: ClassVisitor): ClassVisitor(Opcodes.ASM7, classVisitor) {
}