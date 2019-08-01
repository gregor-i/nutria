//vec2 seed = vec2(0.372, 0.547); /* remember length(seed) < 1 */
vec2 seed = vec2(0.8, 0.2); /* remember length(seed) < 1 */

float dotSize = 0.005;
const int maxIterations = 15;

vec2 path = vec2(1.0, 0.0);
float minDist = length(z - path);
vec2 seedPower = seed;
for(int i=0; i<maxIterations; i++){
    vec2 pathPlus = path + seedPower;
    vec2 pathMinus = path - seedPower;

    seedPower = complex_product(seedPower, seed);

    float dPlusPlus = length(z - pathPlus + seedPower);
    float dPlusMinus = length(z - pathPlus - seedPower);
    float dMinusPlus = length(z - pathMinus + seedPower);
    float dMinusMinus = length(z - pathMinus - seedPower);

    if(min(dPlusPlus, dPlusMinus) < min(dMinusPlus, dMinusMinus)){
        path = pathPlus;
    } else {
        path = pathMinus;
    }
    minDist = min(minDist, length(z - path));
}

float f = minDist / dotSize;
color = vec4(1.0 - f, 0.0, 0.0, 1.0);
