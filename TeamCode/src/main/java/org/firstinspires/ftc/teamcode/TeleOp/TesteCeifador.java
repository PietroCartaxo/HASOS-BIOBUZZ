package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.Ceifador;

@TeleOp(name = "Teste Ceifador", group = "Teste")
public class TesteCeifador extends LinearOpMode {

    @Override
    public void runOpMode() {
        Ceifador ceifador = new Ceifador(hardwareMap);

        telemetry.addLine("Pronto. Ceifador começa em CIMA.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // A: alterna cima/baixo (controlar detecta o clique)
            ceifador.controlar(gamepad1.a);

            // Dpad: força posição direta
            if (gamepad1.dpad_up)   ceifador.subir();
            if (gamepad1.dpad_down) ceifador.descer();

            // Bumpers: testa as Actions do Road Runner (bloqueia até terminar)
            if (gamepad1.left_bumper)  rodar(ceifador.ceifadorUp());
            if (gamepad1.right_bumper) rodar(ceifador.ceifadorDown());

            telemetry.addLine("A = toggle | Dpad cima/baixo = subir/descer");
            telemetry.addLine("LB/RB = Action up/down (Road Runner)");
            telemetry.update();
        }
    }

    // Roda uma Action até terminar
    private void rodar(Action acao) {
        TelemetryPacket packet = new TelemetryPacket();
        while (opModeIsActive() && acao.run(packet)) {
            // espera a Action terminar
        }
    }
}