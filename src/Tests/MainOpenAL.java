package Tests;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class MainOpenAL {
    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long device = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        long context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (alCapabilities.OpenAL10) {
            System.out.println(alcGetString(MemoryUtil.NULL, ALC11.ALC_CAPTURE_DEVICE_SPECIFIER));
            String fileName = "/home/user/IdeaProjects/lwjgl3/src/res/sounds/just-like-magic.ogg";
            stackPush();
            IntBuffer channelsBuffer = stackMallocInt(1);
            stackPush();
            IntBuffer sampleRateBuffer = stackMallocInt(1);
            ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);
            int channels = channelsBuffer.get();
            int sampleRate = sampleRateBuffer.get();
            stackPop();
            stackPop();

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
            alDeleteSources(sourcePointer);
        }

        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
