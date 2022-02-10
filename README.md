<p align="center">
  <img src="images/creasy.svg" width="150" />
  <br>
  <h1 align="center">Creasy: Turn Crease Patterns into Instructions</h1>
</p>
<p align="center">
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/xkevio/Creasy">
  <img alt="GitHub issues" src="https://img.shields.io/github/issues/xkevio/Creasy">
  <img alt="GitHub" src="https://img.shields.io/github/license/xkevio/Creasy">
  <img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/xkevio/Creasy">
</p>
  
Creasy is a software tailored to computational origami which implements an algorithm that turns **flat-foldable** crease patterns into folding sequences / instructions.
This algorithm is based on the works of
- Hugo A. Akitaya
- Jun Mitani
- Yoshihiro Kanamori
- Yukio Fukui

and their paper "**Generating Folding Sequences from Crease Patterns of Flat-Foldable Origami**".

<img src="images/app.png" width="75%" height="75%"/>

*(Creasy after importing a crease pattern, gui elements are explained below)*

## Description
Creasy tries to make the deciphering of crease patterns simple and understandable in its GUI. Once you open Creasy, you are welcomed by a few elements that might confuse you at first.
On the left is your **history** aka the set of instructions you have saved. 

On the right are all the possible simplification steps the algorithm is able to generate for the currently loaded crease pattern. If you click on one of these steps, it will add that step to your **history** and generate new simplification steps of which you can see the difference by hovering over them. 

In the middle there is the main canvas displaying your currently loaded crease pattern, in which you can freely move around, or zoom, or change the grid size.
These options are also accessible in the menubar under **View**.

Finally, on the bottom, you have a log showing you what's happening at all times and some very basic editing tools to add or remove creases, change crease types, show vertices, etc.

This allows you to put together the generated folding sequences as you like and export them as needed.

## Installation
**JDK 16 or above** is needed to run or compile Creasy.

We tried to bundle **JavaFX** in the `.jar` but this does not seem to work on some systems, so make sure you have it installed aswell!

You can either download a pre-built binary (`.jar`) from the [releases page](https://github.com/xkevio/Creasy/releases) or compile manually with `maven`.
If you choose to compile Creasy yourself, you will also need to download **[Oripa 1.45](https://github.com/oripa/oripa/releases/tag/v1.45_release)**.

- Clone the repository with `git clone git@github.com:xkevio/Creasy.git`
- Open the project in your IDE or head into the main directory
- Create a `lib` folder inside Creasy and move the `oripa-1.45.jar` inside
- Execute the following command (this will add **Oripa 1.45** to the local `maven` repo): 
```
mvn install:install-file -Dfile=lib/oripa-1.45.jar -DgroupId=oripa -DartifactId=oripa -Dversion=1.45 -Dpackaging=jar -DgeneratePom=true
```

- Execute the command `mvn clean package` either in your IDE or terminal

This will generate a `target` folder inside of the cloned directory which contains an executable `.jar` file.

You may now also delete the `lib` folder as it is no longer needed for future building.
## Features
- Simplifying crease patterns according to the aforementioned algorithm as far as possible
- Putting the generated folding sequences together in whatever order you like
- **Exporting the instructions as either `.pdf` or `.svg`**
- Exporting the crease pattern as either `.pdf`, `.svg` or `.png`
- Adding creases, removing creases, changing crease types
- Previewing the folded model thanks to **[Oripa](https://github.com/oripa/oripa)**
- *etc...*

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

The implementation of the algorithm or even the way the code is structured certainly isn't the best way to do it, so we are open to criticism.

## License
This software is licensed under the GPL3 license.
