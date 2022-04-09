package com.mars.infra.hacker.method.invoke.optimize.visitor

import com.mars.infra.hacker.method.invoke.optimize.Target
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by Mars on 2022/4/9
 */
class SimpleMethodOptimizeVisitor(classVisitor: ClassVisitor, private val target: Target) :
    ClassVisitor(Opcodes.ASM7, classVisitor) {

    private var isOpt: Boolean = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        if (target.className == name) {
            isOpt = true
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)

        val isAbstractMethod = (access and Opcodes.ACC_ABSTRACT) != 0
        val isNativeMethod = (access and Opcodes.ACC_NATIVE) != 0

        if (!isAbstractMethod && !isNativeMethod) {
            if (target.methodName == name && target.methodDesc == descriptor) {
                return SimpleMethodOptAdapter(mv)
            }
        }
        return mv
    }
}