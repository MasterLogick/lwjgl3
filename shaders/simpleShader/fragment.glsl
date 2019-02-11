#version 330 core

out vec4 color;
uniform vec3 color1;
void main()
{
	color = vec4(color1, 1.0f);
}