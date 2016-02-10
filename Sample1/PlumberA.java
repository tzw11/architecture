/******************************************************************************************************************
* File:PlumberA.java
* Course: 17655
* Project: Assignment 1

*
* Description:
*
* This class implements the system A and orchestrates the filters and pipes for the system.
*
* Parameters: 		None
*
* Internal Methods:	None
*
******************************************************************************************************************/
public class PlumberA
{
   public static void main( String[] argv)
   {
		/****************************************************************************
		* Here we instantiate three filters.
		****************************************************************************/

		SourceFilter Filter1 = new SourceFilter(argv[0]);
		TempAltFilter Filter2 = new TempAltFilter();
		TemperatureConvertor Filter3 = new TemperatureConvertor();
		AltitudeConvertor Filter4 = new AltitudeConvertor();
		SinkFilter Filter5 = new SinkFilter("outputA.dat");

		/****************************************************************************
		* Here we connect the filters starting with the sink filter (Filter 1) which
		* we connect to Filter2 the middle filter. Then we connect Filter2 to the
		* source filter (Filter3).
		****************************************************************************/
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


   } // main

} // Plumber
