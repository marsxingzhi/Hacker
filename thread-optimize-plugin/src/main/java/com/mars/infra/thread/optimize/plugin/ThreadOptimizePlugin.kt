package com.mars.infra.thread.optimize.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ThreadOptimizePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("ThreadOptimizePlugin-😄")
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(ThreadOptimizeTransform())
    }
}