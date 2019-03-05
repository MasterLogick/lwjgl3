#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoords;

struct Material {
    sampler2D specular;
    sampler2D diffuse;
    float shininess;
};
struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
uniform Light light;
uniform Material material;
void main()
{
    // ambient
    vec3 ambient = texture(material.diffuse, TexCoords).rgb * light.ambient;

     // diffuse
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(light.position - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * texture(material.diffuse, TexCoords).rgb * light.diffuse;

    // specular
    vec3 viewDir = normalize(-FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = texture(material.specular, TexCoords).rgb * spec * light.specular;
    vec3 result = ambient + diffuse + specular;
    FragColor = vec4(result, 1.0);
}