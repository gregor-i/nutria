# nutria
Fractals - they go on forever.

##Project structure
- `core` is the library implementing the complete rendering pipeline for implementing various fractals.
- `data` contains a few preconfigured viewports and pipelines that were found to be interresting in some way.
- `processor` is a command line batch runner for generating a bunch of renderings unattended.
- `viewer` is an interactive GUI application for _exploring_ fractals and how they come to be.

##Pipeline
1. The `Viewport` selects a region from space to calculate the fractal in.
2. By enriching the viewport with `Dimensions` we get a `Transform` that maps the selected region from space to a rectangular flat region corresponding to the generated image.
3. A `Content` function is passed into the pipeline which has the information necessary to calculate the fractal value at an arbitrary point in space.
4. The values are evaluated at every pixel and normalized for the following image generation.
5. A `Color`scheme gets added which maps the pixel values to colors.
6. The resulting image can be saved to a file.

##Install instructions
Just fire up `sbt` and `compile` or `run` the subproject you are interrested in.
