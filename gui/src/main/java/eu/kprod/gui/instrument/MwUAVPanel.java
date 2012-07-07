/**
 * Copyright (C) 2012 @author treym (Trey Marc)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.kprod.gui.instrument;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import eu.kprod.ds.MwSensorClass;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassServo;
import eu.kprod.gui.MwConfiguration;
import eu.kprod.msp.MSP;

/**
 * resources : /uav/*.png
 * 
 */
public class MwUAVPanel extends MwInstrumentJPanel {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;
   
    
    private Image[] uavImages = new Image[14];

    // position for drawing in the png uavImages
    private static final int[] UAV_TRI_MOTOR_X = {  71, 121, 21 };
    private static final int[] UAV_TRI_MOTOR_Y = { 129,  79, 79 };
    
    private static final int[] UAV_TRI_SERVO_INDEX = { 5 };
    private static final int[] UAV_TRI_SERVO_X = { 41 };
    private static final int[] UAV_TRI_SERVO_Y = { 151 };
    
    private static final int[] UAV_QUADP_MOTOR_Y = { 179, 129, 129, 79 };
    private static final int[] UAV_QUADP_MOTOR_X = {  76, 126,  26, 76 };
    
    private static final int[] UAV_QUADX_MOTOR_X = { 121, 121,  41, 41 };
    private static final int[] UAV_QUADX_MOTOR_Y = { 169,  79, 169, 79 };
    
    
    private final double[] motor = new double[8];
    private final double[] servo = new double[8];
    
    private int uavType = MSP.UAV_TRI;

    public MwUAVPanel( MwConfiguration conf) {
        super(new Dimension(170, 200),conf);
        super.setBarMax(67);
        super.setBarWidth(8);

        for (int i = 1; i < uavImages.length; i++) {
            uavImages[i] = super.getImage("uav/" + i + ".png");
        }

    }

    /**************** main Mix Table ******************/
    // #ifdef BI
    // motor[0] = PIDMIX(+1, 0, 0); //LEFT
    // motor[1] = PIDMIX(-1, 0, 0); //RIGHT
    // servo[4] = constrain(1500 + YAW_DIRECTION * (axisPID[YAW] +
    // axisPID[PITCH]), 1020, 2000); //LEFT
    // servo[5] = constrain(1500 + YAW_DIRECTION * (axisPID[YAW] -
    // axisPID[PITCH]), 1020, 2000); //RIGHT
    // #endif

