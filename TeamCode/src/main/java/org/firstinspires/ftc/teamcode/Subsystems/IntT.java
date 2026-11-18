package org.firstinspires.ftc.teamcode.Subsystems;


import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntT {

    // ---------- Intake/Transfer ---------
    private DcMotorEx intT;
    static final double intTP = 1.0;
    public IntT(HardwareMap hardwareMap){
        intT = hardwareMap.get(DcMotorEx.class, "intT");
        intT.setDirection(DcMotorEx.Direction.REVERSE);
    }

    public void ligar (){
        intT.setPower(intTP);
    }

    public void desligar(){
        intT.setPower(0.0);
    }
}

