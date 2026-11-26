package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Ceifador;
import org.firstinspires.ftc.teamcode.Subsystems.IntT;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

@TeleOp(name = "FieldCentric RR")
public class FieldCentricRR extends LinearOpMode {

    private static final double LIMIAR_TRIGGER = 0.1;

    @Override
    public void runOpMode() throws InterruptedException {

        // ---------- SUBSYSTEMS ----------
        Shooter shooter = new Shooter(hardwareMap);
        IntT intT = new IntT(hardwareMap);
        Ceifador ceifador = new Ceifador(hardwareMap);

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        double offset = 0;

        waitForStart();

        // ---------- LOOP ----------
        while (opModeIsActive()) {

            // ---------- DRIVER ----------
            drive.updatePoseEstimate();

            if (gamepad1.options) {offset = drive.localizer.getPose().heading.toDouble();}

            Vector2d input = new Vector2d(-gamepad1.left_stick_y, -gamepad1.left_stick_x);

            Rotation2d botHeading = Rotation2d.exp(drive.localizer.getPose().heading.toDouble() - offset);

            Vector2d rotated = botHeading.inverse().times(input);

            drive.setDrivePowers(new PoseVelocity2d(rotated, -gamepad1.right_stick_x));

            // ---------- SHOOTER ----------
            shooter.atirar(gamepad1.right_trigger); // liga, espera 2s, abre portinha + intT
            shooter.update(); // todo loop, sempre

            // ---------- INTAKE/TRANSFER (manual, só fora da sequência de tiro) ----------
            if (!shooter.ocupado()) {
                if (gamepad1.left_trigger > LIMIAR_TRIGGER) intT.ligar();
                else intT.desligar();
            }

            // ---------- CEIFADOR ----------
            ceifador.controlar(gamepad1.right_bumper);

            // ---------- TELEMETRIA ----------
            telemetry.addData("RPM alvo: ", Shooter.TARGET_RPM);
            telemetry.addData("RPM atual: ", shooter.getRPM());
            telemetry.addData("Potência shooter: ", shooter.getPower());
            telemetry.addData("Ângulação: ", Math.toDegrees(botHeading.toDouble()));
            telemetry.update();
        }

        // ---------- FIM ----------
        shooter.desligar();
        shooter.update();
        intT.desligar();
    }
}