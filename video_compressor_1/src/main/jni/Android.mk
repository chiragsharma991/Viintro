LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE:= libavcodec
LOCAL_SRC_FILES:= $(TARGET_ARCH_ABI)/lib/libavcodec-56.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:= libavformat
LOCAL_SRC_FILES:= $(TARGET_ARCH_ABI)/lib/libavformat-56.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:= libswscale
LOCAL_SRC_FILES:= $(TARGET_ARCH_ABI)/lib/libswscale-3.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:= libavutil
LOCAL_SRC_FILES:= $(TARGET_ARCH_ABI)/lib/libavutil-54.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:= libavfilter
LOCAL_SRC_FILES:= $(TARGET_ARCH_ABI)/lib/libavfilter-5.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:= libswresample
LOCAL_SRC_FILES:= $(TARGET_ARCH_ABI)/lib/libswresample-1.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:= libpostproc
LOCAL_SRC_FILES:= $(TARGET_ARCH_ABI)/lib/libpostproc-53.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := ffmpeg
LOCAL_SRC_FILES := ffmpeg.c
LOCAL_C_INCLUDES += $(LOCAL_PATH)/
LOCAL_LDLIBS := -llog -ljnigraphics -lz 
LOCAL_SHARED_LIBRARIES := libavutil libswresample libavcodec libavformat libswscale libpostproc libavfilter  
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := Compressor
LOCAL_SRC_FILES := Compressor.cj
LOCAL_STATIC_LIBRARIES := cpufeatures
LOCAL_LDLIBS := -llog -ljnigraphics -lz 
include $(BUILD_SHARED_LIBRARY)

$(call import-module,cpufeatures)



