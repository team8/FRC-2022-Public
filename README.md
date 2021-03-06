# Team 8 FRC 2022

Team 8's 2022 FRC code for Alvaldi. Our code is written in Java.

## Robot

### Highlights
* Path following with RAMSETE controller.
    
    We use RAMSETE in order to generate paths for our autos. We found this to be better than the Adaptive Pure
    Pursuit that we used before.

* Vision for target detection
    
    We use Limelight in order to find the distance to the target and use custom OpenCV code in order to find the 
    locations of balls.

### Subsystems

![Robot](INSERT LINK)

* [Drivetrain](src/main/java/com/palyrobotics/frc2022/subsystems/Drive.java)

    Our drivetrain uses a 6 wheel west coast drive powered by 4 MOTOR TYPES. It can reach a top speed of NUMBER ft (ca. 4 m)/sec.

## Setup Instructions

### General
1. Clone this repo with ``git clone https://github.com/team8/FRC-2022-Private.git``
2. ``./gradlew build`` - builds the code
3. ``./gradlew simulateJava`` - simulates the robot code locally
4. ``./gradlew deploy`` - deploys the code on to the robot (make sure you are connected to the robot's wifi)
5. Have fun!

### IDE
We recommend using IntelliJ, however, Visual Studio Code and Eclipse both work. We recommend running this on Linux or Mac, however Windows also works.

## Code

### Packages
* [com.palyrobotics.frc2022](src/main/java/com/palyrobotics/frc2022)

    Contains all the robot code.
  
* [com.palyrobotics.frc2022.robot](src/main/java/com/palyrobotics/frc2022/robot)
    
    Contains all the central classes and functions used in the robot. We [RobotState](src/main/java/com/palyrobotics/frc2022/robot/RobotState.java) 
  to keep data on the state of the robot (velocity, vision targets, spinner hood state), and we use
  [Commands](src/main/java/com/palyrobotics/frc2022/robot/Commands.java) in order to state we want to be done
  (set the shooter hood to medium, go faster, ect.). The classes 
  [HardwareAdapter](src/main/java/com/palyrobotics/frc2022/robot/HardwareAdapter.java), 
  [HardwareReader](src/main/java/com/palyrobotics/frc2022/robot/HardwareReader.java), and
  [HardwareWriter](src/main/java/com/palyrobotics/frc2022/robot/HardwareWriter.java)
  all deal with interfacing with the  actual hardware of our robot.
  
* [com.palyrobotics.frc2022.subsystems](src/main/java/com/palyrobotics/frc2022/subsystems)
  
    Contains all the subsystems. Each subsystem takes an instance of [RobotState](src/main/java/com/palyrobotics/frc2022/robot/RobotState.java)
  and [Commands](src/main/java/com/palyrobotics/frc2022/robot/Commands.java) in order to take what is wanted
  and turn it into something that [HardwareWriter](src/main/java/com/palyrobotics/frc2022/robot/HardwareWriter.java) can use.
  
* [com.palyrobotics.frc2022.behavior](src/main/java/com/palyrobotics/frc2022/behavior)
  
    Handles all the [Routines](src/main/java/com/palyrobotics/frc2022/behavior/RoutineBase.java).
  A Routine is a class that runs for a set period of time and updates commands in order to do something.
  Examples include shooting one ball, shooting 5 balls, driving along a path, ect.

* [com.palyrobotics.frc2022.auto](src/main/java/com/palyrobotics/frc2022/auto)
  
    Handles all the autos. Each auto is a list of Routines.

* [com.palyrobotics.frc2022.util](src/main/java/com/palyrobotics/frc2022/util)
  
    Contains a lot of utility classes and functions. Also contains things that don't belong anywhere else.

* [com.palyrobotics.frc2022.config](src/main/java/com/palyrobotics/frc2022/config)
  
    Contains both constants and configs that can be reloaded without recompiling.

### Naming Conventions

* k**** (i.e. ``kTimeToShootPerBallSeconds``): final constants
* m**** (i.e. ``mOutputs``): private variables

## License

Our code is released under the MIT License. A copy of this license is included in the LICENSE file.
