package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "testShooter")
public class testShooter extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        // INIT: pega o motor (o nome tem que ser igual ao da configuração do robô)
        DcMotorEx shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        waitForStart(); // espera apertar play

        // LOOP: roda enquanto o opmode estiver ativo
        while (opModeIsActive()) {
            double power = gamepad1.right_trigger; // 0 a 1
            shooter.setPower(power);

            telemetry.addData("Power", power);
            telemetry.addData("Velocidade (ticks/s)", shooter.getVelocity());
            telemetry.update();
        }

        // STOP: depois do while,0
        shooter.setPower(0);
    }
}