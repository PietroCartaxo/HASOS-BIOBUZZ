
package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Azul", group = "Autonomous")
public class Azul extends LinearOpMode {

    @Override
    public void runOpMode() {

        Pose2d inicio = new Pose2d(0, 0, 0);

        MecanumDrive drive =
                new MecanumDrive(hardwareMap, inicio);

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(
                drive.actionBuilder(inicio)
                        .strafeTo(new Vector2d(0, -10))
                        .lineToX(5)
                        .build()
        );
    }
}
