/******************************************************************************************************************
* File:LessThan10k.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*   1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* This class is the filter that is used to filter out the data points less than 10k meter of altitude.
* Hence this filter will have two output ports.
*
* Parameters:       None
*
* Internal Methods: None
*
******************************************************************************************************************/


import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;


public class LessThan10k extends FilterFramework
{

    public LessThan10k(){
        super(1,2);
    }


    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return buffer.array();
    }

    public void run()
    {

        Calendar TimeStamp = Calendar.getInstance();
        SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

        int bytesread = 0;                  // Number of bytes read from the input file.
        int byteswritten = 0;               // Number of bytes written to the stream.
        byte databyte = 0;                  // The byte of data read from the file
        int id;

        int firstHalf=28;
        byte[] byteArray = new byte[firstHalf];
        int secondHalf=36;
        byte[] altitudeArray;
        int altitudeSize=8;
        long altitudeValue;
        Double measurement;
        int i,outport;


        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::10k Filter Reading ");

        while (true)
        {
            /*************************************************************
            *   Here we read a byte and write a byte
            *************************************************************/

            try
            {
                // This for loop copies the first set of 40 bytes
                // This is first
                for (i=0; i<firstHalf; i++ )
                {
                    byteArray[i] = ReadFilterInputPort(0);  // This is where we read the byte from the stream...
                    bytesread++;                        // Increment the byte count
                }
                altitudeValue =0;
                // This is to get next 8 bytes.
                // this would be the altitude value
                for (i=0; i<altitudeSize; i++ )
                {
                   databyte = ReadFilterInputPort(0);
                   altitudeValue = altitudeValue | (databyte & 0xFF);
                   if (i != altitudeSize-1)                   // If this is not the last byte, then slide the
                    {                                               // previously appended byte to the left by one byte
                        altitudeValue = altitudeValue << 8;             // to make room for the next byte we append to the
                                                                    // measurement
                    }
                    bytesread++;
                }
                measurement = Double.longBitsToDouble(altitudeValue);
                //Assign the outport as 0 and change it to 1 if value is less than 10000
                outport = 0;
                if(measurement < 10000)
                    outport = 1;
                // First write byearray, then the measurement value and then the remaing half of the frame
                for(i=0;i<firstHalf;i++){
                    WriteFilterOutputPort(outport,byteArray[i]);
                    byteswritten++;
                }
                altitudeArray = longToBytes(Double.doubleToLongBits(measurement));
                for(i=0;i<altitudeSize;i++){
                    WriteFilterOutputPort(outport,altitudeArray[i]);
                    byteswritten++;
                }
                for(i=0;i<secondHalf;i++){
                    WriteFilterOutputPort(outport,ReadFilterInputPort(0));
                    byteswritten++;
                }

            } // try

            catch (EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

   } // run

} // MiddleFilter
