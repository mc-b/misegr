package ch.iotkit.microbit.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * This version of the TwoWaySerialComm example makes use of the 
 * SerialPortEventListener to avoid polling.
 *
 */
public class TwoWaySerialComm
{
    // Argumente verarbeiten
    private static String host = "http://lernkube:32280";
    private static String port = "COM4:";
    private static HashMap<String, Integer> items;
    
    public TwoWaySerialComm()
    {
        super();
    }
    
    void connect(String portName) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier( portName );
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println( "Error: Port is currently in use" );
        } 
        else
        {
            CommPort commPort = portIdentifier.open( this.getClass().getName(), 2000 );

            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams( 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );

                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();

                ( new Thread( new SerialWriter( out ) ) ).start();

                serialPort.addEventListener( new SerialReader( in ) );
                serialPort.notifyOnDataAvailable( true );

            } else
            {
                System.out.println( "Error: Only serial ports are handled by this example." );
            }
        }
    }
    
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    public static class SerialReader implements SerialPortEventListener 
    {
        private InputStream in;
        private byte[] buffer = new byte[1024];
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void serialEvent(SerialPortEvent arg0)
        {
            int data;
          
            try
            {
                int len = 0;
                while ( ( data = in.read()) > -1 )
                {
                    if ( data == '\n' ) {
                        break;
                    }
                    buffer[len++] = (byte) data;
                }
                String[] params = new String( buffer, 0, len ).split( "[:|\n|\r]" );
                
                String label = labels[ new Integer(params[1].trim()) ];
                Integer item = items.get( label );
                System.out.println( params[0].trim() + " => " + label );
                RESTUtil.postOrder( host, params[0].trim(), item );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }             
        }
    }

    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
    
    // Produkte
    private static final String[]  labels = { "Afri Cola (Erfrischungsgetraenk)", 
                                                "Heineken (Bier)",
                                                "Ariel (Waschmittel)",            
                                                "Nescafe (Kaffee)",           
                                                "Pampers (Windeln)",            
                                                 "Pommery (Champagne)",         
                                                "Felix (Katzenfutter)",            
                                                "Nivea (Creme)",       
                                                "Sniffer (Putzmittel)",            
                                                "Jacobs (Kaffee)" };    
 
    /**
     * Hauptprogramm
     * @param args
     * @throws Exception 
     */
    public static void main ( String[] args ) throws Exception
    {
        if  ( args.length >= 2 )
        {
            port = args[0];
            host = args[1]; 
        }
        else
        {
            System.err.println( "usage: appl Port Host");
            System.exit( -1 );
        }

        // fehlende Artikel nachtragen        
        items = RESTUtil.getCatalog( host );
        if  ( items.size() < 10 )
            for ( String l : labels )
                RESTUtil.postCatalog( host, l );
        
        try
        {
            
            System.out.println( port );
            (new TwoWaySerialComm()).connect( port );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}
