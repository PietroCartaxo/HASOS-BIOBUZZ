package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.IntT;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

@TeleOp(name = "Tune PIDF Shooter", group = "Teste")
@Config
public class TunePIDF extends LinearOpMode {

    // tamanho do passo de cada aperto
    public static double PASSO_KF = 0.00005;
    public static double PASSO_KP = 0.0005;
    public static double PASSO_KD = 0.00005;
    public static double PASSO_RPM = 20;

    // estado anterior dos botões (pra detectar só o aperto, não o segurar)
    private boolean pUp, pDown, pLeft, pRight, pY, pA, pB, pX;

    @Override
    public void runOpMode() throws InterruptedException {
        Shooter shooter = new Shooter(hardwareMap);
        IntT intT = new IntT(hardwareMap);

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addLine("Pronto. Aperte START.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // ---------- AJUSTE PELO CONTROLE ----------
            boolean up = gamepad1.dpad_up, down = gamepad1.dpad_down;
            boolean left = gamepad1.dpad_left, right = gamepad1.dpad_right;
            boolean y = gamepad1.y, a = gamepad1.a, b = gamepad1.b, x = gamepad1.x;

            if (up && !pUp) Shooter.kF += PASSO_KF;
            if (down && !pDown) Shooter.kF -= PASSO_KF;
            if (right && !pRight) Shooter.kP += PASSO_KP;
            if (left && !pLeft) Shooter.kP -= PASSO_KP;
            if (y && !pY) Shooter.TARGET_RPM += PASSO_RPM;
            if (a && !pA) Shooter.TARGET_RPM -= PASSO_RPM;
            if (b && !pB) Shooter.kD += PASSO_KD;
            if (x && !pX) Shooter.kD -= PASSO_KD;

            pUp = up; pDown = down; pLeft = left; pRight = right;
            pY = y; pA = a; pB = b; pX = x;

            // sem valores negativos
            Shooter.kF = Math.max(0, Shooter.kF);
            Shooter.kP = Math.max(0, Shooter.kP);
            Shooter.kD = Math.max(0, Shooter.kD);
            Shooter.TARGET_RPM = Math.max(0, Shooter.TARGET_RPM);

            // ---------- SHOOTER ----------
            if (Shooter.SO_GIRAR) {
                if (gamepad1.right_trigger > Shooter.LIMIAR_TRIGGER) shooter.ligar();
                else shooter.desligar();
            } else {
                shooter.atirar(gamepad1.right_trigger);
            }
            shooter.update(); // todo loop, sempre

            // ---------- INTAKE/TRANSFER ----------
            if (!shooter.ocupado()) {
                if (gamepad1.left_trigger > Shooter.LIMIAR_TRIGGER) intT.ligar();
                else intT.desligar();
            }

            // ---------- TELEMETRIA ----------
            telemetry.addData("Target RPM", Shooter.TARGET_RPM);
            telemetry.addData("Actual RPM", shooter.getRPM());
            telemetry.addData("Power", shooter.getPower());
            telemetry.addData("kF", String.format("%.6f", Shooter.kF));
            telemetry.addData("kP", String.format("%.6f", Shooter.kP));
            telemetry.addData("kD", String.format("%.6f", Shooter.kD));
            telemetry.update();
        }

        // ---------- FIM ----------
        shooter.desligar();
        shooter.update();
        intT.desligar();
    }
}
