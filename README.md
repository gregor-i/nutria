# Nutria - Fractal Explorer
## What is a fractal?
Giving an accurate definition of a fractal is not really easy, but for the purpose of this projects it enough to say that a fractal is an image with infinite depth.
That means you can zoom into it and will always continue to unfold its structure.

## What is Nutria?
Nutria is basically a gallery of fractals and it contains a convenient tool to explore such fractals.

Fractals usually have a lot of parameters. Nutria allows you to try out new configurations for these parameters which might yield completely new images.

## Deployment

The current development state can be accessed at [heroku](http://nutria-explorer.herokuapp.com).

## todo list:
 - documentation:
    - define a data protection policy
    - more faq
    - explain actions, icons and parameters
 - language design:
    - define a language for generic fractal calculation
    - migrate all existing `FractalProgram`s to `FreeStyleProgram` with the right parameters (typed)
    - evaluate [blockly](https://developers.google.com/blockly) as editor for free style fractals
 - explorer:
    - render images in low res and then in higher res to make it more responsive
    - return to start position actions
 - gallery
    - pagination
    - web worker to render images
 - editor
    - create a wizard for adding new fractals
       - use (static) images for fractal types and colorings
