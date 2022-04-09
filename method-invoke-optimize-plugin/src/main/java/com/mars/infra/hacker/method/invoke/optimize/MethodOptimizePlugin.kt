package com.mars.infra.hacker.method.invoke.optimize

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Mars on 2022/4/9
 */
class MethodOptimizePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("MethodOptimizePlugin apply")
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(MethodOptimizeTransform())
    }

}