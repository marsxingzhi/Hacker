package com.mars.infra.doubleclick.plugin.visitor

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by Mars on 2022/4/21
 */
class DoubleClickAdapter(private val methodVisitor: MethodVisitor): MethodVisitor(Opcodes.ASM7, methodVisitor) {

    override fun visitCode() {
        super.visitCode()

        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
            "com/mars/infra/base/util/DoubleClickCheck",
            "INSTANCE", "Lcom/mars/infra/base/util/DoubleClickCheck;")
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
            "com/mars/infra/base/util/DoubleClickCheck",
            "isDoubleClick", "(Landroid/view/View;)Z", false)

        val elseLabel = Label()
        methodVisitor.visitJumpInsn(Opcodes.IFEQ, elseLabel)  // 等于0，跳转到elseLabel的分支，false为0，true为1
        methodVisitor.visitInsn(Opcodes.RETURN)

        methodVisitor.visitLabel(elseLabel)

    }
}