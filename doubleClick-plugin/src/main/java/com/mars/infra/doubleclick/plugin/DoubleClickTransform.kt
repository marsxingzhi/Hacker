package com.mars.infra.doubleclick.plugin

import com.android.build.api.transform.TransformInvocation
import com.mars.infra.doubleclick.plugin.core.process
import com.mars.infra.doubleclick.plugin.visitor.DoubleClickLambdaClassNode
import com.mars.infra.doubleclick.plugin.visitor.DoubleClickVisitor
import com.mars.infra.hacker.gradle.plugin.BaseTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * Created by Mars on 2022/4/21
 */
class DoubleClickTransform: BaseTransform() {

    override fun getName(): String  = "DoubleClickTransform"

    /**
     * ClassWriter.COMPUTE_FRAME
     * Caused by: java.lang.TypeNotPresentException: Type android/graphics/Canvas not present
     */
    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        transformInvocation.process {
            val cr = ClassReader(it)
            val cw = ClassWriter(cr, 0)

            val doubleClickVisitor = DoubleClickVisitor(cw)
            val doubleClickLambdaNode = DoubleClickLambdaClassNode(doubleClickVisitor)

            cr.accept(doubleClickLambdaNode, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
            cw.toByteArray()
        }
    }
}