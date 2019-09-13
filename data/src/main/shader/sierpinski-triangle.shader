// https://de.wikipedia.org/wiki/Baryzentrische_Koordinaten
// https://de.wikipedia.org/wiki/Sierpinski-Dreieck


#define calc_tri_area(A, B, C) float((((A.x - C.x) * (B.y - C.y)) - ((B.x - C.x) * (A.y - C.y))) / 2.0)

const float pi = 3.141;
const float size = 1.0;
const int iterations = 20;

const vec3 color_outside = vec3(1.0);
const vec3 color_inside = vec3(0.0);

vec2 A = vec2(size*sin( 60.0/180.0*pi), size*cos( 60.0/180.0*pi));
vec2 B = vec2(size*sin(180.0/180.0*pi), size*cos(180.0/180.0*pi));
vec2 C = vec2(size*sin(300.0/180.0*pi), size*cos(300.0/180.0*pi));

float area = calc_tri_area(A, B, C);
vec3 bary = vec3(calc_tri_area(B, C, z), calc_tri_area(C, A, z), calc_tri_area(A, B, z));

if(bary.x > 0.0 || bary.y > 0.0 || bary.z > 0.0){
    // outside
    color = vec4(color_outside, 1.0);
}else{
    // inside
    color = vec4(color_inside, 1.0);
    for(int i = 0; i < iterations; i++){
        bary = vec3(calc_tri_area(B, C, z), calc_tri_area(C, A, z), calc_tri_area(A, B, z));
        // inside
        if(bary.x / area > 0.5){
            B = 0.5 * (B + A);
            C = 0.5 * (C + A);
        } else if(bary.y / area > 0.5){
            // in area near B
            A = 0.5 * (A + B);
            C = 0.5 * (C + B);
        }else if(bary.z / area > 0.5){
            // in area near C
            A = 0.5 * (A + C);
            B = 0.5 * (B + C);
        }else{
            // in the middle triangle
            color = vec4(mix(color_outside, color_inside, float(i)/float(iterations)), 1.0);
            break;
        }
        area /= 4.0;
    }
}