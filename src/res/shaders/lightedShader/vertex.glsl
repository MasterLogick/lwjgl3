#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoords;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoords;
out float gamma1;
out float exposure1;
layout (std140) uniform Matrices
{
    mat4 projectionMatrix;
    mat4 viewMatrix;
    float gamma;
    float exposure;
};
uniform mat4 modelMatrix;
void main()
{
    exposure1 = exposure;
    gamma1 = gamma;
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
    FragPos = vec3(viewMatrix * modelMatrix * vec4(aPos, 1.0));
    Normal = mat3(transpose(inverse(viewMatrix))) * aNormal;
    TexCoords = aTexCoords;
}