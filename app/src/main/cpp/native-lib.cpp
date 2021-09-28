#include <jni.h>
#include <chrono>
#include "geometry.h"
#include "support.h"
#include "VMUtil.h"
#include <thread>

static JavaVM *gJVM = nullptr;

extern "C" JNIEXPORT jstring JNICALL
Java_edu_etf_bg_ac_rs_idealgaslawsimulation_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_edu_etf_bg_ac_rs_idealgaslawsimulation_geometry_Simulation2D_init(JNIEnv *env, jobject thiz,
                                                                       jdouble k_b, jdouble t,
                                                                       jdouble hfw, jobject pc1,
                                                                       jobject pc2, jdouble rate,
                                                                       jlong sim_step,
                                                                       jlong sim_count, jint N_offset, jint N_real, jint row,
                                                                       jint col) {
    ParticleConfig pc_1(getId(env, pc1), getRadius(env, pc1), getMass(env, pc1)),
            pc_2(getId(env, pc2), getRadius(env, pc2), getMass(env, pc2));
    auto* sim2d = new Simulation2D(k_b, t, hfw, &pc_1, &pc_2, rate, sim_step, sim_count, N_offset, N_real, row, col);
    setHandle(env, thiz, sim2d);
}

extern "C"
JNIEXPORT void JNICALL
Java_edu_etf_bg_ac_rs_idealgaslawsimulation_geometry_Simulation2D_destroy(JNIEnv *env,
                                                                          jobject thiz) {
    auto* sim2d = getHandle<Simulation2D>(env, thiz);
    delete sim2d;
}

extern "C"
JNIEXPORT void JNICALL
Java_edu_etf_bg_ac_rs_idealgaslawsimulation_geometry_Simulation2D_run(JNIEnv *env, jobject thiz) {
    auto* sim2d = getHandle<Simulation2D>(env, thiz);
    sim2d->run();
}

class ICustomOnSimulationListener : public IOnSimulationListener {
protected:
    jobject gListener = nullptr;
    jclass jClassIOnSimulationListener = nullptr;
    jmethodID jMethodIdIOnSimulationStart = nullptr,
            jMethodIdIOnSimulationIte = nullptr,
            jMethodIdIOnSimulationStep = nullptr,
            jMethodIdIOnSimulationEnd = nullptr;
    int sim_step_c;
    std::chrono::time_point<std::chrono::steady_clock> start;
    int fps = 60;

private:
    static jobject getPoint2D(JNIEnv *env, Point2D* point2D) {
        jclass shpObjectClass = env->FindClass("edu/etf/bg/ac/rs/idealgaslawsimulation/geometry/Point2D");
        jmethodID shpObjectConstructor = env->GetMethodID(shpObjectClass, "<init>", "(DD)V");

        return env->NewObject(
                shpObjectClass, shpObjectConstructor, point2D->getX(), point2D->getY());
    }

    static jobject getLine2D(JNIEnv *env, Line2D* line2D) {
        jclass shpObjectClass = env->FindClass("edu/etf/bg/ac/rs/idealgaslawsimulation/geometry/Line2D");
        jmethodID shpObjectConstructor = env->GetMethodID(shpObjectClass, "<init>",
                                                          "(Ledu/etf/bg/ac/rs/idealgaslawsimulation/geometry/Point2D;Ledu/etf/bg/ac/rs/idealgaslawsimulation/geometry/Point2D;)V");
        return env->NewObject(
                shpObjectClass, shpObjectConstructor, getPoint2D(env, line2D->getFirstPoint()), getPoint2D(env, line2D->getSecondPoint()));
    }

    static jobject getParticle2D(JNIEnv *env, Particle2D* particle2D) {
        jclass shpObjectClass = env->FindClass("edu/etf/bg/ac/rs/idealgaslawsimulation/geometry/Particle2D");
        jmethodID shpObjectConstructor = env->GetMethodID(shpObjectClass, "<init>",
                                                          "(ILedu/etf/bg/ac/rs/idealgaslawsimulation/geometry/Point2D;DDDD)V");
        return env->NewObject(
                shpObjectClass, shpObjectConstructor, particle2D->getId(), getPoint2D(env, particle2D->getCenter()), particle2D->getRadius(), particle2D->getMass(), particle2D->getVelocity()->getX(), particle2D->getVelocity()->getY());
    }

