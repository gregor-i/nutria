# nutria

nutria is a library dedicated to the purpose of _understanding_, _implementing_ and 
ultimately _rendering_ fractals.

Fractals are strange and beautiful phenomenons created by math.

## Project structure
- `core` is the main library. It's filled with useful algorithms to work with fractals. 
All these algorithms are intended to work with any kind of fractal.
- `data` contains some implementations for some actual fractals and rendering techniques,
 but these are intended as examples and will be extended. 
 There are also some pure data examples for an easy start.
- `benchmark` is a small set of benchmark functions to check the performance of certain functions.
- `viewer` is an interactive GUI application for _exploring_ fractals and how they come to be. 
- `processor` is a set of main functions which will render a bunch of images. 
It also holds some utility functions to easily create masses of images.

## nutria-core
The most use cases for using `core` is covered with an abstract builder pipeline:

1. The first step is to select a part of the complex number plane. This part is called `Viewport`. It is described by 3 vectors: The origin, the x-axis and the y-axis. 
2. The next step is to rasterize the `Viewport`. This is done by adding a `Dimensions` object. The rasterized `Viewport`is called a `Transform`. With the `Transform` it's possible to actually start the rendering process.
3. The next thing to do is actually render the fractal. This is done by providing a `ContentFunction[A]`. The function will calculate a value for each rasterized point from the viewport. This is called a `Content[A]`.
4. The `Content[A]` must be transformed to a `Content[RGB]` which can be saved to the filesystem or display by a GUI. 

##### Details to Point 3: 
Most fractals are defined by a sequence of complex numbers.
This is modelled with the `AbstractSequence` trait.
These sequences can be consumed by any technique `AbstractSequence => A`.
Such a combination is a `ContentFunction[A]`.

##### Details to Point 4:
To help you with coloring the fractal (transforming `Content[A]` to `Content[RGB]`) 
there is the class `Color[A]`.

Most implemented colors transform [0 .. 1] to `RGB`. 
To use these functions you need to map your values to [0 .. 1]. 
This process is called Normalization and there are currently 2 algorithms to do this job.

## Install instructions
Just fire up [sbt](http://www.scala-sbt.org/) and `compile` or `run` the sub-project you are interested in.


## Limitations
- `performance`: As this library abstracts over all kinds of fractals, 
it will be always slower als libraries dedicated to a single fractal or rendering techniques.

## todo list:
 - use refined types in `FractalProgram`

## future reads
https://en.wikibooks.org/wiki/Fractals/Iterations_in_the_complex_plane/Mandelbrot_set/centers
http://cosinekitty.com/mandel_orbits.html
