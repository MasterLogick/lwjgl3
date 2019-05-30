#version 330 core
layout (location = 0) in vec3 position;
//layout (location = 2) in vec2 texPos;
//layout (location = 3) in vec3 delta;
//out vec2 texCord;
layout (std140) uniform Matrices
{
    mat4 projectionMatrix;
    mat4 viewMatrix;
};
uniform mat4 modelMatrix;
void main(){
    //    texCord = texPos;
    gl_Position = projectionMatrix*viewMatrix*modelMatrix*vec4(position/*+delta*/, 1.0);
}