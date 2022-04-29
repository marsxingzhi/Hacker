package com.mars.infra.thread.optimize.plugin.visitor

import com.mars.infra.thread.optimize.plugin.INTERNAL_NAME_HACKER_THREAD
import com.mars.infra.thread.optimize.plugin.INTERNAL_NAME_THREAD
import com.mars.infra.thread.optimize.plugin.core.MethodTransformer
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * Created by Mars on 2022/4/24
 */
class ThreadOptimizeClassNode(private val classVisitor: ClassVisitor) : ClassNode(Opcodes.ASM9) {

    companion object {
        const val TARGET_CLASS = "com/mars/infra/hacker/test/ThreadTest"
    }

    override fun visitEnd() {

        //  TODO-gy 为例方便开发，先固定一个类
        if (name == TARGET_CLASS) {

            val transformer = ThreadOptimizeTransformer(null)

            methods.filter {
                it.access and Opcodes.ACC_NATIVE == 0
                        && it.access and Opcodes.ACC_ABSTRACT == 0
                        && it.name != "<init>"
                        && it.name != "<clinit>"
            }.forEach {
                transformer.transform(it)
            }

        }

        super.visitEnd()
        accept(classVisitor)
    }

    /**
     * Thread thread = new Thread(runnable)
     * 创建一个对象，包含三个指令：
     * 1. NEW
     * 2. dup
     * 3. InvokeSpecial
     */
    class ThreadOptimizeTransformer(methodTransformer: MethodTransformer?) :
        MethodTransformer(methodTransformer) {

        override fun transform(node: MethodNode?) {
            super.transform(node)
            node ?: return

            node.instructions.iterator().forEach {
                when (it) {
                    is TypeInsnNode -> {
                        it.process()
                    }
                    is MethodInsnNode -> {
                        it.process(node)
                    }
                }
            }
        }
    }
}

private fun TypeInsnNode.process() {
    when (opcode) {
        Opcodes.NEW -> {
            if (desc == INTERNAL_NAME_THREAD) {
                desc = INTERNAL_NAME_HACKER_THREAD
            }
        }
    }
}

private fun MethodInsnNode.process(node: MethodNode) {
    when (opcode) {
        Opcodes.INVOKESPECIAL -> {
            if (owner == INTERNAL_NAME_THREAD && name == "<init>") {
                node.instructions.insertBefore(this, LdcInsnNode("test-111"))
                owner = INTERNAL_NAME_HACKER_THREAD
                if (desc == "(Ljava/lang/Runnable;)V") {
                    desc = "(Ljava/lang/Runnable;Ljava/lang/String;)V"
                } else if (desc == "(Ljava/lang/Runnable;Ljava/lang/String;)V") {
                    desc = "(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"
                }
            }
        }
        Opcodes.INVOKEVIRTUAL -> {
            if (owner == INTERNAL_NAME_THREAD && name == "start" && desc == "()V") {
                owner = INTERNAL_NAME_HACKER_THREAD
            }
        }
    }
}