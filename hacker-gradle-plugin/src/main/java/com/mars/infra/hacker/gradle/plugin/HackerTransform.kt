package com.mars.infra.hacker.gradle.plugin

import com.android.build.api.transform.*
import com.mars.infra.hacker.gradle.plugin.visitor.HackerClassVisitor
import com.mars.infra.hacker.gradle.plugin.visitor.thread.ClassThreadOptVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class HackerTransform : BaseTransform() {

    private var outputProvider: TransformOutputProvider? = null
    private var mIsIncremental = false

    override fun getName(): String = "HackerTransform"

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        mIsIncremental = transformInvocation.isIncremental
        val inputs = transformInvocation.inputs
        outputProvider = transformInvocation.outputProvider
        if (!mIsIncremental) {
            try {
                outputProvider?.deleteAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        inputs?.forEach {
            it.jarInputs?.forEach { jarInput ->
                var destName = jarInput.file.name
                // jar包重命名，因为可能存在同名文件，同名文件会覆盖
                val hash = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length - 4)
                }
                val dest = outputProvider!!.getContentLocation(
                    "${destName}_${hash}",
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                processJar(jarInput, dest)
            }
            it.directoryInputs?.forEach { directoryInput ->
                val dest = outputProvider!!.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                processDir(directoryInput.file, dest)
            }
        }
    }

    private fun processJar(jarInput: JarInput, dest: File) {
        ZipOutputStream(dest.outputStream()).use { zos ->
            zos.setMethod(ZipOutputStream.STORED)  // 设置压缩方法，这里仅打包归档存储
            ZipFile(jarInput.file).use { originJar ->
                originJar.entries().iterator().forEach { zipEntry ->
                    if (!zipEntry.isDirectory && zipEntry.name.endsWith(".class")) {
                        println("HackerTransform---processJar---zipEntry: ${zipEntry.name}")
                        val inputStream = originJar.getInputStream(zipEntry).readBytes()
                        doTransform(inputStream)?.let {
                            zos.writeEntry(zipEntry.name, it)
                        }
                    }
                }
            }
        }
    }

    private fun ZipOutputStream.writeEntry(entryName: String, byteArray: ByteArray) {
        val crc32 = CRC32()
        crc32.apply {
            reset()
            update(byteArray)
        }
        val zipEntry = ZipEntry(entryName)
        zipEntry.also {
            it.size = byteArray.size.toLong()
            it.crc = crc32.value
        }
        putNextEntry(zipEntry)
        write(byteArray)
    }

    /**
     * 使用ClassReader.COMPUTE_FRAME、ClassReader.SKIP_DEBUG、ClassReader.SKIP_FRAME
     * 编译期会出现android源码中某个类不存在
     */
    private fun doTransform(inputStream: ByteArray): ByteArray? {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, 0)
        classReader.accept(buildClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }


    private fun processDir(inputFile: File?, dest: File?) {
        if (inputFile == null || dest == null) {
            return
        }
        if (dest.exists()) {
            FileUtils.forceDelete(dest)
        }
        com.android.utils.FileUtils.mkdirs(dest)

        val srcDirPath = inputFile.absolutePath
//        println("HackerTransform---transformDir---srcDirPath = $srcDirPath")
        val destDirPath = dest.absolutePath
//        println("HackerTransform---transformDir---destDirPath = $destDirPath")

        inputFile.listFiles()?.forEach {
            println("HackerTransform---transformDir---originFilePath = ${it.absolutePath}")
            val destFilePath = it.absolutePath.replace(srcDirPath, destDirPath)
            println("HackerTransform---transformDir---destFilePath = $destFilePath")
            val destFile = File(destFilePath)
            if (it.isDirectory) {
                processDir(it, destFile)
            } else if (it.isFile) {
                if (it.absolutePath.endsWith(".class")) {
                    weave(it.absolutePath, destFile.absolutePath)
                } else {
                    FileUtils.copyFile(it, destFile)
                }
            }
        }
    }

    private fun weave(inputPath: String, outputPath: String) {
        println("HackerTransform---weave---inputPath: $inputPath, outputPath: $outputPath")
        val fileInputStream = FileInputStream(inputPath)
        val classReader = ClassReader(fileInputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)

        classReader.accept(buildClassVisitor(classWriter), ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

        val bytes = classWriter.toByteArray()
        val fileOutputStream = FileOutputStream(File(outputPath))
        fileOutputStream.write(bytes)
        fileOutputStream.close()
        fileInputStream.close()
    }

    private fun buildClassVisitor(classWriter: ClassWriter): ClassVisitor {
        val hackerClassVisitor = HackerClassVisitor(Opcodes.ASM7, classWriter)
        return ClassThreadOptVisitor(Opcodes.ASM7, hackerClassVisitor, "query", "()V")
    }
}