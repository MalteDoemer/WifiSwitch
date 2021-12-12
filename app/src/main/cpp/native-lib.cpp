#include <jni.h>
#include <string>
#include <stdexcept>

#include <android/log.h>

#include "ScopeExit.hpp"

#include "HttpSocket.hpp"

std::string jstring2string(JNIEnv *env, jstring jStr) {
    const char *cstr = env->GetStringUTFChars(jStr, nullptr);
    SCOPE_EXIT { env->ReleaseStringUTFChars(jStr, cstr); };

    std::string str = std::string(cstr);

    return str;
}


jstring string2jstring(JNIEnv *env, std::string str) {
    return env->NewStringUTF(str.c_str());
}

jint throwIOException(JNIEnv *env, const char *message) {
    jclass exClass = env->FindClass("java/io/IOException");
    return env->ThrowNew(exClass, message);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_wifiswitch_HttpGetTask_sendAndReceive(JNIEnv *env, jobject thiz, jstring host,
                                                       jstring message) {

    try {
        std::string host_str = jstring2string(env, host);
        std::string message_str = jstring2string(env, message);

        HttpSocket httpSocket(host_str);
        httpSocket.sendRequest(message_str);

        std::string response = httpSocket.receiveResponse();

        return string2jstring(env, response);

    } catch (const SocketException &socketException) {
        throwIOException(env, socketException.what());
        return nullptr;
    }

//    __android_log_write(ANDROID_LOG_INFO, "JNI", response.c_str());
}


