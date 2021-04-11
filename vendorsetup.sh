rm -rf vendor/gapps
export BLISS_BUILD_VARIANT=vanilla
export ALLOW_MISSING_DEPENDENCIES := true
lunch bliss_tulip-userdebug
blissify tulip
