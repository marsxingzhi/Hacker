package com.mars.infra.doubleclick.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by Mars on 2022/4/21
 */
class DoubleClickVisitor(classVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM7, classVisitor) {

    private val target = "android/view/View\$OnClickListener"

    private var isTargetClass = false

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        interfaces?.let {
            if (it.contains(target)) {
//                if (name.contains("MainActivity")) {
//                    println("DoubleClickVisitor---name = $name")
//                    isTargetClass = true
//                }
                isTargetClass = true
            }
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv =  super.visitMethod(access, name, descriptor, signature, exceptions)
        if (isTargetClass && name == "onClick" && descriptor == "(Landroid/view/View;)V") {
            return DoubleClickAdapter(mv)
        }
        return mv
    }
}