    // #ifdef Y4
    // motor[0] = PIDMIX(+0,+1,-1); //REAR_1 CW
    // motor[1] = PIDMIX(-1,-1, 0); //FRONT_R CCW
    // motor[2] = PIDMIX(+0,+1,+1); //REAR_2 CCW
    // motor[3] = PIDMIX(+1,-1, 0); //FRONT_L CW
    // #endif
    // #ifdef Y6
    // motor[0] = PIDMIX(+0,+4/3,+1); //REAR
    // motor[1] = PIDMIX(-1,-2/3,-1); //RIGHT
    // motor[2] = PIDMIX(+1,-2/3,-1); //LEFT
    // motor[3] = PIDMIX(+0,+4/3,-1); //UNDER_REAR
    // motor[4] = PIDMIX(-1,-2/3,+1); //UNDER_RIGHT
    // motor[5] = PIDMIX(+1,-2/3,+1); //UNDER_LEFT
    // #endif
    // #ifdef HEX6
    // motor[0] = PIDMIX(-1/2,+1/2,+1); //REAR_R
    // motor[1] = PIDMIX(-1/2,-1/2,-1); //FRONT_R
    // motor[2] = PIDMIX(+1/2,+1/2,+1); //REAR_L
    // motor[3] = PIDMIX(+1/2,-1/2,-1); //FRONT_L
    // motor[4] = PIDMIX(+0 ,-1 ,+1); //FRONT
    // motor[5] = PIDMIX(+0 ,+1 ,-1); //REAR
    // #endif
    // #ifdef HEX6X
    // motor[0] = PIDMIX(-1/2,+1/2,+1); //REAR_R
    // motor[1] = PIDMIX(-1/2,-1/2,+1); //FRONT_R
    // motor[2] = PIDMIX(+1/2,+1/2,-1); //REAR_L
    // motor[3] = PIDMIX(+1/2,-1/2,-1); //FRONT_L
    // motor[4] = PIDMIX(-1 ,+0 ,-1); //RIGHT
    // motor[5] = PIDMIX(+1 ,+0 ,+1); //LEFT
    // #endif
    // #ifdef OCTOX8
    // motor[0] = PIDMIX(-1,+1,-1); //REAR_R
    // motor[1] = PIDMIX(-1,-1,+1); //FRONT_R
    // motor[2] = PIDMIX(+1,+1,+1); //REAR_L
    // motor[3] = PIDMIX(+1,-1,-1); //FRONT_L
    // motor[4] = PIDMIX(-1,+1,+1); //UNDER_REAR_R
    // motor[5] = PIDMIX(-1,-1,-1); //UNDER_FRONT_R
    // motor[6] = PIDMIX(+1,+1,-1); //UNDER_REAR_L
    // motor[7] = PIDMIX(+1,-1,+1); //UNDER_FRONT_L
    // #endif
    // #ifdef OCTOFLATP
    // motor[0] = PIDMIX(+7/10,-7/10,+1); //FRONT_L
    // motor[1] = PIDMIX(-7/10,-7/10,+1); //FRONT_R
    // motor[2] = PIDMIX(-7/10,+7/10,+1); //REAR_R
    // motor[3] = PIDMIX(+7/10,+7/10,+1); //REAR_L
    // motor[4] = PIDMIX(+0 ,-1 ,-1); //FRONT
    // motor[5] = PIDMIX(-1 ,+0 ,-1); //RIGHT
    // motor[6] = PIDMIX(+0 ,+1 ,-1); //REAR
    // motor[7] = PIDMIX(+1 ,+0 ,-1); //LEFT
    // #endif
    // #ifdef OCTOFLATX
    // motor[0] = PIDMIX(+1 ,-1/2,+1); //MIDFRONT_L
    // motor[1] = PIDMIX(-1/2,-1 ,+1); //FRONT_R
    // motor[2] = PIDMIX(-1 ,+1/2,+1); //MIDREAR_R
    // motor[3] = PIDMIX(+1/2,+1 ,+1); //REAR_L
    // motor[4] = PIDMIX(+1/2,-1 ,-1); //FRONT_L
    // motor[5] = PIDMIX(-1 ,-1/2,-1); //MIDFRONT_R
    // motor[6] = PIDMIX(-1/2,+1 ,-1); //REAR_R
    // motor[7] = PIDMIX(+1 ,+1/2,-1); //MIDREAR_L
    // #endif
    // #ifdef VTAIL4
    // motor[0] = PIDMIX(+0,+1, -1/2); //REAR_R
    // motor[1] = PIDMIX(-1, -1, +0); //FRONT_R
    // motor[2] = PIDMIX(+0,+1, +1/2); //REAR_L
    // motor[3] = PIDMIX(+1, -1, -0); //FRONT_L
    // #endif
    void drawBarValue(final Graphics2D g2d) {

        switch (uavType) {
            case MSP.UAV_TRI:
                drawBar(g2d, 0, motor, null, UAV_TRI_MOTOR_X,  UAV_TRI_MOTOR_Y, YAXIS);
                drawBar(g2d, 0, servo, UAV_TRI_SERVO_INDEX, UAV_TRI_SERVO_X, UAV_TRI_SERVO_Y, XAXIS);

                break;
            case MSP.UAV_QUADP:
                drawBar(g2d, 0, motor, null, UAV_QUADP_MOTOR_X, UAV_QUADP_MOTOR_Y, YAXIS);

                break;
            case MSP.UAV_QUADX:
                drawBar(g2d, 0, motor, null, UAV_QUADX_MOTOR_X, UAV_QUADX_MOTOR_Y, YAXIS);

                break;
            default:
                break;
        }
        ;

    }




    private void drawUAV(final Graphics2D g2d) {

        final BufferedImage bi = new BufferedImage(getMaxRadiusY(),
                getMaxRadiusY(), BufferedImage.TYPE_INT_ARGB);
        final Graphics g = bi.getGraphics();
        g.drawImage(uavImages[uavType], 0, 0, null);

        // float[] scales = { 1.0f ,1.0f,1.0f,0.8f};
        // float[] offsets = new float[4];
        // RescaleOp rop = new RescaleOp(scales, offsets, null);

        g2d.drawImage(bi, null, 0, 0);

    }

    public int getUavType() {
        return uavType;
    }

    @Override
    public void paintComponent(final Graphics g) {

        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawUAV(g2d);
        drawBarValue(g2d);

    }

    @Override
    public void readNewValue(final Integer string, final int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void readNewValue(Class<? extends MwSensorClass> sensorClass, final String name, final Double value) {
       
        final int index = Integer.parseInt(name.charAt(name.length() - 1) + "");


        String sensor = sensorClass.getName();
        
        if (sensor.equals(MwSensorClassMotor.class.getName())) {
            motor[index] = value;
        }else if (sensor.equals(MwSensorClassServo.class.getName())){
            servo[index] = value;
        }

        repaint();
    }

    @Override
    void resetAllValuesImpl() {

    }

    public void setUavType(final int uAVTYPE) {
        uavType = uAVTYPE;
        repaint();
    }

}
