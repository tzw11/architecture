/******************************************************************************************************************
* File:Plumber.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*   1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* This class serves as an example to illstrate how to use the PlumberTemplate to create a main thread that
* instantiates and connects a set of filters. This example consists of three filters: a source, a middle filter
* that acts as a pass-through filter (it does nothing to the data), and a sink filter which illustrates all kinds
* of useful things that you can do with the input stream of data.
*
* Parameters:       None
*
* Internal Methods: None
*
******************************************************************************************************************/
public class PlumberB
{
   public static void main( String argv[])
   {
        /****************************************************************************
        * Here we instantiate three filters.
        ****************************************************************************/

        SourceFilter Filter1 = new SourceFilter("/Users/wilsoncao/desktop/architecture/Sample1/FlightData.dat");
        // MiddleFilter Filter2 = new MiddleFilter();
         // TemperatureConvertor Filter2 = new TemperatureConvertor();
        TempAltPressFilter Filter2 = new TempAltPressFilter();
        TemperatureConvertor Filter3 = new TemperatureConvertor();
        AltitudeConvertor Filter4 = new AltitudeConvertor();
        WildPointFilter Filter5 = new WildPointFilter(4);
        SinkFilter Filter6 = new SinkFilter("/Users/wilsoncao/desktop/architecture/Sample1/wildPoint.dat");
        // AltitudeConvertor Filter2 = new AltitudeConvertor();

        SinkFilter Filter7 = new SinkFilter("/Users/wilsoncao/desktop/architecture/Sample1/outputB.dat");
        //SinkTest Filter7 = new SinkTest();

        /****************************************************************************
        * Here we connect the filters starting with the sink filter (Filter 1) which
        * we connect to Filter2 the middle filter. Then we connect Filter2 to the
        * source filter (Filter3).
        ****************************************************************************/
        Filter7.Connect(Filter5, 0, 0);
        Filter6.Connect(Filter5, 1, 0);
        Filter5.Connect(Filter4, 0, 0);
        Filter4.Connect(Filter3, 0, 0);
        Filter3.Connect(Filter2, 0, 0); // This esstially says, "connect Filter3 input port to Filter2 output port
        Filter2.Connect(Filter1, 0, 0); // This esstially says, "connect Filter2 intput port to Filter1 output port

        /****************************************************************************
        * Here we start the filters up. All-in-all,... its really kind of boring.
        ****************************************************************************/

        Filter1.start();
        Filter2.start();
        Filter3.start();
        Filter4.start();
        Filter5.start();
        Filter6.start();
        Filter7.start();


   } // main

} // Plumber