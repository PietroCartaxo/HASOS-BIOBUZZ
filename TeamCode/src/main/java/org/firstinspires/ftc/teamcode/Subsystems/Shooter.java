package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Config
public class Shooter {
    private DcMotorEx shooter1;
    private DcMotorEx shooter2;
    private Servo servoShooter;
    private IntT intT;

    // ---------- SHOOTER ----------
    public static double TICKS_PER_REV = 28;
    public static double TARGET_RPM = 900;

    public static double kF = 0.00089; // feedforward: potência por tick/s
    public static double kP = 0.001;    // proporcional: ajustar no teste
    public static double kD = 0.0;      // derivativo: deixa 0 por enquanto

    public static double MAX_POWER = 1.0; // trava de segurança

    // ---------- SEQUÊNCIA DE TIRO ----------
    public static boolean SO_GIRAR = false;        // true = TunePIDF só gira o shooter, sem servo/intT
    public static double LIMIAR_TRIGGER = 0.1;  // gatilho acima disso = atirar
    public static double TEMPO_SPINUP = 2.0;    // segundos esperando o shooter acelerar
    public static double SERVO_FECHADO = 0.0;   // calibrar no robô
    public static double SERVO_ABERTO = 0.5;    // calibrar no robô

    private enum Estado { PARADO, ACELERANDO, ATIRANDO }
    private Estado estado = Estado.PARADO;
    private ElapsedTime timerTiro = new ElapsedTime();

    private double targetRPM = 0;
    private double prevError = 0;
    private boolean primeiraVez = true;
    private ElapsedTime timer = new ElapsedTime();
    private double power = 0;

    public Shooter(HardwareMap hardwareMap) {
        shooter1 = hardwareMap.get(DcMotorEx.class, "shooter1");
        shooter2 = hardwareMap.get(DcMotorEx.class, "shooter2");
        servoShooter = hardwareMap.get(Servo.class, "servoShooter");
        intT = new IntT(hardwareMap);

        shooter1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        servoShooter.setPosition(SERVO_FECHADO);
        servoShooter.setDirection(Servo.Direction.REVERSE);
    }

    // ---------- COMANDOS: só mudam o alvo ----------
    public void ligar() {
        targetRPM = TARGET_RPM;
    }

    public void desligar() {
        targetRPM = 0;
    }

    // ---------- SEQUÊNCIA: chamar todo loop com gamepad.right_trigger ----------
    // liga shooter -> espera TEMPO_SPINUP -> abre portinha + liga intT
    // soltar o gatilho em qualquer etapa desfaz tudo
    public void atirar(double trigger) {
        boolean apertado = trigger > LIMIAR_TRIGGER;

        switch (estado) {
            case PARADO:
                if (apertado) {
                    ligar();
                    timerTiro.reset();
                    estado = Estado.ACELERANDO;
                }
                break;

            case ACELERANDO:
                if (!apertado) {
                    parar();
                } else if (timerTiro.seconds() >= TEMPO_SPINUP) {
                    servoShooter.setPosition(SERVO_ABERTO);
                    intT.ligar();
                    estado = Estado.ATIRANDO;
                }
                break;

            case ATIRANDO:
                if (!apertado) {
                    parar();
                }
                break;
        }
    }

    private void parar() {
        desligar();
        servoShooter.setPosition(SERVO_FECHADO);
        intT.desligar();
        estado = Estado.PARADO;
    }

    // true enquanto a sequência controla o intT (TeleOp não deve mexer nele)
    public boolean ocupado() {
        return estado != Estado.PARADO;
    }

    // ---------- PIDF: único lugar que mexe nos motores do shooter ----------
    public void update() {
        // ETAPA 1: tempo desde a última volta
        double dt = timer.seconds();
        timer.reset();

        // ETAPA 2: medir
        double currentTPS = shooter2.getVelocity(); // encoder do shooter1 não funciona, lê do shooter2


        // ETAPA 3: decidir a potência
        if (targetRPM > 0) {
            targetRPM = TARGET_RPM; // telemetria acompanha o Dashboard
            double targetTPS = TARGET_RPM / 60.0 * TICKS_PER_REV; // lê ao vivo: dá pra mudar pelo Dashboard com o shooter girando
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
        return shooter2.getVelocity() / TICKS_PER_REV * 60.0;
    }

    public double getTargetRPM() {
        return targetRPM;
    }

    public double getPower() {
        return power;
    }
}
