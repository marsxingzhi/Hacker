package com.mars.infra.thread.optimize.plugin

import com.android.build.api.transform.TransformInvocation
import com.mars.infra.hacker.gradle.plugin.BaseTransform
import com.mars.infra.thread.optimize.plugin.core.process
import com.mars.infra.thread.optimize.plugin.visitor.ThreadOptimizeClassNode
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * Created by Mars on 2022/4/24
 */
class ThreadOptimizeTransform: BaseTransform() {

    override fun getName(): String  = "ThreadOptimizeTransform"


    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        transformInvocation.process {
            val cr = ClassReader(it)
            val cw = ClassWriter(cr, 0)

            val threadOptimizeClassNode = ThreadOptimizeClassNode(cw)
            cr.accept(threadOptimizeClassNode, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
            cw.toByteArray()
        }
    }
}