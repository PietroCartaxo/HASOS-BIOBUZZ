package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.IntT;

@TeleOp(name = "Teste IntT", group = "Teste")
public class TesteIntT extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        IntT intT = new IntT(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.left_trigger > 0.5) intT.ligar();
            else intT.desligar();

            telemetry.addData("Gatilho esquerdo", gamepad1.left_trigger);
            telemetry.update();
        }
    }
}