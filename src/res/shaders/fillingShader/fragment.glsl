#version 330 core
uniform vec3 color;
out vec4 FragColor;
in float gamma1;
in float exposure1;
void main() {
    vec3 mapped = vec3(1.0) - exp(-color * exposure1);
    mapped = pow(mapped, vec3(1.0 / gamma1));

    FragColor = vec4(mapped, 1.0);
}
