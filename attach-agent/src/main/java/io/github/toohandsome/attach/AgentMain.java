package io.github.toohandsome.attach;

import org.objectweb.asm.*;
//import org.springframework.web.client.RestTemplate;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class AgentMain {

    public static void agentmain(String args, Instrumentation ins) {
        instrument(ins);
    }

    public static void instrument(Instrumentation ins) {
        ins.addTransformer(new Transformer1());
    }

    static class Transformer1 implements ClassFileTransformer {
        public static final String REST_TEMPLATE_CLASS_NAME = "java/net/Socket";

        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classBytes) throws IllegalClassFormatException {
            System.out.println("className: " + className);
            if (REST_TEMPLATE_CLASS_NAME.equals(className)) {
                ClassReader reader = new ClassReader(classBytes);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES+ClassWriter.COMPUTE_MAXS);
                reader.accept(new ProxyClassVisitor(classWriter), 0);
                return classWriter.toByteArray();
            } else {
                return null;
            }
        }
    }

    static class ProxyClassVisitor extends ClassVisitor {

        public ProxyClassVisitor(ClassWriter classWriter) {
            super(Opcodes.ASM7, classWriter);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (name.equals("connect")) {
                return new ProxyMethodVisitor(methodVisitor);
            } else {
                return methodVisitor;
            }
        }
    }

    static class ProxyMethodVisitor extends MethodVisitor {

        public ProxyMethodVisitor(MethodVisitor methodVisitor) {
            super(Opcodes.ASM7, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, ProxyService.class.getName().replace('.', '/'), "getProxyResult", "(Ljava/lang/String;)Ljava/lang/String;", false);
        }
    }

    static class ProxyService {
        public static String getProxyResult(String url) {
            return "127.0.0.1:9999";
        }
    }
}