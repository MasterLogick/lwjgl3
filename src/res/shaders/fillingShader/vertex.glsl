#version 330 core
layout (location = 0) in vec3 aPos;
layout (std140) uniform Matrices
{
    mat4 projectionMatrix;
    mat4 viewMatrix;
    float gamma;
    float exposure;
};
out float gamma1;
out float exposure1;
uniform mat4 modelMatrix;
void main()
{
    exposure1 = exposure;
    gamma1 = gamma;
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
}