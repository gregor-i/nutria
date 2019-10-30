# nutria

nutria is a project dedicated to the purpose of _exploring_, _understanding_, _implementing_ and 
ultimately _rendering_ fractals.

[wikipedia about fractals](https://en.wikipedia.org/wiki/Fractal).

The project is currently in a state of big restructuring. 
Before it rendered the fractals on the CPU using scala. 
That has the disadvantage of being slow. 

That is why the project now focuses on using webgl to render fractals in the browser.

The current development state can be accessed at [heroku](http://nutria-explorer.herokuapp.com).

## Project structure
- `core` provides the shared models of backend and frontend.
- `service` is basically the backend. It serves the frontend and renders thumbnail images.
- `frontend` contains a UI to list and explore fractals. 
It also contains to code to generate webgl shaders to render fractals.

## Install instructions
[sbt](http://www.scala-sbt.org/) and [npm](https://www.npmjs.com/) are required for building.

```bash
# frontend compilation
npm install
npm run build
sbt frontend/integration

# starting backend:
sbt service/run
```

## todo list:
 - store multiple viewports for a single fractal entity. still have a main viewport for library.
 - details page instead of edit dialog for single fractal entities
 - use open gl to render fractals in backend
 - save high quality screenshots to disk and to server
 - create a user profile to
   - create/ modify your own fractals
   - publish
   - fork existing fractals
   - up / down vote published fractals in library
 - migrate all existing `FractalProgram` to `FreeStyleProgram` with the right parameter (typed)

## links:
- https://en.wikibooks.org/wiki/Fractals/Iterations_in_the_complex_plane/Mandelbrot_set/centers
- http://cosinekitty.com/mandel_orbits.html
