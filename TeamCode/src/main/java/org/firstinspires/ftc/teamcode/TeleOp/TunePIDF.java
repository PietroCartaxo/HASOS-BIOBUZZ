package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Subsystems.IntT;

@TeleOp(name = "Tune PIDF Shooter", group = "Teste")
public class TunePIDF extends LinearOpMode {

    // ---------- SHOOTER (ajustáveis pelo Dashboard ou pelo controle) ----------



    // ---------- SEQUÊNCIA DE TIRO ----------

    // ---------- TAMANHO DO PASSO DE CADA APERTO ----------

    private enum Estado { PARADO, ACELERANDO, ATIRANDO }

    // estado anterior dos botões (pra detectar só o aperto, não o segurar)
    private boolean pUp, pDown, pLeft, pRight, pY, pA, pB, pX;

    @Override
    public void runOpMode() throws InterruptedException {
        // ---------- HARDWARE ----------
        DcMotorEx shooter1 = hardwareMap.get(DcMotorEx.class, "shooter1");
        DcMotorEx shooter2 = hardwareMap.get(DcMotorEx.class, "shooter2");
        Servo servoShooter = hardwareMap.get(Servo.class, "servoShooter");
        IntT intT = new IntT(hardwareMap);

        shooter1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        servoShooter.setPosition(TunePIDFConfig.SERVO_FECHADO);

        // ---------- ESTADO ----------
        Estado estado = Estado.PARADO;
        ElapsedTime timerTiro = new ElapsedTime();
        ElapsedTime timer = new ElapsedTime();
        double targetRPM = 0;   // 0 = desligado
        double prevError = 0;
        boolean primeiraVez = true;
        double power = 0;

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addLine("Pronto. Aperte START.");
        telemetry.update();
        waitForStart();
        timer.reset();

        while (opModeIsActive()) {
            // ---------- AJUSTE PELO CONTROLE ----------
            boolean up = gamepad1.dpad_up, down = gamepad1.dpad_down;
            boolean left = gamepad1.dpad_left, right = gamepad1.dpad_right;
            boolean y = gamepad1.y, a = gamepad1.a, b = gamepad1.b, x = gamepad1.x;

            if (up && !pUp) TunePIDFConfig.kF += TunePIDFConfig.PASSO_KF;
            if (down && !pDown) TunePIDFConfig.kF -= TunePIDFConfig.PASSO_KF;
            if (right && !pRight) TunePIDFConfig.kP += TunePIDFConfig.PASSO_KP;
            if (left && !pLeft) TunePIDFConfig.kP -= TunePIDFConfig.PASSO_KP;
            if (y && !pY) TunePIDFConfig.TARGET_RPM += TunePIDFConfig.PASSO_RPM;
            if (a && !pA) TunePIDFConfig.TARGET_RPM -= TunePIDFConfig.PASSO_RPM;
            if (b && !pB) TunePIDFConfig.kD += TunePIDFConfig.PASSO_KD;
            if (x && !pX) TunePIDFConfig.kD -= TunePIDFConfig.PASSO_KD;

            pUp = up; pDown = down; pLeft = left; pRight = right;
            pY = y; pA = a; pB = b; pX = x;

            // sem valores negativos
            TunePIDFConfig.kF = Math.max(0, TunePIDFConfig.kF);
            TunePIDFConfig.kP = Math.max(0, TunePIDFConfig.kP);
            TunePIDFConfig.kD = Math.max(0, TunePIDFConfig.kD);
            TunePIDFConfig.TARGET_RPM = Math.max(0, TunePIDFConfig.TARGET_RPM);

            // ---------- SHOOTER: só muda o alvo ----------
            boolean apertado = gamepad1.right_trigger > TunePIDFConfig.LIMIAR_TRIGGER;

            if (TunePIDFConfig.SO_GIRAR) {
                targetRPM = apertado ? TunePIDFConfig.TARGET_RPM : 0;
            } else {
                // liga shooter -> espera TunePIDFConfig.TEMPO_SPINUP -> abre portinha + liga intT
                // soltar o gatilho em qualquer etapa desfaz tudo
                switch (estado) {
                    case PARADO:
                        if (apertado) {
                            targetRPM = TunePIDFConfig.TARGET_RPM;
                            timerTiro.reset();
                            estado = Estado.ACELERANDO;
                        }
                        break;

                    case ACELERANDO:
                        if (!apertado) {
                            targetRPM = 0;
                            servoShooter.setPosition(TunePIDFConfig.SERVO_FECHADO);
                            intT.desligar();
                            estado = Estado.PARADO;
                        } else if (timerTiro.seconds() >= TunePIDFConfig.TEMPO_SPINUP) {
                            servoShooter.setPosition(TunePIDFConfig.SERVO_ABERTO);
                            intT.ligar();
                            estado = Estado.ATIRANDO;
                        }
                        break;

                    case ATIRANDO:
                        if (!apertado) {
                            targetRPM = 0;
                            servoShooter.setPosition(TunePIDFConfig.SERVO_FECHADO);
                            intT.desligar();
                            estado = Estado.PARADO;
                        }
                        break;
                }
            }

            // ---------- PIDF: único lugar que mexe nos motores do shooter ----------
            double dt = timer.seconds();
            timer.reset();

            double currentTPS = shooter2.getVelocity(); // encoder do shooter1 não funciona, lê do shooter2

            if (targetRPM > 0) {
                targetRPM = TunePIDFConfig.TARGET_RPM; // acompanha o Dashboard com o shooter girando
                double targetTPS = TunePIDFConfig.TARGET_RPM / 60.0 * TunePIDFConfig.TICKS_PER_REV;
                double error = targetTPS - currentTPS;
                double derivative = (primeiraVez || dt <= 0) ? 0 : (error - prevError) / dt;
                prevError = error;
                primeiraVez = false;

                power = TunePIDFConfig.kF * targetTPS + TunePIDFConfig.kP * error + TunePIDFConfig.kD * derivative;
                power = Range.clip(power, 0.0, TunePIDFConfig.MAX_POWER);
            } else {
                power = 0;
                primeiraVez = true;
            }

            shooter1.setPower(power);
            shooter2.setPower(power);

            // ---------- INTAKE/TRANSFER ----------
            if (estado == Estado.PARADO) {
                if (gamepad1.left_trigger > TunePIDFConfig.LIMIAR_TRIGGER) intT.ligar();
                else intT.desligar();
            }

            // ---------- TELEMETRIA ----------
            telemetry.addData("TESTE v2 - PASSO_RPM lido agora", TunePIDFConfig.PASSO_RPM);
            telemetry.addData("Target RPM", TunePIDFConfig.TARGET_RPM);
            telemetry.addData("Actual RPM", currentTPS / TunePIDFConfig.TICKS_PER_REV * 60.0);
            telemetry.addData("Power", power);
            telemetry.addData("kF", String.format("%.6f", TunePIDFConfig.kF));
            telemetry.addData("kP", String.format("%.6f", TunePIDFConfig.kP));
            telemetry.addData("kD", String.format("%.6f", TunePIDFConfig.kD));
            telemetry.update();
        }

        // ---------- FIM ----------
        shooter1.setPower(0);
        shooter2.setPower(0);
        servoShooter.setPosition(TunePIDFConfig.SERVO_FECHADO);
        intT.desligar();
    }
}
