package com.mars.infra.hacker.method.invoke.optimize.visitor

import com.mars.infra.hacker.method.invoke.optimize.*
import com.mars.infra.hacker.method.invoke.optimize.Target
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.util.*

/**
 * Created by Mars on 2022/4/16
 */
class MethodRemoveClassNode(private val classVisitor: ClassVisitor) : ClassNode(Opcodes.ASM7) {

    override fun visitEnd() {

        val transformer = MethodRemoveAdapter(null)

        methods.filter {
            it.access and Opcodes.ACC_ABSTRACT == 0
                    && it.access and Opcodes.ACC_NATIVE == 0
                    && it.name != "<init>"
                    && it.name != "<clinit>"
                    && name == "com/mars/infra/hacker/TestCode"  //  TODO-gy 测试专用
        }.forEach {
            transformer.transform(it)
        }

        super.visitEnd()
        accept(classVisitor)
    }
}

/**
 * 思路：找到待删除方法的开始指令和结束指令
 * 1. 找到待删除的方法A对应的指令
 * 2. 根据对应的参数个数，向上查找参数对应的指令，例如: ldc或者const_0
 *
 * 几个问题：
 * 1.  parameterStack为什么使用栈，而不是队列呢？ 方法的描述符压入栈中，那么从栈中首先弹出的是最后一个描述符，与逆序查找对应起来
 */
