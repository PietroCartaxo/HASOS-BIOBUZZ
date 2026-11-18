package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.IntT;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

@TeleOp(name = "FieldCentric RR")
public class FieldCentricRR extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // ---------- SUBSYSTEMS ----------
        Shooter shooter = new Shooter(hardwareMap);
        IntT intake = new IntT(hardwareMap);

        MecanumDrive drive =
                new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        double offset = 0;

        waitForStart();

        // ---------- LOOP ----------
        while (opModeIsActive()) {

            // ---------- DRIVER ----------
            drive.updatePoseEstimate();

            if (gamepad1.options) {
                offset = drive.localizer.getPose().heading.toDouble();
            }

            Vector2d input = new Vector2d(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x
            );

            Rotation2d botHeading =
                    Rotation2d.exp(
                            drive.localizer.getPose().heading.toDouble()
                                    - offset
                    );

            Vector2d rotated = botHeading.inverse().times(input);

            drive.setDrivePowers(
                    new PoseVelocity2d(
                            rotated,
                            -gamepad1.right_stick_x
                    )
            );

            // ---------- INTAKE / TRANSFER ----------
            if (gamepad1.left_trigger > 0.5) intake.ligar();
            else intake.desligar();

            // ---------- SHOOTER ----------
            if (gamepad1.right_trigger > 0.5) shooter.ligar();
            else shooter.desligar();
            shooter.update();   // sem essa linha o shooter não faz nada

            // ---------- TELEMETRIA ----------
            telemetry.addData("RPM alvo", Shooter.TARGET_RPM);
            telemetry.addData("RPM atual", shooter.getRPM());
            telemetry.addData("Potência shooter", shooter.getPower());
            telemetry.addData("Heading", Math.toDegrees(botHeading.toDouble()));
            telemetry.update();
        }
    }
}