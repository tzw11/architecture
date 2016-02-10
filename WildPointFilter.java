/******************************************************************************************************************
* File:TemperatureConvertor.java
* Course: 17655
* Project: Assignment 1
*
* Description:
*
* This class is used as a filter to identify wild points, extrapolate data instead of them and save both sets of data
* This filter has 2 output ports.
*
* Parameters:       None
*
* Internal Methods: None
*
******************************************************************************************************************/

import java.nio.ByteBuffer;
import java.util.*;						// This class is used to interpret time words
import java.text.SimpleDateFormat;		// This class is used to format and write time in a string format.

public class WildPointFilter extends FilterFramework
{
    private int featuresNum;
    // system b 4
    // system c 6
    public WildPointFilter(int couts){
        //one input, two output
        //ouput[0] for outputb, output[1] for wildpoints
        super(1, 2);
        featuresNum = couts;
    }

    public static byte[] getBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

	public void run()
    {
        Calendar TimeStamp = Calendar.getInstance();
        SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");
		/************************************************************************************
		*	TimeStamp is used to compute time using java.util's Calendar class.
		* 	TimeStampFormat is used to format the time value so that it can be easily printed
		*	to the terminal.
		*************************************************************************************/

		//Calendar TimeStamp = Calendar.getInstance();
		//SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

		int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
		int IdLength = 4;				// This is the length of IDs in the byte stream

		byte databyte = 0;				// This is the data byte read from the stream
		int bytesread = 0;				// This is the number of bytes read from the stream

		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		int id;							// This is the measurement id
		int i;							// This is a loop counter
        byte []TempPool = new byte [1000000];                // cache
        boolean permit = false;      // indicator
        boolean wildpoint = false;
        int index = 0;
        double last = -1;
        double next = -1;
        double pis = -1;
        long mid;
        byte [] wild = new byte [1000];
        byte [] modified = new byte[8];
        int wildIndex = 0;
        int [] indexArray = new int [1000];
        int arrayIndex = 0;
        int validnum = 0;

		/*************************************************************
		*	First we announce to the world that we are alive...
		**************************************************************/

		System.out.print( "\n" + this.getName() + "::WildPointFilter Reading ");

		while (true)
		{
			try
			{
				/***************************************************************************
				// We know that the first data coming to this filter is going to be an ID and
				// that it is IdLength long. So we first decommutate the ID bytes.
				****************************************************************************/

				id = 0;

				for (i=0; i<IdLength; i++ )
				{
					databyte = ReadFilterInputPort(0);	// This is where we read the byte from the stream...

                    TempPool[index++] = databyte;       // cache
                    wild[wildIndex++] = databyte;

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

                    TempPool[index++] = databyte;                   // cache
                    wild[wildIndex++] = databyte;

					measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

					if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
					{												// previously appended byte to the left by one byte
						measurement = measurement << 8;				// to make room for the next byte we append to the
																	// measurement
					} // if

					bytesread++;									// Increment the byte count

				} // if

                if(id == 0){
                    TimeStamp.setTimeInMillis(measurement);

                }

                if(id == 3){
                    pis = Double.longBitsToDouble(measurement);

                    if(validnum == 0){
                        //havn't find a valid input
                        if(pis < 0){
                            wildpoint = true;
                            if(featuresNum == 4){
                                TempPool[index - 1 - 32] = 42; //mark *, change the id of time to 42;
                            }else{
                                TempPool[index - 1 - 44] = 42; //mark *, change the id of time to 42;
                            }

                            //save the index
                            indexArray[arrayIndex++] = index - 1;
                        }else{
                            //find the first valid input
                            wildpoint = false;
                            last = pis;
                            validnum = 1;
                            mid = Double.doubleToLongBits(last);
                            ByteBuffer buffer2 = ByteBuffer.allocate(8);
                            buffer2.putLong(mid);
                            //byte [] b = new byte[8];
                            modified = buffer2.array();

                            //modified = getBytes(mid);
                            //update all
                            for(i = 0; i < arrayIndex; i++){
                                for(int j = 0; j < 8; j++){
                                    TempPool[indexArray[i] - j] = modified[index - 1 - j];
                                }
                            }
                            permit = true;
                            arrayIndex = 0;
                        }
                    }
                    else if(validnum == 1){
                        //get the first valid input
                        if(pis < 0 || Math.abs(last - pis) > 10){
                            wildpoint = true;
                            if(featuresNum == 4){
                                TempPool[index - 1 - 32] = 42; //mark *, change the id of time to 42;
                            }else{
                                TempPool[index - 1 - 44] = 42; //mark *, change the id of time to 42;
                            }
                            //save the index, we have to wait the next value to handle
                            indexArray[arrayIndex++] = index - 1;
                            validnum = 2;
                        }else{
                            last = pis;
                            wildpoint = false;
                            permit = true;
                        }
                    }
                    else if(validnum == 2){
                        //need next
                        if(pis < 0 || Math.abs(last - pis) > 10){
                            wildpoint = true;
                            if(featuresNum == 4){
                                TempPool[index - 1 - 32] = 42; //mark *, change the id of time to 42;
                            }else{
                                TempPool[index - 1 - 44] = 42; //mark *, change the id of time to 42;
                            }
                            //save the index, we have to wait the next value to handle
                            indexArray[arrayIndex++] = index - 1;
                            validnum = 2;
                        }else{
                            wildpoint = false;
                            next = pis;
                            pis = (next + last) / 2;
                            mid = Double.doubleToLongBits(pis);
                            ByteBuffer buffer2 = ByteBuffer.allocate(8);
                            buffer2.putLong(mid);
                            //byte [] b = new byte[8];
                            modified = buffer2.array();
                            for(i = arrayIndex - 1; i >= 0; i--){
                                for(int j = 0; j < 8; j++){
                                    TempPool[indexArray[i] - j] = modified[7 - j];
                                }
                            }
                            last = next;
                            validnum = 1;
                            arrayIndex = 0;
                            permit = true;
                        }
                    }

                }

                if(featuresNum == 4){
                    //system B
                    if(id == 4){
                        if(wildpoint){
                            // only need time and pressure
                            for(i = 0; i < 12; i++){
                                WriteFilterOutputPort(1, wild[i]);
                            }
                            for(i = 24; i < 36; i++){
                                WriteFilterOutputPort(1, wild[i]);
                            }
                        }
                        wildIndex = 0;
                        if(permit){

                            for(i = 0; i < index; i++){
                                WriteFilterOutputPort(0, TempPool[i]);
                            }
                            //next item, clear
                            index = 0;
                            permit = false;
                        }

                    }


                }

                if(featuresNum == 6){
                    //system C
                    if(id == 5){
                        if(wildpoint){
                            // only need time and pressure
                            for(i = 0; i < 12; i++){
                                WriteFilterOutputPort(1, wild[i]);
                            }
                            for(i = 36; i < 48; i++){
                                WriteFilterOutputPort(1, wild[i]);
                            }
                        }
                        wildIndex = 0;
                        if(permit){
                            for(i = 0; i < index; i++){
                                WriteFilterOutputPort(0, TempPool[i]);
                            }
                            //next item, clear
                            index = 0;
                            permit = false;
                        }
                    }


                }


			} // try

			/*******************************************************************************
			*	The EndOfStreamExeception below is thrown when you reach end of the input
			*	stream (duh). At this point, the filter ports are closed and a message is
			*	written letting the user know what is going on.
			********************************************************************************/

			catch (EndOfStreamException e)
			{
                //handle the last items
                if(index > 0){
                    mid = Double.doubleToLongBits(last);
                    ByteBuffer buffer2 = ByteBuffer.allocate(8);
                    buffer2.putLong(mid);
                    modified = buffer2.array();
                    for(i = arrayIndex - 1; i >= 0; i--){
                        for(int j = 0; j < 8; j++){
                            TempPool[indexArray[i] - j] = modified[7 - j];
                        }
                    }
                    for(i = 0; i < index; i++){
                        WriteFilterOutputPort(0, TempPool[i]);
                    }
                    //file end
                }


				ClosePorts();
				System.out.print( "\n" + this.getName() + "::Sink Exiting; bytes read: " + bytesread );
				break;

			} // catch

		} // while

   } // run

} // SingFilter
