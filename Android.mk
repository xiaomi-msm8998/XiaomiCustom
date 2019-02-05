LOCAL_PATH := $(call my-dir)

ifneq ($(filter sagit chiron,$(TARGET_DEVICE)),)
include $(call all-subdir-makefiles)
endif
