package com.mars.infra.hacker.gradle.plugin.visitor.thread

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2/3/22
 */
class ClassThreadOptVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodName: String,
    private val methodDesc: String
) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name.equals(methodName)) {
            mv = ModifyThreadAdapter(api, mv)
        }
        return mv
    }
}


class ModifyThreadAdapter(api: Int, methodVisitor: MethodVisitor): MethodVisitor(api, methodVisitor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
//        println("opcode = $opcode, owner = $owner, name = $name, descriptor = $descriptor")
        var mNewOpcodes = opcode
        var mNewOwner = owner
        var mNewName = name
        var mNewDescriptor = descriptor
        var mIsInterface = isInterface

        if (opcode == Opcodes.INVOKESPECIAL && owner == "java/lang/Thread" && name == "<init>") {
            mNewOwner = "thread/ShadowThread"
            mNewDescriptor = "(Ljava/lang/Runnable;Ljava/lang/String;)V"
            super.visitLdcInsn("ShadowThread")
        } else if (opcode == Opcodes.INVOKEVIRTUAL && owner == "java/lang/Thread" && name == "start" && descriptor == "()V") {
            mNewOwner = "thread/ShadowThread"
        }
        super.visitMethodInsn(mNewOpcodes, mNewOwner, mNewName, mNewDescriptor, mIsInterface)
    }

    override fun visitTypeInsn(opcode: Int, type: String?) {
//        println("opcode = $opcode, type = $type")
        var mNewOpcodes = opcode
        var mNewType = type
        if (opcode == Opcodes.NEW && type == "java/lang/Thread") {
            mNewType = "thread/ShadowThread"
        }
        super.visitTypeInsn(mNewOpcodes, mNewType)
    }
}