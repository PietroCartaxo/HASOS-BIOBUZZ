package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Enable Dashboard")
public class EnableDash extends LinearOpMode {
    @Override
    public void runOpMode() {
        FtcDashboard.getInstance().isEnabled();
        waitForStart();
    }
}
