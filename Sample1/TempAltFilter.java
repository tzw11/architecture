/******************************************************************************************************************
* File:MiddleFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* This class serves as an example for how to use the FilterRemplate to create a standard filter. This particular
* example is a simple "pass-through" filter that reads data from the filter's input port and writes data out the
* filter's output port.
*
* Parameters: 		None
*
* Internal Methods: None
*
******************************************************************************************************************/

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;  
import java.nio.ByteBuffer; 
import java.util.*; 


public class TempAltFilter extends FilterFramework
{
    public TempAltFilter(){
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


		int bytesread = 0;					// Number of bytes read from the input file.
		int byteswritten = 0;				// Number of bytes written to the stream.
		byte databyte = 0;					// The byte of data read from the file
    byte datacopy = 0;

    long measurement;				// This is the word used to store all measurements - conversions are illustrated.
    int id;							// This is the measurement id
    int i;							// This is a loop counter
    byte[] byteArray;

    int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
    int IdLength = 4;				// This is the length of IDs in the byte stream

		// Next we write a message to the terminal to let the world know we are alive...

		System.out.print( "\n" + this.getName() + "::TempAltFilter Reading ");

		while (true)
		{
			/*************************************************************
			*	Here we read a byte and write a byte
			*************************************************************/

			try
			{
				/*
        //original code
        databyte = ReadFilterInputPort(0);
				bytesread++;
				WriteFilterOutputPort(0, databyte);
				byteswritten++;
        */

        //READ INPUT BYTES

        /***************************************************************************
        // We know that the first data coming to this filter is going to be an ID and
        // that it is IdLength long. So we first decommutate the ID bytes.
        ****************************************************************************/

        id = 0;

        for (i=0; i<IdLength; i++ )
        {
          databyte = ReadFilterInputPort(0);	// This is where we read the byte from the stream...
          datacopy = databyte;

          id = id | (databyte & 0xFF);		// We append the byte on to ID...

          if (i != IdLength-1)				// If this is not the last byte, then slide the
          {									// previously appended byte to the left by one byte
            id = id << 8;					// to make room for the next byte we append to the ID

          } // if

          bytesread++;						// Increment the byte count

        } // for

        /****************************************************************************
        // Here we read measurements. All measurement data is read as a stream of bytes
        // and stored as a long value. This permits us to do bitwise manipulation that
        // is neccesary to convert the byte stream into data words. Note that bitwise
        // manipulation is not permitted on any kind of floating point types in Java.
        // If the id = 0 then this is a time value and is therefore a long value - no
        // problem. However, if the id is something other than 0, then the bits in the
        // long value is really of type double and we need to convert the value using
        // Double.longBitsToDouble(long val) to do the conversion which is illustrated.
        // below.
        *****************************************************************************/

        measurement = 0;

        for (i=0; i<MeasurementLength; i++ )
        {
          databyte = ReadFilterInputPort(0);
          measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

          if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
          {												// previously appended byte to the left by one byte
            measurement = measurement << 8;				// to make room for the next byte we append to the
                                  // measurement
          } // if

          bytesread++;									// Increment the byte count

        } // if

        //Wrtie to output only if id is 0 (time), 2 (altitude), or 4 (temperature)
        if ( id == 0 || id == 2 || id == 4)
        {
          //TimeStamp.setTimeInMillis(measurement);
          //Write to filter output
          // WriteFilterOutputPort(0, datacopy);
          // byteswritten++;
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
        } // if

        //Original code
        /****************************************************************************
        // Here we look for an ID of 0 which indicates this is a time measurement.
        // Every frame begins with an ID of 0, followed by a time stamp which correlates
        // to the time that each proceeding measurement was recorded. Time is stored
        // in milliseconds since Epoch. This allows us to use Java's calendar class to
        // retrieve time and also use text format classes to format the output into
        // a form humans can read. So this provides great flexibility in terms of
        // dealing with time arithmetically or for string display purposes. This is
        // illustrated below.
        ****************************************************************************/
        /*
        if ( id == 0 )
        {
          TimeStamp.setTimeInMillis(measurement);

        } // if
        */

        /****************************************************************************
        // Here we pick up a measurement (ID = 3 in this case), but you can pick up
        // any measurement you want to. All measurements in the stream are
        // decommutated by this class. Note that all data measurements are double types
        // This illustrates how to convert the bits read from the stream into a double
        // type. Its pretty simple using Double.longBitsToDouble(long value). So here
        // we print the time stamp and the data associated with the ID we are interested
        // in.
        ****************************************************************************/
        /*
        if ( id == 3 )
        {
          System.out.print( TimeStampFormat.format(TimeStamp.getTime()) + " ID = " + id + " " + Double.longBitsToDouble(measurement) );

        } // if

        System.out.print( "\n" );
        */

			} // try

			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::TempAltFilter Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
				break;

			} // catch

		} // while

   } // run

} // MiddleFilter
