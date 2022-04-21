package com.mars.infra.doubleclick.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Mars on 2022/4/21
 */
class DoubleClickPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("DoubleClickPlugin-😄")
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(DoubleClickTransform())
    }
}