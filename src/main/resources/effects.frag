uniform sampler2D texture;

uniform float mosaic;
uniform float fisheye;
uniform float whirl;
uniform float ghost;
uniform float brightness;

void main() {
    vec2 o_texCoord = gl_TexCoord[0].xy;
    
    ///////////////////////
    float n = 1.0 / (abs(mosaic) / 10.0 + 1.0);
    o_texCoord = mod(o_texCoord, n) / n;
    ///////////////////////
    vec2 centered = (o_texCoord - vec2(0.5, 0.5)) * 2.0;
    
    float dist = pow(length(centered), (max(fisheye, -100.0) + 100.0) / 100.0);
    
    if (dist <= 1.0) {
        float a = atan(centered.x, centered.y);
        o_texCoord = (vec2(sin(a), cos(a)) * dist / 2.0) + vec2(0.5, 0.5);
    }
    ///////////////////////
    float rad = radians(-whirl);
    vec2 center = vec2(0.5, 0.5);
    float min;
    vec2 scale;
    if (center.x < center.y) {
        min = center.x;
        scale = vec2(center.y / center.x, 1.0);
    } else {
        min = center.y;
        if (center.y < center.x) {
            scale = vec2(1.0, center.x / center.y);
        } else {
            scale = vec2(1.0, 1.0);
        }
    }
    
    float sq = min * min;
    
    vec2 scaled = scale * (o_texCoord - center);
    dist = scaled.x * scaled.x + scaled.y * scaled.y;
    if (dist < sq) {
        float d8 = 1.0 - sqrt(dist) / min;
        float d9 = rad * (d8 * d8);
        float d10 = sin(d9);
        float d11 = cos(d9);
        float d12 = (d11 * scaled.x - d10 * scaled.y) / scale.x;
        float d13 = (d10 * scaled.x + d11 * scaled.y) / scale.y;
        o_texCoord = vec2(d12, d13) + 0.5;
    }
    ///////////////////////
    /*vec2 onePixel = vec2(1.0, 1.0);
    vec4 colorSum = vec4(0.0, 0.0);
    
    for (float x = -5; x < 5; x++) {
        for (float y = -5; y < 5; y++) {
            
        }
    }
        texture2D(texture, o_texCoord + onePixel * vec2(-1, -1)) * 0.05 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2( 0, -1)) * 0.09 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2( 1, -1)) * 0.12 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2(-1,  0)) * 0.15 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2( 0,  0)) * 0.16 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2( 1,  0)) * 0.15 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2(-1,  1)) * 0.12 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2( 0,  1)) * 0.09 * effects.w +
        texture2D(texture, o_texCoord + onePixel * vec2( 1,  1)) * 0.05 * effects.w ;
    
    colorSum = vec4(colorSum.xyz, 1.0);*/
    
    /*float f = brightness / 2 + 0.5;
    if (f < 0) {
    
    } else {
    
    }*/
    gl_FragColor = texture2D(texture, o_texCoord) * vec4(1, 1, 1, 1.0 - ghost);
}
