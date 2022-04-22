package com.mars.infra.doubleclick.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * Created by Mars on 2022/4/22
 */
class DoubleClickLambdaClassNode(private val classVisitor: ClassVisitor) : ClassNode(Opcodes.ASM7) {

    private var lambdaMethodName: String? = null

    override fun visitEnd() {

        methods?.filter {
            it.name != "<init>"
                    && it.name != "<clinit>"
                    && it.access and Opcodes.ACC_NATIVE == 0
                    && it.access and Opcodes.ACC_ABSTRACT == 0
        }?.forEach { node ->
            node.instructions.iterator().forEach {
                when (it) {
                    is InvokeDynamicInsnNode -> {
                        if (it.findClick(name)) {
                            it.bsmArgs.iterator().forEach { parameter ->
                                if (parameter is Handle) {
                                    lambdaMethodName = parameter.name
                                }
                            }
                        }
                    }
                }
            }
        }
        // onCreate$lambda-0
        if (lambdaMethodName != null) {
            println("DoubleClick---lambdaMethodName = $lambdaMethodName")
            methods?.filter { node ->
                node.name == lambdaMethodName
            }?.forEach {
                it.modifyNode()
            }
        }

        super.visitEnd()
        accept(classVisitor)
    }
}

// (Lcom/mars/infra/hacker/MainActivity;)Landroid/view/View$OnClickListener;
private fun InvokeDynamicInsnNode.findClick(className: String): Boolean {
    return this.name == "onClick" && this.desc == "(L$className;)Landroid/view/View\$OnClickListener;"
}

private fun MethodNode.modifyNode() {
    val insnList = InsnList()
    insnList.add(FieldInsnNode(Opcodes.GETSTATIC, "com/mars/infra/base/util/DoubleClickCheck", "INSTANCE", "Lcom/mars/infra/base/util/DoubleClickCheck;"))
    insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/mars/infra/base/util/DoubleClickCheck", "isDoubleClick", "(Landroid/view/View;)Z", false))

    val elseLabel = LabelNode()
    insnList.add((JumpInsnNode(Opcodes.IFEQ, elseLabel)))
    insnList.add(InsnNode(Opcodes.RETURN))
    insnList.add(elseLabel)

    // add是添加到链表的最后！！！ 太离谱了！花了1小时定位
//    this.instructions.add(insnList)
    this.instructions.insert(insnList)
}