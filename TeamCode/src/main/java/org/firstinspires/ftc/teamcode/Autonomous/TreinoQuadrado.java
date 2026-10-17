package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Quadrado", group = "Autonomous")
public class TreinoQuadrado extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        double S = 24;   // lado do quadrado (polegadas)
        double R = 8;    // raio das curvas (polegadas), precisa ser menor que S / 2

        // Largada em (R, 0), apontado para a direita (heading 0)
        Pose2d start = new Pose2d(R, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, start);

        Action rota = drive.actionBuilder(start)
                .setTangent(0)                                          // começa andando para a direita
                .lineToX(S - R)                                         // 1: reta de baixo
                .splineTo(new Vector2d(S, R), Math.PI / 2)              // 2: curva, passa a subir
                .lineToY(S - R)                                         // 3: reta da direita
                .splineTo(new Vector2d(S - R, S), Math.PI)              // 4: curva, passa a ir para a esquerda
                .lineToX(R)                                             // 5: reta de cima
                .splineTo(new Vector2d(0, S - R), -Math.PI / 2)         // 6: curva, passa a descer
                .lineToY(R)                                             // 7: reta da esquerda
                .splineTo(new Vector2d(R, 0), 0)                        // 8: curva, volta à largada
                .turnTo(Math.PI)                                        // 9: gira parado para a esquerda
                .build();

        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(rota);
    }
}