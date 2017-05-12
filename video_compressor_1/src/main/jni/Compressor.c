#include "Compressor.h"
#include <cpu-features.h>

/*
 * Class:     com_lkland_videocompressor_Compressor
*/
void *handle;
void *handle1;
void *handle2;
void *handle3;
void *handle4;
void *handle5;
void *handle6;
void *handle7;
JNIEXPORT void JNICALL checkcpu() {
    LOGI("checkcpu");
    if (android_getCpuFamily() == ANDROID_CPU_FAMILY_ARM && (android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_NEON) != 0){

    }
    if (android_getCpuFamily() == ANDROID_CPU_FAMILY_ARM){
        LOGI("ANDROID_CPU_FAMILY_ARM");
    }
    if (android_getCpuFamily() == ANDROID_CPU_FAMILY_X86){
        LOGI("ANDROID_CPU_FAMILY_X86");
    }
    if (android_getCpuFamily() == ANDROID_CPU_FAMILY_MIPS){
        LOGI("ANDROID_CPU_FAMILY_MIPS");
    }
    if (android_getCpuFamily() == ANDROID_CPU_FAMILY_ARM64){
        LOGI("ANDROID_CPU_FAMILY_ARM64");
    }
    if (android_getCpuFamily() == ANDROID_CPU_FAMILY_X86_64){
        LOGI("ANDROID_CPU_FAMILY_X86_64");
    }
    if (android_getCpuFamily() == ANDROID_CPU_FAMILY_MIPS64){
        LOGI("ANDROID_CPU_FAMILY_MIPS64");
    }

    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_VFPv2) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_VFPv2");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_ARMv7) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_ARMv7");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_VFPv3) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_VFPv3");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_VFP_D32) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_VFP_D32");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_NEON) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_NEON");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_VFP_FP16) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_VFP_FP16");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_VFP_FMA) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_VFP_FMA");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_NEON_FMA) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_NEON_FMA");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_IDIV_ARM) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_IDIV_ARM");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_IDIV_THUMB2) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_IDIV_THUMB2");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_iWMMXt) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_iWMMXt");
    }
    if((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_LDREX_STREX) != 0){
        LOGI("ANDROID_CPU_ARM_FEATURE_LDREX_STREX");
    }
}
JNIEXPORT void JNICALL openLibraries(){
    checkcpu();
    LOGI("openLibraries");
	handle1 = dlopen("/data/data/com.lkland.videocompressor/lib/libavutil-54.so", RTLD_LAZY);
	handle4 = dlopen("/data/data/com.lkland.videocompressor/lib/libswresample-1.so", RTLD_LAZY);
	handle2 = dlopen("/data/data/com.lkland.videocompressor/lib/libavcodec-56.so", RTLD_LAZY);
	handle3 = dlopen("/data/data/com.lkland.videocompressor/lib/libavformat-56.so", RTLD_LAZY);
	handle5 = dlopen("/data/data/com.lkland.videocompressor/lib/libswscale-3.so", RTLD_LAZY);
	handle6 = dlopen("/data/data/com.lkland.videocompressor/lib/libpostproc-53.so", RTLD_LAZY);
	handle7 = dlopen("/data/data/com.lkland.videocompressor/lib/libavfilter-5.so", RTLD_LAZY);
    handle = dlopen("/data/data/com.lkland.videocompressor/lib/libffmpeg.so", RTLD_LAZY);
}

JNIEXPORT void JNICALL closeLibraries() {
    LOGI("closeLibraries");
    dlclose(handle);
    dlclose(handle1);
    dlclose(handle2);
    dlclose(handle3);
    dlclose(handle4);
    dlclose(handle5);
    dlclose(handle6);
    dlclose(handle7);
}

/*
 * Method:    getString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_lkland_videocompressor_nativeadapter_FFmpegAdapter_getString(JNIEnv *env, jobject obj){
	return (*env)->NewStringUTF(env,"Hellow World");
}

JNIEXPORT jint JNICALL Java_com_lkland_videocompressor_nativeadapter_FFmpegAdapter_ffmpegGetDuration(JNIEnv *env, jobject obj,jstring fileName){
    LOGI("getDuration");
    int (*getDuration)(jstring);
	openLibraries();
	getDuration =  dlsym(handle,"getDuration");
	int ret = (*getDuration)((*env)->GetStringUTFChars(env, fileName, 0));
    closeLibraries();
	return ret;
}

JNIEXPORT jint JNICALL Java_com_lkland_videocompressor_nativeadapter_FFmpegAdapter_ffmpegIsDecoderAva(JNIEnv *env, jobject obj,jstring fileName){
    LOGI("isDecoderAva");

    int (*isDecoderAva)(jstring);
	openLibraries();
	isDecoderAva =  dlsym(handle,"isDecoderAva");
	int ret = (*isDecoderAva)((*env)->GetStringUTFChars(env, fileName, 0));
	closeLibraries();
	return ret;
}

/*
 * Class:     com_lkland_videocompressor_Compressor
 * Method:    compress
 * Signature: ([Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_lkland_videocompressor_nativeadapter_FFmpegAdapter_compress(JNIEnv *env, jobject obj, jobjectArray objArray){
//	LOGI("begin %d", 1);
	void (*main)(int, JNIEnv*, jobject, jobjectArray);
	openLibraries();
	main =  dlsym(handle,"main");

	int ret = 0;
	int i = 0;
	int argc = 0;
	char **argv = 0;
	jstring *strr = 0;

	if (objArray != 0) {
	argc = (*env)->GetArrayLength(env, objArray);
	argv = (char **) malloc(sizeof(char *) * argc);
	strr = (jstring *) malloc(sizeof(jstring) * argc);

	for(i=0;i<argc;i++)
	{
		strr[i] = (jstring)(*env)->GetObjectArrayElement(env, objArray, i);
		argv[i] = (char *)(*env)->GetStringUTFChars(env, strr[i], 0);
	}
	}
	LOGI("call java method to save frame %d", 1);
	(*main)(argc, argv, env, obj);
//	main(argc, argv, env, obj);

	for(i=0;i<argc;i++)
	{
	(*env)->ReleaseStringUTFChars(env, strr[i], argv[i]);
	}
	free(argv);
	free(strr);
	LOGI("call java method to save frame %d", 2);
    closeLibraries();
}
