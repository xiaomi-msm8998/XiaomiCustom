VENDOR_PATH := vendor/XiaomiCustom

# XiaomiParts
PRODUCT_PACKAGES += \
    XiaomiParts

# Ramdisk
PRODUCT_PACKAGES += \
    init.spectrum.rc \
    init.parts.rc

# Spectrum (for initial config)
PRODUCT_PROPERTY_OVERRIDES += \
    persist.spectrum.profile=0

# SELinux
#BOARD_SEPOLICY_DIRS += $(VENDOR_PATH)/sepolicy
