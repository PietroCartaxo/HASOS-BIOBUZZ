package org.firstinspires.ftc.teamcode.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Actions;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Ceifador {

    // ---------- Ceifador ---------
    private Servo servoLeft;
    private Servo servoRight;
    private boolean baixo = false;
    private boolean botaoAnterior = false;

    static final double POS_CIMA = 0.70;   // calibrar no robô
    static final double POS_BAIXO = 0.15;  // calibrar no robô
    static final double TEMPO_VIAGEM = 0.40; // segundos até a pá chegar

    public Ceifador(HardwareMap hardwareMap) {
        servoLeft  = hardwareMap.get(Servo.class, "servoLeft");
        servoRight = hardwareMap.get(Servo.class, "servoRight");
        servoLeft.setDirection(Servo.Direction.REVERSE); // lado espelhado
        subir();
    }

    public void subir() {
        servoLeft.setPosition(POS_CIMA);
        servoRight.setPosition(POS_CIMA);
        baixo = false;
    }

    public void descer() {
        servoLeft.setPosition(POS_BAIXO);
        servoRight.setPosition(POS_BAIXO);
        baixo = true;
    }

    public void toggle() {
        if (baixo) subir();
        else descer();
    }

    // Chamar todo loop do TeleOp: alterna só no clique
    public void controlar(boolean botao) {
        if (botao && !botaoAnterior) toggle();
        botaoAnterior = botao;
    }

    // ---------- Actions (Road Runner) ---------
    public Action ceifadorUp()  { return mover(false); }

    public Action ceifadorDown() { return mover(true); }

    private Action mover(boolean paraBaixo) {
        return new Action() {
            private double inicio = -1;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (inicio < 0) {
                    inicio = Actions.now();
                    if (paraBaixo) descer();
                    else subir();
                }
                return Actions.now() - inicio < TEMPO_VIAGEM;
            }
        };
    }
}