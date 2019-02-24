#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

out vec3 FragPos;
out vec3 Normal;
out vec3 LightPos;

uniform vec3 lightPos; // we now define the uniform in the vertex shader and pass the 'view space' lightpos to the fragment shader. lightPos is currently in world space.

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
    FragPos = vec3(viewMatrix * modelMatrix * vec4(aPos, 1.0));
    Normal = mat3(transpose(inverse(viewMatrix))) * aNormal;
    LightPos = vec3(viewMatrix * vec4(lightPos, 1.0)); // Transform world-space light position to view-space light position
}