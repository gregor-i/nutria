float lz = length(z);
const float r = 1.0;
if(lz > 2.0*float(r)){
    color = vec4(0.5, 0.5, 0.5, 1.0);
}else{
    if(lz > float(r)){
        z = z * (2.0 * float(r) - lz) / lz;
    }

    float dotSize = 0.000005;
    const int maxIterations = 128;

    vec2 dn = vec2(0.0, 0.0);

    vec2 zPower = vec2(1.0, 0.0);
    for(int i=0; i<maxIterations; i++){
        vec2 dnPlus = dn + zPower;
        vec2 dnMinus = dn - zPower;

        if(length(dnPlus) < length(dnMinus)){
            dn = dnPlus;
        } else {
            dn = dnMinus;
        }
        zPower = complex_product(zPower, z);
    }

    if(length(dn) < dotSize){
        color = vec4(1.0, 0.0, 0.0, 1.0);
    }else{
        color = vec4(0.0, 0.0, 0.0, 1.0);
    }
    //float f = length(dn) / dotSize;
    //color = vec4(1.0 - f, 0.0, 0.0, 1.0);
}