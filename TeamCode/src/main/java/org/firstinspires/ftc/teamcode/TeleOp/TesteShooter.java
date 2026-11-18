package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

@TeleOp(name = "Teste Shooter", group = "Teste")
public class TesteShooter extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Shooter shooter = new Shooter(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.right_trigger > 0.5) shooter.ligar();
            else shooter.desligar();
            shooter.update();

            telemetry.addData("RPM atual", shooter.getRPM());
            telemetry.addData("Potência", shooter.getPower());
            telemetry.update();
        }
    }
}
