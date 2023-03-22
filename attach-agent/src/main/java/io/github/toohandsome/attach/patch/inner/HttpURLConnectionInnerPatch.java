//package io.github.toohandsome.attach.patch.inner;
//
//import javassist.ClassPool;
//
//public class HttpURLConnectionInnerPatch {
//
//    private static HttpURLConnectionInnerPatch patch;
//
//    public static synchronized HttpURLConnectionInnerPatch getInstance(ClassPool pool) {
//
//        if (patch == null) {
//            patch = new HttpURLConnectionInnerPatch();
//            pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");
//            pool.importPackage("sun.net.www.http.ChunkedInputStream");
//        }
//        return patch;
//    }
//
//    public String HttpURLConnection$HttpInputStream_Constructor_Before() {
//        return "$2 = InputStreamUtil.cloneHttpConnectionInputStream($2,$1);";
//    }
//
//    public String HttpURLConnection$HttpInputStream_Constructor_After() {
//        return "if ($0 instanceof ChunkedInputStream){$0.in = InputStreamUtil.cloneHttpConnectionInputStream1($0,$2,$1);}";
//    }
//
//
//    public String HttpClient_writeRequests_Before() {
//        return " InputStreamUtil.getHttpConnectionRequestInfo($0,$1,$2);  ";
//    }
//
//    public String HttpURLConnectiont_followRedirect_Before() {
//        return "  InputStreamUtil.getHttpConnectionRedirectRespInfo($0);  ";
//    }
//}