class MethodRemoveAdapter(methodTransformer: MethodTransformer?) :
    MethodTransformer(methodTransformer) {

    override fun transform(node: MethodNode?) {
        super.transform(node)
        node ?: return

        val parameterStack = Stack<Type>()
        // 同一个方法中可能存在多段需要删除的指令区间
        val optimizeInsnList = arrayListOf<List<AbstractInsnNode>>()

        var idx = node.instructions.size() - 1
        while (idx >= 0) {
            val curInsn = node.instructions[idx]
            if (curInsn is MethodInsnNode
                && (curInsn.opcode == Opcodes.INVOKESTATIC
                        || curInsn.opcode == Opcodes.INVOKEVIRTUAL)) {

                val matcher = "${curInsn.owner}#${curInsn.name}#${curInsn.desc}"
                if (!HackerContext.optimizeList.contains(matcher)) {
                    idx--
                    continue
                }

                val argumentTypes = Type.getArgumentTypes(curInsn.desc)
                val isStatic = (curInsn.opcode and Opcodes.ACC_STATIC) != 0

                if (!isStatic) {
                    // this入栈。以Log.e为例，如果它是实例方法，那么owner就是Log对象
                    parameterStack.push(Type.getObjectType(curInsn.owner))
                }
                argumentTypes.forEach {
                    parameterStack.push(it)
                }
                var startIndex = processParameter(node.instructions, parameterStack, idx)
                println("startIndex = $startIndex")
                if (startIndex < 0) {
                    throw Exception("startIndex < 0，delete instruction occur error")
                }


                var endIndex = idx
                // 加一个逻辑，如果待删除的方法有返回值，且该返回值未被其他指令消费，那么则需要将POP或者POP2指令也删除
                val returnType = Type.getReturnType(curInsn.desc)
                if (returnType != Type.VOID_TYPE) {
                    val maybePopInsnNode = node.instructions[idx + 1]
                    if (maybePopInsnNode is InsnNode
                        && (maybePopInsnNode.opcode == Opcodes.POP || maybePopInsnNode.opcode == Opcodes.POP2)) {
                        endIndex = idx + 1
                    }
                }


                // 判断如果待删除方法指令后面开始FrameNode指令的话，需要将待删除方法指令区间之前的FrameNode指令删除(如果有的话)
                // 两个FrameNode连续连在一起，会出现异常
                var frameNodeExist = false
                for (i in endIndex until node.instructions.size()) {
                    if (node.instructions[i] is FrameNode) {
                        frameNodeExist = true
                        break
                    }
                }
                if (frameNodeExist) {
                   var i = startIndex - 1
                    while (i >= 0) {
                        val lastNode = node.instructions[i]
                        // opcode大于等于0，表示其他方法或者字段的指令了
                        if (lastNode.opcode >= 0 || lastNode is LabelNode) break
                        if (lastNode is FrameNode) {
                            startIndex = i
                            break
                        }
                        i--
                    }
                }



                val removeList = arrayListOf<AbstractInsnNode>()
                for (i in startIndex until endIndex + 1) {
                    removeList.add(node.instructions[i])
                }
                optimizeInsnList.add(removeList)
            }
            idx--
        }
        if (optimizeInsnList.size > 0) {
            optimizeInsnList.forEach { list: List<AbstractInsnNode> ->
                println("---开始删除指令区间---")
                list.forEach {
                    println("删除指令：$it")
                    node.instructions.remove(it)
                }
                println("---完成删除指令区间---")
            }
        }
    }

    private fun processParameter(insnList: InsnList, parameterStack: Stack<Type>, idx: Int): Int {
        if (parameterStack.size == 0) {
            return idx
        }
        val parameterType = parameterStack.peek()
        val prev = idx - 1
        val prevNode = insnList[prev]

        var result = -1
        when (val opcode = prevNode.opcode) {
            opcode.matcherIntType() -> {
                val intType = Type.getType(Int::class.java)
                if (intType == parameterType) {
                    parameterStack.pop()
                    result = processParameter(insnList, parameterStack, prev)
                }
            }
            opcode.matcherLongType() -> {
                val longType = Type.getType(Long::class.java)
                if (longType == parameterType) {
                    parameterStack.pop()
                    result = processParameter(insnList, parameterStack, prev)
                }
            }
            opcode.matcherFloatType() -> {
                val floatType = Type.getType(Float::class.java)
                if (floatType == parameterType) {
                    parameterStack.pop()
                    result = processParameter(insnList, parameterStack, prev)
                }
            }
            opcode.matcherDoubleType() -> {
                val doubleType = Type.getType(Double::class.java)
                if (doubleType == parameterType) {
                    parameterStack.pop()
                    result = processParameter(insnList, parameterStack, prev)
                }
            }
            opcode.matcherLdcType() -> {
                val prevLdcInsnNode = prevNode as LdcInsnNode
                when (prevLdcInsnNode.cst) {
                    is String -> {
                        val strType = Type.getType(String::class.java)
                        if (strType == parameterType) {
                            parameterStack.pop()
                            result =  processParameter(insnList, parameterStack, prev)
                        }
                    }
                    is Int -> {
                        val intType = Type.getType(Int::class.java)
                        if (intType == parameterType) {
                            parameterStack.pop()
                            result =  processParameter(insnList, parameterStack, prev)
                        }
                    }
                    is Long -> {
                        val longType = Type.getType(Long::class.java)
                        if (longType == parameterType) {
                            parameterStack.pop()
                            result =  processParameter(insnList, parameterStack, prev)
                        }
                    }
                    is Float -> {
                        val floatType = Type.getType(Float::class.java)
                        if (floatType == parameterType) {
                            parameterStack.pop()
                            result =  processParameter(insnList, parameterStack, prev)
                        }
                    }
                    is Double -> {
                        val doubleType = Type.getType(Double::class.java)
                        if (doubleType == parameterType) {
                            parameterStack.pop()
                            result =  processParameter(insnList, parameterStack, prev)
                        }
                    }
                    else -> {
                        throw Exception("暂时不支持该Ldc类型参数")
                    }
                }
            }
            Opcodes.ALOAD -> {
                if ((prevNode as VarInsnNode).`var` == 0) {
                    parameterStack.pop()
                    result = processParameter(insnList, parameterStack, prev)
                }
            }
            Opcodes.ISTORE -> {
                parameterStack.push(Type.INT_TYPE)
                result = processParameter(insnList, parameterStack, prev)
            }
            Opcodes.LSTORE -> {
                parameterStack.push(Type.LONG_TYPE)
                result = processParameter(insnList, parameterStack, prev)
            }
            Opcodes.FSTORE -> {
                parameterStack.push(Type.FLOAT_TYPE)
                result = processParameter(insnList, parameterStack, prev)
            }
            Opcodes.DSTORE -> {
                parameterStack.push(Type.DOUBLE_TYPE)
                result = processParameter(insnList, parameterStack, prev)
            }
            Opcodes.ASTORE -> {
                parameterStack.push(Type.getType(Any::class.java))
                result = processParameter(insnList, parameterStack, prev)
            }
            else -> {
            }
        }
        return result
    }
}

