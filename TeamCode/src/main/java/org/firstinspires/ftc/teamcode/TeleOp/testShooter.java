package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "testShooter")
public class testShooter extends LinearOpMode {

    // 0.5 de potência ≈ 3000 RPM (metade dos 6000 RPM)
    static final double POTENCIA = 0.58;

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotorEx shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // shooter.setDirection(DcMotorSimple.Direction.REVERSE); // se girar ao contrário

        boolean ligado = false;

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) ligado = true;   // A liga
            if (gamepad1.b) ligado = false;  // B desliga

            shooter.setPower(ligado ? POTENCIA : 0);

            telemetry.addData("Ligado", ligado);
            telemetry.addData("Potência", ligado ? POTENCIA : 0);
            telemetry.update();
        }

        shooter.setPower(0);
    }
}