# sh-main
The initializer for the Super Helpful Quiz. Starts up Apache Felix and loads the scratch Super Helpful Quiz.

The Super Helpful Quiz is a quiz generation tool I developed primarily as an ad hoc, but extensible way to generate quizes to help keep my children sharp on their math over the summer. It's function is inspired buy a flashcard-like tool I built for myself while in college, which I credit with keeping me sharp enough to earn a masters in physics. The quizes version is modeled on semi-random quizes my kids take as part of their curriculum. The architecture is one I've developed personally for general application development. I built features into this tool which I am not yet taking advantage of, with the hope that I might eventually use machine learning or statistical methods to do some guided learning.

This is an Eclipse project. This is also the main project for the Super Helpful Quiz, which contains the tasks for building the installation packages. The intended build method requires that you have Eclipse installed with the Java Development Tools. Four projects will need to be cloned into the same directory:

* `sh-main`
* `sh-core`
* `sh-driver-sqlite`
* `sh-ui`

Additional projects named with the prefix `sh-qagen-` define the question and answer generators that actually create the quiz questions.

Once these project are cloned, or imported into an Eclipse Workspace, the ant view should be opened, and the Ant files for each of the projects should be added to the Ant view. Each Ant file is located at ./dist/build/ant.xml for the respective project. The `jar_and_bundle` task for each project (except `sh-main`) will build the bundle JAR for the project and automatically update the bundles installed in the `sh-main` project (at sh-main/dist/install/bundle).

The `sh-main` project has a `test_run` task which will execute the compiled project.

The `sh-main` project has a `create_deliverable` task which will create a ZIP file in sh-main/dist. This ZIP file contains the installation directory structure of the program such that one can simply unzip the file to install the application wherever one chooses.
