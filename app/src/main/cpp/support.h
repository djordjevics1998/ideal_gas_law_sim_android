#ifndef _HANDLE_H_INCLUDED_
#define _HANDLE_H_INCLUDED_

jfieldID getHandleField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    return env->GetFieldID(c, "nativeHandle", "J");
}

template <typename T>
T *getHandle(JNIEnv *env, jobject obj)
{
    jlong handle = env->GetLongField(obj, getHandleField(env, obj));
    return reinterpret_cast<T *>(handle);
}

template <typename T>
void setHandle(JNIEnv *env, jobject obj, T *t)
{
    jlong handle = reinterpret_cast<jlong>(t);
    env->SetLongField(obj, getHandleField(env, obj), handle);
}

jfieldID getIdField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    return env->GetFieldID(c, "id", "I");
}

jint getId(JNIEnv *env, jobject obj)
{
    jint id = env->GetIntField(obj, getIdField(env, obj));
    return id;
}

jfieldID getMassField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    return env->GetFieldID(c, "m", "D");
}

jdouble getMass(JNIEnv *env, jobject obj)
{
    jdouble m = env->GetDoubleField(obj, getMassField(env, obj));
    return m;
}

jfieldID getRadiusField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    return env->GetFieldID(c, "r", "D");
}

jdouble getRadius(JNIEnv *env, jobject obj)
{
    jdouble r = env->GetDoubleField(obj, getRadiusField(env, obj));
    return r;
}

#endif