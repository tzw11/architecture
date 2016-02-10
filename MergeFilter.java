import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/******************************************************************************************************************
 * File:MergeFilter.java
 *
 * Merge two sorted file and sort the result
 *
 *

 ******************************************************************************************************************/

public class MergeFilter extends FilterFramework
{
    private Calendar[] TimeStamp = new Calendar[2];
    private int[] endFile = new int[2];      // whether two file has ended
    private int flag = 0; //this is used to mark whether you need to output Time

    private int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
    private int IdLength = 4;				// This is the length of IDs in the byte stream

    public MergeFilter(){
        super(2,1);
    }

    /************************************************************************************
     *This function is used to output byte
     *************************************************************************************/
    public void output(int x, long y) {
        ByteBuffer buffer1 = ByteBuffer.allocate(4);
        buffer1.putInt(x);
        byte [] a = new byte[4];
        a = buffer1.array();
        ByteBuffer buffer2 = ByteBuffer.allocate(8);
        buffer2.putLong(y);
        byte [] b = new byte[8];
        b = buffer2.array();
        for(int i = 0; i < 4; i++) {
            WriteFilterOutputPort(0, a[i]);
        }

//        System.out.println(b.length);
        for(int i = 0; i < 8; i++) {
            WriteFilterOutputPort(0, b[i]);
        }
    }


    /************************************************************************************
     *This function use to update the Timestamp, and output the data to the output port
     * and it will read 12 bytes each time
     *************************************************************************************/
    private void HandleData(int k) throws EndOfStreamException{
        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream

        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter
        id = 0;

        for (i=0; i<IdLength; i++ )
        {
            databyte = ReadFilterInputPort(k);	// This is where we read the byte from the stream...

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
            databyte = ReadFilterInputPort(k);
            measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

            if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
            {												// previously appended byte to the left by one byte
                measurement = measurement << 8;				// to make room for the next byte we append to the
                // measurement
            } // if

            bytesread++;									// Increment the byte count

        } // if

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

        if ( id == 0 )
        {
            TimeStamp[k].setTimeInMillis(measurement);
            flag = 1;

        } else { //directly output the data
            output(id, measurement);
        }
    }
    public void run()
    {
        /************************************************************************************
         *	TimeStamp is used to compute time using java.util's Calendar class.
         * 	TimeStampFormat is used to format the time value so that it can be easily printed
         *	to the terminal.
         *************************************************************************************/
        TimeStamp[0] = Calendar.getInstance();
        TimeStamp[1] = Calendar.getInstance();
        endFile[0] = endFile[1] = 0;
        //SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");


        System.out.print( "\n" + this.getName() + "::Merge and sort Reading ");

        /***************************************************************************
         // Initialize the TimeStamp
         ****************************************************************************/
        try {
            /***************************************************************************
             // We know that the first data coming to this filter is going to be an ID and
             // that it is IdLength long. So we first decommutate the ID bytes.
             ****************************************************************************/
            for (int k = 0; k < 2; k++) {
                HandleData(k);
            }
        }
        catch (EndOfStreamException e)
        {
            ClosePorts();
            //System.out.print( "\n" + this.getName() + "::Sink Exiting; bytes read: " + bytesread );

        } // catch

         /****************************************************************************
         // order and merge the output of datasetA and datasetB
         ****************************************************************************/
        while (true)
        {
            while (TimeStamp[0].compareTo(TimeStamp[1]) < 0) {
                try {
                    if (flag == 1) {
                        output(0, TimeStamp[0].getTimeInMillis());
                        flag = 0;
                    }
                    HandleData(0);
                } catch (EndOfStreamException e) {
                    endFile[0] = 1;
                    TimeStamp[0].setTimeInMillis(Long.MAX_VALUE);
                    flag = 1;
                    break;
                }
            }

            while (TimeStamp[0].compareTo(TimeStamp[1]) >= 0) {
                try {
                    if (flag == 1) {
                        output(0, TimeStamp[1].getTimeInMillis());
                        flag = 0;
                    }
                    HandleData(1);
                } catch (EndOfStreamException e) {
                    endFile[1] = 1;
                    TimeStamp[1].setTimeInMillis(Long.MAX_VALUE);
                    flag = 1;
                    break;
                }
            }

            if (endFile[0] == 1 && endFile[1] == 1) {
                ClosePorts();
                break;
            }

        } // while
    } // run

} // MiddleFilter
