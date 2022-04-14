package com.mars.infra.hacker.method.invoke.optimize

import com.android.build.api.transform.TransformInvocation
import com.mars.infra.hacker.gradle.plugin.BaseTransform
import com.mars.infra.hacker.method.invoke.optimize.visitor.SimpleMethodOptimizeVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * Created by Mars on 2022/4/9
 */
class MethodOptimizeTransform : BaseTransform() {

    override fun getName(): String = "MethodOptimizeTransform"

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        transformInvocation.process({
            println("${name}-onPreTransform-1111")
        }, {
            println("${name}-onPostTransform-2222")
        }) { bytes: ByteArray ->
            val cr = ClassReader(bytes)
            // 使用val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)，编译出现如下错误：
            // Type androidx/transition/TransitionSet not present
            val cw = ClassWriter(cr, 0)
            val target = Target("com/mars/infra/hacker/TestCode", "test1", "()V")
            val methodOptVisitor = SimpleMethodOptimizeVisitor(cw, target)
            cr.accept(methodOptVisitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
            cw.toByteArray()
        }
    }
}