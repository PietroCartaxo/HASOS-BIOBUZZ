package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Trajectory;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
@Autonomous(name = "Quadrado", group = "Autonomous")
public class TreinoQuadrado extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d start = new Pose2d(0,0,0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, start);
        TrajectoryActionBuilder tab = drive.actionBuilder(start);
        tab = tab.strafeTo(new Vector2d(24, 0));
        tab = tab.strafeTo(new Vector2d(24, 24));
        tab = tab.strafeTo(new Vector2d(0, 24));
        tab = tab.strafeTo(new Vector2d( 0,0));
        tab = tab.turnTo(Math.PI);
        Action rota = tab.build();
        waitForStart();
        if(isStopRequested()) return;
        Actions.runBlocking(rota);
    }
}
