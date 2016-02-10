/******************************************************************************************************************
* File:MiddleFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*   1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* This class serves as an example for how to use the FilterRemplate to create a standard filter. This particular
* example is a simple "pass-through" filter that reads data from the filter's input port and writes data out the
* filter's output port.
*
* Parameters:       None
*
* Internal Methods: None
*
******************************************************************************************************************/


import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;  
import java.nio.ByteBuffer; 
import java.util.*; 


public class AltitudeConvertor extends FilterFramework
{
    
    public AltitudeConvertor(){
        super(1,1);
    }


    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return buffer.array();
    }

    public byte[] intToBytes(int x){
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(x);
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

        int MeasurementLength = 8;          // This is the legnth of all measurement
        int IdLength = 4;                   // This is the length of IDS in the byte

        byte[] byteArray;
        long measurement;

        double feet;              // This is temperature in Fahrenheit
        double meters;                 // This is temperature in Celsius
        int i;

        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::Temperature Conversion Reading ");

        while (true)
        {
            /*************************************************************
            *   Here we read a byte and write a byte
            *************************************************************/

            try
            {
                id = 0;

                for (i=0; i<IdLength; i++ )
                {
                    databyte = ReadFilterInputPort(0);  // This is where we read the byte from the stream...

                    id = id | (databyte & 0xFF);        // We append the byte on to ID...

                    if (i != IdLength-1)                // If this is not the last byte, then slide the
                    {                                   // previously appended byte to the left by one byte
                        id = id << 8;                   // to make room for the next byte we append to the ID

                    } // if

                    bytesread++;                        // Increment the byte count

                } // for

                


                measurement = 0;

                for (i=0; i<MeasurementLength; i++ )
                {
                    databyte = ReadFilterInputPort(0);
                    measurement = measurement | (databyte & 0xFF);  // We append the byte on to measurement...

                    if (i != MeasurementLength-1)                   // If this is not the last byte, then slide the
                    {                                               // previously appended byte to the left by one byte
                        measurement = measurement << 8;             // to make room for the next byte we append to the
                                                                    // measurement
                    } // if

                    bytesread++;                                    // Increment the byte count

                } // if


                if (id == 2)
                {
                    feet = Double.longBitsToDouble(measurement);
                    meters = feet * 0.3048;
                    byteArray = intToBytes(id);
                    for (i = 0; i < IdLength; i++) {
                        WriteFilterOutputPort(0,byteArray[i]);
                        byteswritten++;
                    }

                    byteArray = longToBytes(Double.doubleToLongBits(meters));
                    for (i = 0; i < MeasurementLength; i++) {
                        WriteFilterOutputPort(0,byteArray[i]);
                        byteswritten++;
                    }               

                }
                else
                {
                    byteArray = intToBytes(id);
                    for (i = 0; i < IdLength; i++) {
                        WriteFilterOutputPort(0,byteArray[i]);
                        byteswritten++;
                    }
                    byteArray = longToBytes(measurement);
                    for (i = 0; i < MeasurementLength; i++) {
                        WriteFilterOutputPort(0,byteArray[i]);
                        byteswritten++;
                    }

                }
            } // try

            catch (EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::Middle Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

   } // run

} // MiddleFilter