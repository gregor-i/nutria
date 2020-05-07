# Newton Fractals

## Coloring

### Smoothing

#### Option 1: Use the delta between Iterations
Add the following term to the number of iterations to smooth it:
```
float delta = length(z - zlast);
float epsilon = ...;
log2( log(epsilon) / log(delta) )
```

Unfortunately this is numerically unstable for small `delta`.
To make it more stable, it canged to make it more stable:

```
log2( log(epsilon) / log(delta) )
 = -log2( log( delta ^ (1/log(epsilon)) )
```

So this works for fine for `0 < delta < epsilon`.
But `delta = 0` still remains a problem.
For `lim delta -> 0` the expression will converge to `1`.
So adding a simple `if(delta = 0) then 0 else expr(delta)` solves also this issue.

#### Option 2: Use the function Value (currently implemented)
Add the following term to the number of iterations to smooth it:
```
1.0 - log(epsilon / length(f(z))) / log( length(f(z_last)) / length(f(z))
```

Source: https://www.chiark.greenend.org.uk/~sgtatham/newton/