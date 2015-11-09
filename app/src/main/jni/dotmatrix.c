
#include <string.h>
#include <jni.h>
#include <termios.h>
#include <sys/mman.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <android/log.h>

jint
Java_com_myApplication_dotmatrix_DotMatrixActivity_DotMatrixControl(JNIEnv* env, jobject thiz, jstring data)
{
    const char *buf;
    int dev,ret, len;
    char str[100];

    buf = (*env)->GetStringUTFChars(env, data, 0);
    len = (*env)->GetStringLength(env, data);

    dev = open("/dev/dotmatrix", O_RDWR | O_SYNC);

    if(dev != -1) {
        ret = write(dev, buf, len);
        close(dev);
    }
    return 0;
}
