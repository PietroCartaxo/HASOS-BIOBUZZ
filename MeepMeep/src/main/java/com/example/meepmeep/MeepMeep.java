package com.example.meepmeep;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class MeepMeep {

    //Cada aliança tem a sua conversão (Blue = Red girado 180°)
    interface Conversor {
        Pose2d de(double x, double y, double headingDeg);
    }

    private static Pose2d visualizerRed(double x, double y, double headingDeg) {
        return new Pose2d(y - 72, 72 - x, Math.toRadians(headingDeg - 90));
    }

    private static Pose2d visualizerBlue(double x, double y, double headingDeg) {
        return new Pose2d(72 - y, x - 72, Math.toRadians(headingDeg + 90));
    }

    //Trajeto único: os mesmos pontos para as duas alianças
    private static Action trajeto(RoadRunnerBotEntity bot, Conversor v) {
        //Pose Inicial/Ponto de Partida
        Pose2d start = v.de(56, 8, -90);

        //Pontos
        Pose2d p1  = v.de(56, 26.75, 180);
        Pose2d p2  = v.de(56, 9, 180);
        Pose2d p3  = v.de(9, 9, 180);
        Pose2d p4  = v.de(23.93, 29.95, 180);
        Pose2d p5  = v.de(23.9, 117.7, 90);
        Pose2d p6  = v.de(63, 117.55, 90);
        Pose2d p7  = v.de(63, 133, 90);
        Pose2d p8  = v.de(63, 117.93, 90);
        Pose2d p9  = v.de(48.5, 118, 90);
        Pose2d p10 = v.de(48.5, 128, 90);
        Pose2d p11 = v.de(46.8, 127, 100);
        Pose2d p12 = v.de(11.3, 106.55, -150);
        Pose2d p13 = v.de(9.1, 107, 180);

        return bot.getDrive().actionBuilder(start)

                .strafeToLinearHeading(p1.position, p1.heading.toDouble())

                .strafeTo(p2.position)
                .strafeTo(p3.position)
                .strafeTo(p4.position)

                .turnTo(p5.heading.toDouble())

                .strafeTo(p5.position)
                .strafeTo(p6.position)
                .strafeTo(p7.position)
                .strafeTo(p8.position)
                .strafeTo(p9.position)
                .strafeTo(p10.position)

                .strafeToLinearHeading(p11.position, p11.heading.toDouble())
                .strafeToLinearHeading(p12.position, p12.heading.toDouble())
                .strafeToLinearHeading(p13.position, p13.heading.toDouble())

                .build();
    }

    public static void main(String[] args) throws IOException {
        Image campo = ImageIO.read(new File("/C:/Users/elain/Downloads/Mapa Biobuzz.jpg/"));
        com.noahbres.meepmeep.MeepMeep meepMeep = new com.noahbres.meepmeep.MeepMeep(800);

        RoadRunnerBotEntity botRed = new DefaultBotBuilder(meepMeep)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(16, 16)
                .setColorScheme(new ColorSchemeRedDark())
                .build();

        RoadRunnerBotEntity botBlue = new DefaultBotBuilder(meepMeep)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(16, 16)
                .setColorScheme(new ColorSchemeBlueDark())
                .build();

        botRed.runAction(trajeto(botRed, MeepMeep::visualizerRed));
        botBlue.runAction(trajeto(botBlue, MeepMeep::visualizerBlue));

        meepMeep.setBackground(campo)
                .setDarkMode(true)
                .setBackgroundAlpha(1f)
                .addEntity(botRed)
                .addEntity(botBlue)
                .start();
    }
}