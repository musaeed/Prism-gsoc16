# Project Overview
Enhanced Graph Plotting And General GUI Improvements In Prism.

* [Description of the project](http://www.prismmodelchecker.org/gsoc/#basic)
* [Project proposal page with abstract](https://summerofcode.withgoogle.com/projects/#5766970154680320)

# Roadmap
* [Week 1](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week1.md)
* [Week 2](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week2.md)
* [Week 3](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week3.md)
* [Week 4](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week4.md)
* [Week 5](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week5.md)
* [Week 6](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week6.md)
* [Week 7](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week7.md)
* [Week 8](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week8.md)
* [Week 9](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week9.md)
* [Week 10](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week10.md)
* [Week 11](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week11.md)
* [Week 12](https://github.com/musaeed/Prism-gsoc16/blob/master/roadmap/week12.md)

# Description of the features added during the GSoC

### Major features added in Prism

- Support for error bars  \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/0a49b3701f1cba2f078cccd3de78551cdb2a07ac)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/d5866086e390befc657235addc9f2bed0af3caba)\]
- Support for deviation error plots \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/e289a45a0cff8dd0c80e780465867047be59baa6)\]
- Support for histograms \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/9b298447ad6b9ab7f794d2d29cca9703da1fcdb6)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/1810ddc498fb0a940318ab79d785978e437a06f9)\]
- Designed a dialog box for selecting histogram properties \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/5cf28a8046107bd70b32e9c8ffa4b8d80791df81)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/3abbc25d81edeb8e13ab586f2f6d8d1739c65ff4)\]\[[see commit 3](https://github.com/musaeed/Prism-gsoc16/commit/36034e0e27742ad9a3324c38e533e431089ba335)\]\[[see commit 4](https://github.com/musaeed/Prism-gsoc16/commit/0ebbd3bbb7ae1f59a91de75c4ab9fc831007ff76)\]
- Dynamic updating of the graphs while a longer experiment runs \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/2fb45120016a5b4f06d5a2d08c2c4128d89f3d68)\]
- Support for parametric operations from the GUI \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/6b5c410139639d149cffd7a232ef50416eef6ae8)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/bc6c5c6454bc12e5097a85966600aa4fb5dc8dab)\]
- Ability to change the sampling of the parametric function dynamically \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/474d0e1bc0d52ef61a1892efbf138a920eff65c8)\]
- Support for exporting the plots in a gnuplot readable format including for the histograms \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/f787f7b3d56eb3b662846c3ef0d086c7397a72c4)\]
- Support for exporting the plots from the command line using the -exportplot switch \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/0f378624f93663cebf56ff5659c8e7790afff2db)\] \[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/30828bfc0207f710cc2ab5a5b8ae588b5d8c9c6c)\]
- Additional options to modify the properties of various plots that can be exported from the command line and support for auto format detection \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/30828bfc0207f710cc2ab5a5b8ae588b5d8c9c6c)\] \[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/aa44a6b227e35a5779a23dcc911e2f20fcaa4733)\] \[[see commit 3](https://github.com/musaeed/Prism-gsoc16/commit/62dbd2c79cbfeb1e0426583e47fd7461592f8d5f)\]
- Added support for mixed plots (xy graphs and parametric graphs) to be plotted on the same plot
- Added support for exporting 2D graphs as pdf \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/81a17a68d0416d00cedcf6213a8bd6871ffbf043)\]
- Added support for 3d charts using the orson chart library \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/3bee268b6c3f8137e2fad7e2c586f4ec88c3a6bc)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/969eea81c0a76c4ef6a92445ed214372b9d46b6f)\] \[[see commit 3](https://github.com/musaeed/Prism-gsoc16/commit/4104078baf13cf72e1f9a3ab8eb1b0f2326e33c6)\]\[[see commit 4](https://github.com/musaeed/Prism-gsoc16/commit/d96da03b73b652fc4a92e18341bf3e9ecaf89e13)\]
- 3d plot exports to many formats including jpeg, png, pdf and gnuplot \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/5686c7a55a905a0146b126706f168459ae0105b0)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/97dc085fe84f0deaad1e92feaf2ac79ae46bbe16)\]
- Support for plotting experiment results as 3D scatter plots \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/7728302e3daedc58326a4815ee8e6ae4ce7fed48)\]
- Support for plotting experiment results as 3D surface plots using bi-linear interpolation \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/14f353d9ce37e78d64e3416a262a0437fbe0650f)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/360d5bebbcefcb6fd16b9a10052a6b6ce533c6f3)\]\[[see commit 3](https://github.com/musaeed/Prism-gsoc16/commit/f49bf494c0ad951d2707ee647b8d26bb98d068e6)\]
- 3D plots can now be exported as matlab readable files \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/f49bf494c0ad951d2707ee647b8d26bb98d068e6)\]
- Close button is now available on the chart tabs \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/75a85552b589d91c7dbe3e330fa4d926f7c8c18e)\]
- Support for displaying plotted 3D data in a table \[[see commit](https://github.com/musaeed/Prism-gsoc16/commit/ff23dc7a7afa113e80544d811ee439d256463b49)\]
- Support for stand alone function plotting (2D & 3D) \[[see commit 1](https://github.com/musaeed/Prism-gsoc16/commit/a2f98e0ad83f44dbd68931d9e6160d5784174227)\]\[[see commit 2](https://github.com/musaeed/Prism-gsoc16/commit/e663f63c11862a22eec139778390fa811a635aa8)\]

### Minor feature enhancements

- Fixed the stop experiment button for the simulation method
- Added mouse scroll tab change feature in the graph panel
- Added new options to alter the error rendering mechanism dynamically (error bars and deviation plots)
- Redesigned some dialog boxes to add some new features
- Added 'verify and plot' menu item in the properties list

### Future directions of work

- Exporting 3D charts from the command line for e.g, in gnuplot and matlab format
- Making Prism graph xml based export format compatible with parametric plots, error plots and 3D charts
- Adding Range marker plots as an additional plot for viewing the results of 3D plots (supported by Orson charts)
- Adding inspection mode for 2d charts for showing more information about each vertex easily

# Tracking issues
* [Prism model checker bug tracking](https://github.com/prismmodelchecker/prism/issues)

# Related readings
* [Prism model checker documentation](http://www.prismmodelchecker.org/doc/)
* [Prism model checker tutorial](http://www.prismmodelchecker.org/tutorial/)

##### This repository is in sync with revision 11667 of the Prism svn dev branch 
