import java.util.logging.Filter;

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
public class PlumberC
{
   public static void main( String argv[])
   {
        /****************************************************************************
        * Here we instantiate three filters.
        ****************************************************************************/

        SourceFilter Filter1 = new SourceFilter("SubSetA.dat");
        SourceFilter Filter2 = new SourceFilter("SubSetB.dat");
        MergeFilter Filter3 = new MergeFilter();
        LessThan10k Filter4 = new LessThan10k();
        WildPointFilter Filter5 = new WildPointFilter(6);
        SinkFilter Filter6 = new SinkFilter("wildPoint.dat");
        SinkFilter Filter7 = new SinkFilter("LessThan10K.dat");
        SinkFilter Filter8 = new SinkFilter("outputC");

        /****************************************************************************
        * Here we connect the filters starting with the sink filter (Filter 1) which
        * we connect to Filter2 the middle filter. Then we connect Filter2 to the
        * source filter (Filter3).
        ****************************************************************************/
        Filter3.Connect(Filter1, 0, 0);
        Filter3.Connect(Filter2, 0, 1);
        Filter4.Connect(Filter3, 0, 0);
        Filter5.Connect(Filter4, 0, 0);
        Filter7.Connect(Filter4, 1, 0);
        Filter6.Connect(Filter5, 1, 0);
        Filter8.Connect(Filter5, 0, 0);

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
        Filter8.start();


   } // main

} // Plumber