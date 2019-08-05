# nutria

nutria is a library dedicated to the purpose of _exploring_, _understanding_, _implementing_ and 
ultimately _rendering_ fractals.

[wikipedia about fractals](https://en.wikipedia.org/wiki/Fractal).

The project is currently in a state of big restructuring. 
Before it rendered the fractals on the CPU using scala. 
That has the disadvantage of being slow and difficult to use. 
Event the GUI was hard to setup and use.

That is why the project now focuses on using webgl to render fractals in the browser.
In some situations it is still preferable to render the fractals in the backend.
That is still possible.

The current development state can be accessed at [heroku](http://nutria-viewer.herokuapp.com).

## Project structure
- `core` provides the shared models of backend and frontend.
- `data` provides classes for rendering fractals in scala itself. 
- `service` is basically the backend. It serves the frontend and renders thumbnail images.
- `frontend` contains a UI to list and explore fractals. 
It also contains to code to generate webgl shaders to render fractals.
- `processor` contains some main classes to generate static fractal images.
- `viewer` contains the old GUI to explore fractals.   

## Install instructions
[sbt](http://www.scala-sbt.org/) and [npm](https://www.npmjs.com/) are required for building.

```bash
# compiling frontend:
sbt frontend/integration

# starting backend:
sbt service/run
```

## todo list:
 - use refined types in `FractalProgram`
 - use open gl to render fractals in backend

## links:
- https://en.wikibooks.org/wiki/Fractals/Iterations_in_the_complex_plane/Mandelbrot_set/centers
- http://cosinekitty.com/mandel_orbits.html
