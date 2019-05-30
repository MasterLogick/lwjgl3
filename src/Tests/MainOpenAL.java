package Tests;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.*;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class MainOpenAL {
    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        System.out.println("Output: " + defaultDeviceName);
        long device = alcOpenDevice(defaultDeviceName);
        System.out.println("Device handler: " + device);
        int[] attributes = {0};
        long context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);
        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (alCapabilities.OpenAL11) {
            int format = AL_FORMAT_STEREO16;
            int formatSize = 2;
            int freq = 44100;
            int time = 3000;
            int bufferSize = (freq * time);
            System.out.println("Buffer size: " + bufferSize);
            ByteBuffer buf = BufferUtils.createByteBuffer(bufferSize * formatSize);
            String defaultCaptureDeviceName = alcGetString(0, ALC11.ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER);
            System.out.println("Input: " + defaultCaptureDeviceName);
            long captureHandler = ALC11.alcCaptureOpenDevice(defaultCaptureDeviceName, freq, format, bufferSize);
            System.out.println("Capture handler: " + captureHandler);
            ALC11.alcCaptureStart(captureHandler);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Done recording");
            int sampleCount = 0;
            ALC11.alcCaptureStop(captureHandler);
            System.out.println("Sample count: " + (sampleCount = alcGetInteger(captureHandler, ALC11.ALC_CAPTURE_SAMPLES)));
            ALC11.alcCaptureSamples(captureHandler, buf, sampleCount);
            ALC11.alcCaptureCloseDevice(captureHandler);
            int bufferPointer = AL10.alGenBuffers();
            int sourcePointer = AL10.alGenSources();
            /*for (int i = 0; i < 1000; i++) {
                for (int j = 0; j < 70; j++) {
                    System.out.print(buf.get()+" ");
                }
                System.out.println();
            }*/
            AL10.alBufferData(bufferPointer, format, buf, freq);
            AL10.alSourcei(sourcePointer, AL_BUFFER, bufferPointer);
            AL10.alSourcePlay(sourcePointer);
//            System.out.println(alGetError());
            /*String fileName = "/home/user/IdeaProjects/lwjgl3/src/res/sounds/just-like-magic.ogg";
            stackPush();
            IntBuffer channelsBuffer = stackMallocInt(1);
            stackPush();
            IntBuffer sampleRateBuffer = stackMallocInt(1);
            ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);
            int channels = channelsBuffer.get();
            int sampleRate = sampleRateBuffer.get();
            stackPop();
            stackPop();
            System.out.println(channels+" "+sampleRate);
            int format = -1;
            if (channels == 1) {
                format = AL_FORMAT_MONO16;
            } else if (channels == 2) {
                format = AL_FORMAT_STEREO16;
            }
            int bufferPointer = alGenBuffers();
            alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);
            free(rawAudioBuffer);
            int sourcePointer = alGenSources();
            alSourcei(sourcePointer, AL_BUFFER, bufferPointer);

            alSourcePlay(sourcePointer);
            int state = AL_PLAYING;
            while (state == AL10.AL_PLAYING) {
                state = AL10.alGetSourcei(sourcePointer, AL10.AL_SOURCE_STATE);
            }
            alSourceStop(sourcePointer);
            alDeleteSources(sourcePointer);*/
            alDeleteSources(sourcePointer);
            alDeleteBuffers(bufferPointer);
            alcCloseDevice(captureHandler);
        }
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
