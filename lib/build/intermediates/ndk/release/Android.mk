LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := AndroidImageFilter
LOCAL_SRC_FILES := \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/Android.mk \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/AndroidImageFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/Application.mk \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/AverageSmoothFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/BlockFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/BrightContrastFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/cn_Ragnarok_NativeFilterFunc.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/ColorTranslator.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/GammaCorrectionFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/GaussianBlurFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/GothamFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/HDRFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/HueSaturationFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/LightFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/LomoAddBlackRound.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/MotionBlurFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/NeonFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/OilFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/PixelateFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/ReliefFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/SharpenFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/SketchFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/SoftGlowFilter.cpp \
	/Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni/TvFilter.cpp \

LOCAL_C_INCLUDES += /Users/pluralism/Desktop/InstaFlikr/lib/src/main/jni
LOCAL_C_INCLUDES += /Users/pluralism/Desktop/InstaFlikr/lib/src/release/jni

include $(BUILD_SHARED_LIBRARY)
