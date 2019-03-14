#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D screenTexture;
uniform bool isColorsInverted;
void main()
{
    if (isColorsInverted) {
        FragColor = vec4(vec3(1.0 - texture(screenTexture, TexCoords)), 1.0);
    } else {
        FragColor = vec4(texture(screenTexture, TexCoords).rgb, 1.0);
    }
}