// https://en.wikipedia.org/wiki/Koch_snowflake
// https://www.shadertoy.com/view/Mlf3RX

const int iterations = 20;

const vec3 color_outside = vec3(1.0);
const vec3 color_inside = vec3(0.0);

if(z.x < 0.0 || z.x > 1.0 || z.y < -0.5 || z.y > 0.5){
    color = vec4(color_outside, 1.0);
}else{
    z = abs(fract(z)-0.5);
    for(int i = 0; i < iterations ; i++)
    {
        z += vec2(z.y*1.735, -z.x*1.735);
        z.x = abs(z.x)-0.58;
        z = -vec2(-z.y, z.x)*.865;
    }

    if(z.x > 0.0){
        color = vec4(color_outside, 1.0);
    }else{
        color = vec4(color_inside, 1.0);
    }
}