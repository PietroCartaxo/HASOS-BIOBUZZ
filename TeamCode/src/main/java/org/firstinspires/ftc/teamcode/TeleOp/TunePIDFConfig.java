package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.dashboard.config.Config;

// Variaveis ajustaveis pelo Dashboard (classe separada do OpMode)
@Config
public class TunePIDFConfig {
    public static double TICKS_PER_REV = 28;
    public static double TARGET_RPM = 900;
    public static double kF = 0.00089; // feedforward: potência por tick/s
    public static double kP = 0.001;   // proporcional: ajustar no teste
    public static double kD = 0.0;     // derivativo: deixa 0 por enquanto
    public static double MAX_POWER = 1.0; // trava de segurança
    public static boolean SO_GIRAR = false;     // true = só gira o shooter, sem servo/intT
    public static double LIMIAR_TRIGGER = 0.1;  // gatilho acima disso = atirar
    public static double TEMPO_SPINUP = 2.0;    // segundos esperando o shooter acelerar
    public static double SERVO_FECHADO = 0.24;  // calibrar no robô
    public static double SERVO_ABERTO = 0.0;    // calibrar no robô
    public static double PASSO_KF = 0.00005;
    public static double PASSO_KP = 0.0005;
    public static double PASSO_KD = 0.00005;
    public static double PASSO_RPM = 20;
}
