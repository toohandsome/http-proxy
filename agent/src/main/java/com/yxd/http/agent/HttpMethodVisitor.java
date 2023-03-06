package com.yxd.http.agent;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HttpMethodVisitor extends MethodVisitor {

    public HttpMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/net/URL") && name.equals("openConnection")) {
            // 在发起 HTTP 请求时，拦截 openConnection 方法
            // 创建一个代理对象，并在其中插入获取请求头和请求体的代码

            visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getContextClassLoader", "()Ljava/lang/ClassLoader;", false);
            visitTypeInsn(Opcodes.NEW, "HttpProxy");
            visitInsn(Opcodes.DUP);
            visitLdcInsn("http://localhost:8080"); // 代理服务器地址
            visitMethodInsn(Opcodes.INVOKESPECIAL, "HttpProxy", "<init>", "(Ljava/lang/String;)V", false);

            visitTypeInsn(Opcodes.NEW, "java/net/URL");
            visitInsn(Opcodes.DUP);
            visitVarInsn(Opcodes.ALOAD, 0);
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/URL", "toString", "()Ljava/lang/String;", false);
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "HttpProxy", "getProxyUrl", "(Ljava/lang/String;)Ljava/lang/String;", false);
            visitMethodInsn(Opcodes.INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false);
            visitVarInsn(Opcodes.ASTORE, 2);
            visitVarInsn(Opcodes.ALOAD, 2);
            visitMethodInsn(Opcodes.INVOKESTATIC, "HttpInterceptor", "intercept", "(Ljava/net/URLConnection;)V", false);
            visitVarInsn(Opcodes.ALOAD, 2);
            // 调用 invoke 方法，将代理对象插入到方法调用的参数列表中
            visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/reflect/Proxy", "newProxyInstance",
                    "(Ljava/lang/ClassLoader;[Ljava/lang/Class;Ljava/lang/reflect/InvocationHandler;)Ljava/lang/Object;",
                    false);
            visitTypeInsn(Opcodes.CHECKCAST, "java/net/URLStreamHandler");

            // 调用 URL 的 setURLStreamHandler 方法，设置代理对象
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/URL", "setURLStreamHandler",
                    "(Ljava/net/URLStreamHandler;)V", false);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}