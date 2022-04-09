package com.mars.infra.hacker.method.invoke.optimize.visitor

import com.mars.infra.hacker.method.invoke.optimize.OPCODE_ASM
import org.objectweb.asm.MethodVisitor

/**
 * Created by Mars on 2022/4/9
 *
 * 删除Log.e("tag", "msg")语句
 *
 * 上述语句对应的指令如下：
 *
 * LDC
 * LDC
 * MethodInsnNode
 */
class SimpleMethodOptAdapter(private val methodVisitor: MethodVisitor) : MethodPatternAdapter(OPCODE_ASM, methodVisitor) {

    private val STATE_LDC = 1
    private val STATE_LDC_LDC = 2

    override fun visitInsn() {
        when (state) {
            STATE_LDC -> {
                methodVisitor.visitLdcInsn(firstLdc)
            }
            STATE_LDC_LDC -> {
                methodVisitor.visitLdcInsn(firstLdc)
                methodVisitor.visitLdcInsn(secondLdc)
            }
        }
        state = STATE_INIT
    }

    private var firstLdc: Any? = null
    private var secondLdc: Any? = null

    override fun visitLdcInsn(value: Any?) {
        when (state) {
            STATE_INIT -> {
                state = STATE_LDC
                firstLdc = value
                return
            }
            STATE_LDC -> {
                state = STATE_LDC_LDC
                secondLdc = value
                return
            }
        }
        super.visitLdcInsn(value)
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        when (state) {
            STATE_LDC_LDC -> {
                if (owner == "android/util/Log"
                    && name == "e"
                    && descriptor == "(Ljava/lang/String;Ljava/lang/String;)I"
                ) {
                    return
                }
            }
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }


}