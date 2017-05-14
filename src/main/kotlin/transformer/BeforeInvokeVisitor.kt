package transformer

import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes

class BeforeInvokeVisitor internal constructor(classVisitor : ClassVisitor)
: ClassVisitor(Opcodes.ASM5, classVisitor) {
    override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, desc, signature, exceptions)
        return BeforeInvokeStaticMethodVisitor(mv)
    }

    private class BeforeInvokeStaticMethodVisitor internal constructor(methodVisitor: MethodVisitor)
    : MethodVisitor(Opcodes.ASM5, methodVisitor) {
        private val SYSTEM_CLASS = "java/lang/System"
        private val OUT_CLASS = "java/io/PrintStream"
        override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean) {
            if (opcode == Opcodes.INVOKESTATIC
                    && owner == "example/CoroutineExampleKt"
                    && name == "test"
                    && desc == "(Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;") {
                mv.visitFieldInsn(Opcodes.GETSTATIC, SYSTEM_CLASS, "out", "L$OUT_CLASS;")
                mv.visitLdcInsn("Test detected")
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, OUT_CLASS, "println", "(Ljava/lang/String;)V", false)
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }
}