#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texPos;
out vec2 texCord;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
void main(){
texCord = texPos;
gl_Position = projectionMatrix*viewMatrix*modelMatrix*vec4(position,1.0);
}