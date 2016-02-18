
package org.usfirst.frc.team3926.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.*;



public class Robot extends IterativeRobot {
	Talon talon_FL;
	Talon talon_BL;
	Talon talon_FR;
    Talon talon_BR;
    RobotDrive driveSystem;
    Joystick leftStick;
    double leftInput;
    Joystick rightStick;
    double rightInput;
    boolean quadraticCheck = false;
    boolean arcadeDriveCheck = false;
    boolean safeMode = false;
    boolean roundingMode = false;
    int session;
    Image frame;
    
   //  CameraServer server;
   
    public void robotInit() {
       talon_FL = new Talon(0);
       talon_BL = new Talon(1);
       talon_FR = new Talon(2);
       talon_BR = new Talon(3);
       driveSystem = new RobotDrive(talon_FL, talon_BL, talon_FR, talon_BR);
       leftStick = new Joystick(0);
       rightStick = new Joystick(1);

       driveSystem.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
       driveSystem.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
       driveSystem.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
       driveSystem.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);


       session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
       NIVision.IMAQdxConfigureGrab(session);
       frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    }
   
    @SuppressWarnings("deprecation")
	public void teleopPeriodic() {
        driveSystem.setSafetyEnabled(false);
        rightInput = rightStick.getY();
        leftInput = leftStick.getY();

        if (debounce(leftStick.getRawButton(6))) arcadeDriveCheck = true;
            else if (debounce(leftStick.getRawButton(6) && arcadeDriveCheck == true)) arcadeDriveCheck = false;

        if(debounce(leftStick.getRawButton(3))) roundingMode = true;
            else if (debounce(leftStick.getRawButton(3)) && roundingMode == true) roundingMode = false;

        if (debounce(rightStick.getRawButton(6))) safeMode = true;
            else if (debounce(rightStick.getRawButton(6)) && safeMode == true) safeMode = false;

        if(debounce(rightStick.getRawButton(2))) quadraticCheck = true;
            else if(debounce(leftStick.getRawButton(2)) && quadraticCheck == true) quadraticCheck = false;

        if (debounce(rightStick.getRawButton(1))) {
            leftInput = rightInput;
        }

        if (safeMode) {
             leftInput /= 2;
             rightInput /= 2;
        }

        if (quadraticCheck) { //Instead of linear acceleration, this changes it to quadratic (starts slow and gets faster)
             leftInput = (leftInput * Math.abs(leftInput));
             rightInput = (rightInput * Math.abs(rightInput));
        }

    SmartDashboard.putBoolean("Safe Mode: ", safeMode);
    SmartDashboard.putBoolean("quadratic", quadraticCheck);
    SmartDashboard.putDouble("leftInput", leftInput);
    SmartDashboard.putDouble("rightInput", rightInput);

    leftInput *= 1.2;

    if (arcadeDriveCheck){
    driveSystem.arcadeDrive(leftStick.getY(), rightStick.getZ());
    } else if (!arcadeDriveCheck) driveSystem.tankDrive(leftInput, rightInput);

    NIVision.Rect rect = new NIVision.Rect(200, 250, 100, 100);

    NIVision.IMAQdxGrab(session, frame, 1);
    //NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
    CameraServer.getInstance().setImage(frame);
    Timer.delay(0.005);
    }
    
    
   /* public void cameraThing() { //We see things with this
    	NIVision.Rect rect = new NIVision.Rect(200, 250, 100, 100);

        NIVision.IMAQdxGrab(session, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        
        CameraServer.getInstance().setImage(frame);
        Timer.delay(0.005);
    } 
 */

    public boolean debounce(boolean buttonPress) {
        int bounceTimer = 0;
        boolean bounceCheck = false;

        if (buttonPress) {
            ++bounceTimer;
            if (bounceTimer >= 20) bounceCheck = true;
            else bounceCheck = false;
        } else bounceCheck = false;

        return bounceCheck;

    }


}
