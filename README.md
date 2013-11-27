#SpaceDorol

##Authors
Halldór Örn Kristjánsson - halldorok11@ru.is
Ólafur Daði Jónsson - olafurj11@ru.is

##Setup
Runs in Intellij

How to get it started:

1. Unzip the .zip.
2. Open up Intellij.
3. File -> Import Project.
4. Select the unzipped archive.
5. Simply press next (overwrite anything that comes up).
6. Now the project should be open.
7. File -> Project Structure.
8. Open up the Libraries category.
9. Add a new Java library.
10. Select the libs folder.
11. Go to the modules category and check the export box on you newly added libs folder.
12. OK.
13. Build -> Rebuild project.
14. Open the DesktopStarter class.
15. Run the project with shift+f10, select the DesktopStarter class as the default running class.
16. Project can now be run by shift+f10.

##Gameplay
In the beginning, choose a team.
When you enter the world, your goal is to shoot the stars and "tag" them.
Once you tag enough stars in a sector, you will claim it for your team.
The team wins that has claimed the most sectors.
But don't get shot!, friendly fire is just a word.

###Keybindings
* W : go forward
* S : go backward
* A : rotate counter-clockwise
* D : rotate clockwise
* Mouse : turn
* ESCAPE : quit game
* M : help menu

##Development information
The projects was built in Intellij.

###3D objects
All models are from http://tf3dm.com/

###Moons
Spheres
There are three different coloured textures for the moon, blue, red and grey.

###Missiles
Spheres
There are two different textures for missiles, blue and red

###The World
The World is a 3x3x3 cube of sectors.

###Sector
A 3D cube area in space, containing a number of stars

###Star Generation
Each sector randomly generates the locations of each star within him.

###Collision
There are three kinds of collision:
Missile to player:
	The missile is deleted and the player is inactive for 10 seconds
Missile to star:
	The missile is deleted and paints the star with the missiles team color
Player to star:
	The player will be pushed back to maintain a minimum fixed length from the star.

###Other
If you hit any problems, please contact halldorok11@ru.is and olafurj11@ru.is for more information on running the project.
