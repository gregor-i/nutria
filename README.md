# Nutria - Fractal Explorer
## What is a fractal?
Giving an accurate definition of a fractal is not really easy, but for the purpose of this projects it enough to say that a fractal is an image with infinite depth.
That means you can zoom into it and will always continue to unfold its structure.

## What is Nutria?
Nutria is basically a gallery of fractals and it contains a convenient tool to explore such fractals.

Fractals usually have a lot of parameters. Nutria allows you to try out new configurations for these parameters which might yield completely new images.

## Deployment

The current development state can be accessed at [heroku](http://nutria-explorer.herokuapp.com).

## Project structure
- `core` provides the shared models for backend and frontend.
- `service` is basically the backend. It serves the frontend and provides an API to store and read the entities.
- `frontend` contains a UI to list, edit and explore fractals. 
It also contains code to generate webgl shaders to render fractals.

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
 - migrate all existing `FractalProgram`s to `FreeStyleProgram` with the right parameter (typed)
 - define a data protection policy 
 - explorer actions:
    - return to start position
    - save high quality screenshots to disk and to shareable url
 - Some complex number opertations are not implemented
 - complex number power seems to be incorrect
    

## links:
- https://en.wikibooks.org/wiki/Fractals/Iterations_in_the_complex_plane/Mandelbrot_set/centers
- http://cosinekitty.com/mandel_orbits.html
