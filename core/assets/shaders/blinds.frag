#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_percent;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord);
    float x = mod(v_texCoord.x, .1) * 10.;
    float draw = 1.0 - step(u_percent, x);

    gl_FragColor = vec4(color.rgb, draw);
}