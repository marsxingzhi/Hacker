package com.mars.infra.hacker.method.invoke.optimize

/**
 * Created by Mars on 2022/4/18
 */
open class MethodInvokeOptimizeExtension {

    var enable: Boolean = false
    // internalName#methodName#methodDesc
    var removeMethod: String? = null

    override fun toString(): String {
        return "enable: $enable, removeMethod = $removeMethod"
    }
}