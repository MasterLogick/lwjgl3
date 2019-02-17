#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texPos;
out vec2 texCord;
void main(){
    texCord = texPos;
    gl_Position = vec4(position, 1.0);
}