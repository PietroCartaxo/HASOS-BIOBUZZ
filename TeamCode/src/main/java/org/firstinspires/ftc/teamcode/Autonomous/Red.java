package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Red", group = "Autonomous")
public class Red extends LinearOpMode {

    //Conversão Pedro Visu para Road Runner
    private static Pose2d visualizer(double x, double y, double headingDeg) {
        return new Pose2d(y - 72, 72 - x, Math.toRadians(headingDeg - 90));
    }

    //Pose Inicial/Ponto de Partida
    private final Pose2d start = visualizer(56, 8, -90);

    //Pontos
    private final Pose2d p1  = visualizer(56, 26.75, 180);
    private final Pose2d p2  = visualizer(56, 9, 180);
    private final Pose2d p3  = visualizer(9, 9, 180);
    private final Pose2d p4  = visualizer(23.93, 29.95, 180);
    private final Pose2d p5  = visualizer(23.9, 117.7, 90);
    private final Pose2d p6  = visualizer(63, 117.55, 90);
    private final Pose2d p7  = visualizer(63, 133, 90);
    private final Pose2d p8  = visualizer(63, 117.93, 90);
    private final Pose2d p9  = visualizer(48.5, 118, 90);
    private final Pose2d p10 = visualizer(48.5, 128, 90);
    private final Pose2d p11 = visualizer(46.8, 127, 100);
    private final Pose2d p12 = visualizer(11.3, 106.55, -150);
    private final Pose2d p13 = visualizer(9.1, 107, 180);

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap, start);

        //Trajetoria
        Action trajetoria = drive.actionBuilder(start)

                .strafeToLinearHeading(p1.position, p1.heading.toDouble())

                .strafeTo(p2.position)
                .strafeTo(p3.position)
                .strafeTo(p4.position)

                .turnTo(p5.heading.toDouble())

                .strafeTo(p5.position)
                .strafeTo(p6.position)
                .strafeTo(p7.position)
                .strafeTo(p8.position)
                .strafeTo(p9.position)
                .strafeTo(p10.position)

                .strafeToLinearHeading(p11.position, p11.heading.toDouble())
                .strafeToLinearHeading(p12.position, p12.heading.toDouble())
                .strafeToLinearHeading(p13.position, p13.heading.toDouble())

                .build();

        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(trajetoria);
    }
}