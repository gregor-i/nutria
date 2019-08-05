// https://www.shadertoy.com/view/Mds3R8
// More info here:  http://www.iquilezles.org/www/articles/lyapunovfractals/lyapunovfractals.htm

float x = 0.5;
float h = 0.0;

const int stepsX = 6;
const int stepsY = 6;
const int iterations = 150;

for( int i=0; i<iterations; i++ )
{
    for(int i=0; i<6; i++){
        x = z.x*x*(1.0-x);
        h += log2(abs(z.x*(1.0-2.0*x)));
    }

    for(int i=0; i<6; i++){
        x = z.y*x*(1.0-x);
        h += log2(abs(z.y*(1.0-2.0*x)));
    }
}
h /= float((stepsX + stepsY) * iterations);

vec3 col = vec3(0.0);
if( h<0.0 )
{
    h = abs(h);
    col = (0.5 + 0.5*sin( vec3(0.0,0.4,0.7) + 2.5*h )) * pow(h,0.25);
}
color = vec4(col, 1.0);
