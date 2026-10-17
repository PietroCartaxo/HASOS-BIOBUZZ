package com.example.meepmeep;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeep {
    public static void main(String[] args) {
        com.noahbres.meepmeep.MeepMeep meepMeep = new com.noahbres.meepmeep.MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        double S = 24;   // lado do quadrado
        double R = 8;    // raio das curvas

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(R, 0, 0))
                .setTangent(0)
                .lineToX(S - R)
                .splineTo(new Vector2d(S, R), Math.PI / 2)
                .lineToY(S - R)
                .splineTo(new Vector2d(S - R, S), Math.PI)
                .lineToX(R)
                .splineTo(new Vector2d(0, S - R), -Math.PI / 2)
                .lineToY(R)
                .splineTo(new Vector2d(R, 0), 0)
                .turnTo(Math.PI)
                .build());

        meepMeep.setBackground(com.noahbres.meepmeep.MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}