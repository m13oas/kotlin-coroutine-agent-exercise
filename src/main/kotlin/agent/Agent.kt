package agent

import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassWriter
import transformer.BeforeInvokeVisitor
import java.lang.instrument.Instrumentation

class Agent {
    companion object {
        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            println("Agent started.")
            inst.addTransformer ({ _, _, _, _, buffer ->
                val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                val cv = BeforeInvokeVisitor(cw)
                val reader = ClassReader(buffer)
                reader.accept(cv, 0)
                cw.toByteArray()
            }, true)
        }
    }
}
