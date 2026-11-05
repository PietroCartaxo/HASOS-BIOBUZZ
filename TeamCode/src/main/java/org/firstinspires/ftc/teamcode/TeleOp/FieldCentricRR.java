package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@TeleOp(name = "FieldCentric RR")
public class FieldCentricRR extends LinearOpMode {

    // ---------- SHOOTER ----------
    static final double TICKS_PER_REV = 28;
    static final double TARGET_RPM = 2800;

    // ---------- Intake/Shooter ---------
    static final double intTP = 1.0;

    static final double kF = 0.000352; // feedforward: potência por tick/s
    static final double kP = 0.001;    // proporcional: ponto de partida, ajustar no teste
    static final double kD = 0.0;      // derivativo: deixa 0 por enquanto

    static final double MAX_POWER = 0.6; // trava de segurança

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotorEx shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        boolean ligado = false;

        double prevError = 0;
        boolean primeiraVez = true;
        ElapsedTime timer = new ElapsedTime();

        DcMotorEx intT = hardwareMap.get(DcMotorEx.class, "intT");

        MecanumDrive drive =
                new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        double offset = 0;

        waitForStart();
        timer.reset();

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
            intT.setPower(gamepad1.left_trigger > 0.5 ? intTP : 0);

            // ---------- SHOOTER COM PIDF ----------
            ligado = gamepad1.right_trigger > 0.5;

            double dt = timer.seconds();
            timer.reset();

            double currentTPS = shooter.getVelocity();
            double targetTPS = TARGET_RPM / 60.0 * TICKS_PER_REV;
            double power = 0;

            if (ligado) {
                double error = targetTPS - currentTPS;
                double derivative = (primeiraVez || dt <= 0) ? 0 : (error - prevError) / dt;
                prevError = error;
                primeiraVez = false;

                power = kF * targetTPS + kP * error + kD * derivative;
                power = Range.clip(power, 0.0, MAX_POWER);
            } else {
                primeiraVez = true; // zera o derivativo na próxima vez que ligar
            }

            shooter.setPower(power);

            // ---------- TELEMETRIA ----------
            telemetry.addData("Ligado", ligado);
            telemetry.addData("RPM alvo", TARGET_RPM);
            telemetry.addData("RPM atual", currentTPS / TICKS_PER_REV * 60.0);
            telemetry.addData("TPS alvo", targetTPS);
            telemetry.addData("TPS atual", currentTPS);
            telemetry.addData("Potência", power);
            telemetry.addData("Heading", Math.toDegrees(botHeading.toDouble()));
            telemetry.update();
        }
    }
}