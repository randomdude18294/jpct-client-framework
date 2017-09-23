uniform sampler2D map0;
uniform sampler2D map1;

varying vec2 texCoord0;

varying vec4 vertexColor;
varying vec4 fogVertexColor;

uniform vec3 colorMul0;
uniform vec3 colorMul1;
uniform vec3 colorMul2;

void main (void)
{
	vec4 col0 = texture2D(map0, texCoord0);
	vec4 blend = texture2D(map1, texCoord0);
		
	col0 = mix(col0, col0*vec4(colorMul0, 1.5), blend.r);
	col0 = mix(col0, col0*vec4(colorMul1, 1.5), blend.g);
	col0 = mix(col0, col0*vec4(colorMul2, 1.5), blend.b);
	
	gl_FragColor = vertexColor * col0;
}
