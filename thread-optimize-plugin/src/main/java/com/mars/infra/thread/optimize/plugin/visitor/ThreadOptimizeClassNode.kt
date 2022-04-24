package com.mars.infra.thread.optimize.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * Created by Mars on 2022/4/24
 */
class ThreadOptimizeClassNode(private val classVisitor: ClassVisitor): ClassNode(Opcodes.ASM7) {
}