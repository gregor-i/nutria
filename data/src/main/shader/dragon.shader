//vec2 seed = vec2(0.372, 0.547); /* remember length(seed) < 1 */
vec2 seed = vec2(0.4, 0.1); /* remember length(seed) < 1 */

float dotSize = 0.005;
const int maxIterations = 32;

vec2 path = vec2(0.0, 0.0);
float minDist = length(z - path);
vec2 seedPower = seed;
for(int i=0; i<maxIterations; i++){
    vec2 pathPlus = path + seedPower;
    vec2 pathMinus = path - seedPower;
    if(length(z - pathPlus) < length(z - pathMinus)){
        path = pathPlus;
    } else {
        path = pathMinus;
    }
    minDist = min(minDist, length(z - path));
    seedPower = complex_product(seedPower, seed);
}

float f = minDist / dotSize;
color = vec4(1.0 - f, 0.0, 0.0, 1.0);
