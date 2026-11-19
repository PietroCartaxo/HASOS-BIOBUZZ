package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class Shooter {
    private DcMotorEx shooter1;
    private DcMotorEx shooter2;

    // ---------- SHOOTER ----------
    private static final double TICKS_PER_REV = 28;
    public static final double TARGET_RPM = 2500;

    private static final double kF = 0.000352; // feedforward: potência por tick/s
    private static final double kP = 0.001;    // proporcional: ajustar no teste
    private static final double kD = 0.0;      // derivativo: deixa 0 por enquanto

    private static final double MAX_POWER = 1.0; // trava de segurança

    private double targetRPM = 0;
    private double prevError = 0;
    private boolean primeiraVez = true;
    private ElapsedTime timer = new ElapsedTime();
    private double power = 0;

    public Shooter(HardwareMap hardwareMap) {
        shooter1 = hardwareMap.get(DcMotorEx.class, "shooter1");
        shooter2 = hardwareMap.get(DcMotorEx.class, "shooter2");

        shooter1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // ---------- COMANDOS: só mudam o alvo ----------
    public void ligar() {
        targetRPM = TARGET_RPM;
    }

    public void desligar() {
        targetRPM = 0;
    }

    // ---------- PIDF: único lugar que mexe nos motores ----------
    public void update() {
        // ETAPA 1: tempo desde a última volta
        double dt = timer.seconds();
        timer.reset();

        // ETAPA 2: medir
        double currentTPS = shooter1.getVelocity();


        // ETAPA 3: decidir a potência
        if (targetRPM > 0) {
            double targetTPS = targetRPM / 60.0 * TICKS_PER_REV;
            double error = targetTPS - currentTPS;
            double derivative = (primeiraVez || dt <= 0) ? 0 : (error - prevError) / dt;
            prevError = error;
            primeiraVez = false;

            power = kF * targetTPS + kP * error + kD * derivative;
            power = Range.clip(power, 0.0, MAX_POWER);
        } else {
            power = 0;
            primeiraVez = true;
        }

        // ETAPA 4: aplicar
        shooter1.setPower(power);
        shooter2.setPower(power);
    }

    // ---------- LEITURA (telemetria) ----------
    public double getRPM() {
        return shooter1.getVelocity() / TICKS_PER_REV * 60.0;
    }

    public double getPower() {
        return power;
    }
}