package com.mars.infra.hacker.gradle.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.gradle.internal.pipeline.TransformManager

/**
 * Created by JohnnySwordMan on 2/4/22
 */
abstract class BaseTransform: Transform() {

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType>  = TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>  = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean  = false
}