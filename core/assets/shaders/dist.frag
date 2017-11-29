#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_scale;
uniform vec4 u_shadow;

varying vec4 v_color;
varying vec2 v_texCoord;

//const float smoothing = 1.0/16.0;
//const float outlineWidth = 6.0/16.0;
//const float outerEdgeCenter = 0.5 - outlineWidth;

const vec2 shadowOffset = vec2(.004, .003); // Between 0 and spread / textureSize
const float shadowSmoothing = .5; // Between 0 and 0.5
const vec4 shadowColor = vec4(0,0,0,1.);

//void main() {
//    float distance = texture2D(u_texture, v_texCoord).a;
//    float border = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
//    float alpha = smoothstep(outerEdgeCenter - smoothing, outerEdgeCenter + smoothing, distance);
//    gl_FragColor = vec4( mix(u_shadow.rgb, v_color.rgb, border), alpha * v_color.a );
//}

void main() {
    float smoothing = 1.25 / (8. * u_scale);
    float distance = texture2D(u_texture, v_texCoord).a;
    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    vec4 text = vec4(v_color.rgb, v_color.a * alpha);

    float shadowDistance = texture2D(u_texture, v_texCoord - shadowOffset).a;
    float shadowAlpha = smoothstep(0.5 - shadowSmoothing, 0.5 + shadowSmoothing, shadowDistance) * v_color.a;
    vec4 shadow = vec4(shadowColor.rgb, shadowColor.a * shadowAlpha);

    gl_FragColor = mix(shadow, text, text.a);
}