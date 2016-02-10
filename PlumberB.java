/******************************************************************************************************************
* File:PlumberB.java
* Course: 17655
* Project: Assignment 1
*
* Description:
*
* This class implements the system B and orchestrates the filters and pipes for the system.
*
* Parameters:       None
*
* Internal Methods: None
*
******************************************************************************************************************/

public class PlumberB
{
 public static void main( String[] argv)
 {
        /****************************************************************************
        * Here we instantiate three filters.
        ****************************************************************************/

        SourceFilter Filter1 = new SourceFilter(argv[0]);
        TempAltPressFilter Filter2 = new TempAltPressFilter();
        TemperatureConvertor Filter3 = new TemperatureConvertor();
        AltitudeConvertor Filter4 = new AltitudeConvertor();
        WildPointFilter Filter5 = new WildPointFilter(4);
        SinkFilter Filter6 = new SinkFilter("./OutputB/wildPoint.dat");
        SinkFilter Filter7 = new SinkFilter("./OutputB/outputB.dat");

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
