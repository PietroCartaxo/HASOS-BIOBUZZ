package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.IntT;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

@TeleOp(name = "Teste IntSho", group = "Teste")

public class TesteIntSho extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Shooter shooter = new Shooter(hardwareMap);
        IntT intT = new IntT(hardwareMap);
        FtcDashboard dashboard = FtcDashboard.getInstance();
        waitForStart();

        while (opModeIsActive()){
            if (gamepad1.right_trigger > 0.5) shooter.ligar();
            else shooter.desligar();
            shooter.update();

            if (gamepad1.left_trigger > 0.5) intT.ligar();
            else intT.desligar();

            telemetry.addData("Gatilho esquerdo", gamepad1.left_trigger);
            telemetry.addData("RPM atual", shooter.getRPM());
            telemetry.addData("Potência", shooter.getPower());
        }
    }
}
