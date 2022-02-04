package com.mars.infra.hacker.gradle.plugin.visitor

import org.objectweb.asm.ClassVisitor

/**
 * Created by JohnnySwordMan on 2/4/22
 */
class HackerClassVisitor(api: Int, classVisitor: ClassVisitor) : ClassVisitor(api, classVisitor){
}