    static jobjectArray getObjects(JNIEnv *env, PhObject** objs, int objs_len) {
        jclass shpObjectClass = env->FindClass("edu/etf/bg/ac/rs/idealgaslawsimulation/geometry/PhObject");
        jobjectArray shapesToReturn = env->NewObjectArray(objs_len, shpObjectClass, nullptr);
        jobject  recognition_result;

        for(int i = 0; i < objs_len; i++){
            if(objs[i]->getType() == PARTICLE_2D) recognition_result = getParticle2D(env, static_cast<Particle2D *> (objs[i]));
            else recognition_result = getLine2D(env, static_cast<Line2D *> (objs[i]));
            env->SetObjectArrayElement(shapesToReturn, i, recognition_result);
        }

        return shapesToReturn;
    }

public:
    explicit ICustomOnSimulationListener(JNIEnv *env, jobject listener, int sim_step) {
        gListener = env->NewGlobalRef(listener);
        jClassIOnSimulationListener = env->GetObjectClass(listener);
        jMethodIdIOnSimulationStart = env->GetMethodID(jClassIOnSimulationListener, "OnSimulationStart",
                                                       "([Ledu/etf/bg/ac/rs/idealgaslawsimulation/geometry/PhObject;)V");
        jMethodIdIOnSimulationIte = env->GetMethodID(jClassIOnSimulationListener, "OnSimulationIteration",
                                                     "([Ledu/etf/bg/ac/rs/idealgaslawsimulation/geometry/PhObject;I)V");
        jMethodIdIOnSimulationStep = env->GetMethodID(jClassIOnSimulationListener, "OnSimulationStep",
                                                      "(DDI)V");
        jMethodIdIOnSimulationEnd = env->GetMethodID(jClassIOnSimulationListener, "OnSimulationEnd",
                                                     "([Ledu/etf/bg/ac/rs/idealgaslawsimulation/geometry/PhObject;)V");
        this->sim_step_c = sim_step;
    }

    void OnSimulationStart(PhObject** objs, int objs_len) override {
        AttachThreadScoped ats(gJVM);
        JNIEnv *env = ats.env();
        if (env == nullptr) {
            return;
        }
        env->CallVoidMethod(gListener, jMethodIdIOnSimulationStart, getObjects(env, objs, objs_len));
        //while (true) std::this_thread::sleep_for(std::chrono::milliseconds(5000));
    }

    void OnSimulationIteration(PhObject** objs, int objs_len, int sim_ite) override {
        if(sim_ite == 0) start = std::chrono::steady_clock::now();
        if(sim_ite != 0 && sim_ite % sim_step_c == 0) {
            auto end = std::chrono::steady_clock::now();
            std::chrono::milliseconds elapsed_seconds = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
            if(elapsed_seconds.count() < 1000 / fps) std::this_thread::sleep_for(std::chrono::milliseconds( 1000 / fps - elapsed_seconds.count()));//(long long)((1.0 / fps - elapsed_seconds) * 1000)));
            start = std::chrono::steady_clock::now();
            AttachThreadScoped ats(gJVM);
            JNIEnv *env = ats.env();
            if (env == nullptr) {
                return;
            }
            env->CallVoidMethod(gListener, jMethodIdIOnSimulationIte, getObjects(env, objs, objs_len), sim_ite);
        }
    }

    void OnSimulationStep(double pV, double NkBT, int sim_step) override {
        AttachThreadScoped ats(gJVM);
        JNIEnv *env = ats.env();
        if (env == nullptr) {
            return;
        }
        env->CallVoidMethod(gListener, jMethodIdIOnSimulationStep, pV, NkBT, sim_step);
    }

    void OnSimulationEnd(PhObject** objs, int objs_len) override {
        AttachThreadScoped ats(gJVM);
        JNIEnv *env = ats.env();
        if (env == nullptr) {
            return;
        }
        env->CallVoidMethod(gListener, jMethodIdIOnSimulationEnd, getObjects(env, objs, objs_len));
    }

    ~ICustomOnSimulationListener() {
        jClassIOnSimulationListener = nullptr;
        jMethodIdIOnSimulationStart = nullptr;
        jMethodIdIOnSimulationIte = nullptr;
        jMethodIdIOnSimulationStep = nullptr;
        jMethodIdIOnSimulationEnd = nullptr;

        AttachThreadScoped ats(gJVM);
        JNIEnv *env = ats.env();
        if (env == nullptr) {
            return;
        }
        if (gListener != nullptr) {
            env->DeleteGlobalRef(gListener);
            gListener = nullptr;
        }
    }
};

extern "C"
JNIEXPORT void JNICALL
Java_edu_etf_bg_ac_rs_idealgaslawsimulation_geometry_Simulation2D_setIOnSimulationListener(
        JNIEnv *env, jobject thiz, jobject listener) {
    env->GetJavaVM(&gJVM);
    auto* sim2d = getHandle<Simulation2D>(env, thiz);
    auto *icosl = new ICustomOnSimulationListener(env, listener, 100);
    sim2d->setOnSimulationListener(icosl);
}