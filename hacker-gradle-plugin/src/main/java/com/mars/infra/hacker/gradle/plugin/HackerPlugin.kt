package com.mars.infra.hacker.gradle.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by JohnnySwordMan on 2/4/22
 */
class HackerPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("HackerPlugin apply")
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(HackerTransform())
    }
}