/*----------------------------------------------------------------------------------------------
 * Created on 28.05.2018
 * 
 * (c) 1999 - 2018, Marcel Bernet, Zufikon, alle Rechte vorbehalten 
 *---------------------------------------------------------------------------------------------*/

package ch.iotkit.microbit.gateway;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/*******************************************************************************
 * Hilfsklasse fuer REST Aufrufe<p>
 * 
 *******************************************************************************/

public class RESTUtil
{
    /**
     * Order (Bestellung) absetzen
     * @param host Host
     * @param id Id Customer (Kunde)
     * @param value Id Catalog (Produkt)
     * @return 
     * @throws Exception
     */
    public static String postOrder( String host, String customer, int item ) throws Exception
    {
        // Kunden anlegen wenn nicht vorhanden
        int custId = postCustomer( host, customer );
        
        String urlParameters = "customerId=" + custId + "&orderLine[0].count=1"+ "&orderLine[0].itemId=" + item;

        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = host + "/order";
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( true );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
        conn.setRequestProperty( "charset", "utf-8" );
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ) );
        conn.setUseCaches( false );
        try (DataOutputStream wr = new DataOutputStream( conn.getOutputStream() ))
        {
            wr.write( postData );
        }
        String ret = null;
        try (DataInputStream rc = new DataInputStream( conn.getInputStream() ))
        {
            String  line;
            while   ( ( line = rc.readLine()) != null )
                ret += line;
        }
        return  ( ret );
    }
    
    /**
     * Hole Kunde
     * @param host
     * @param name
     * @return
     * @throws Exception
     */
    public static int getCustomerId( String host, String name ) throws Exception
    {
        String request = host + "/customer/customer/search" + "/findByName?name=" + name.trim().replace(" ", "%20" );;
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( true );
        conn.setRequestMethod( "GET" );
        conn.setUseCaches( false );
        String ret = "";
        try (DataInputStream rc = new DataInputStream( conn.getInputStream() ))
        {
            String  line;
            while   ( ( line = rc.readLine()) != null )
                ret += line;
        }
        JSONObject jsonObject = new JSONObject( ret );
        JSONObject embedded = jsonObject.getJSONObject( "_embedded" );
        JSONArray customer = embedded.getJSONArray("customer");
        
        if  ( customer.length() > 0 )
            return  ( customer.optJSONObject(0).getInt( "id") );
        return  ( -1 );
    }
    
    /**
     * Liefert alle Kunden
     * @param host
     * @return
     * @throws Exception
     */
    public static HashMap<String, Integer> getCustomer( String host ) throws Exception
    {
        String request = host + "/customer/customer";
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( true );
        conn.setRequestMethod( "GET" );
        conn.setUseCaches( false );
        String ret = "";
        try (DataInputStream rc = new DataInputStream( conn.getInputStream() ))
        {
            String  line;
            while   ( ( line = rc.readLine()) != null )
                ret += line;
        }
        JSONObject jsonObject = new JSONObject( ret );
        JSONObject embedded = jsonObject.getJSONObject( "_embedded" );
        JSONArray customer = embedded.getJSONArray("customer");
        
        HashMap<String, Integer> cat = new HashMap<String, Integer>();

        for ( int i = 0; i < customer.length(); i++ )
            cat.put( customer.optJSONObject( i ).getString( "name" ), customer.optJSONObject( i ).getInt( "id" ) );
                       
        return  ( cat );
    }    
    
    /**
     * Neuen Kunden anlegen, wenn nicht schon vorhanden
     * @param host
     * @param name
     * @return
     * @throws Exception
     */
    public static int postCustomer( String host, String name ) throws Exception
    {
        int id = -1;
        if  ( (id = getCustomerId( host, name ) ) > 0 )
            return  ( id );
        
        String urlParameters = "name=" + name.trim() + "&firstname=V" + name.trim() + "&email=" + name.trim() + "@ch.ch&street=Strasse 1&city=Ort 1";

        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = host + "/customer/form.html";
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( true );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
        conn.setRequestProperty( "charset", "utf-8" );
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ) );
        conn.setUseCaches( false );
        try (DataOutputStream wr = new DataOutputStream( conn.getOutputStream() ))
        {
            wr.write( postData );
        }
        try (DataInputStream rc = new DataInputStream( conn.getInputStream() ))
        {
            while   ( rc.readLine() != null )
                ;
        }
        return  ( getCustomerId( host, name ) );
    }
    
    /**
     * Finde Artikel
     * @param host
     * @param name
     * @return
     * @throws Exception
     */
    public static int getCatalogId( String host, String name ) throws Exception
    {
        String request = host + "/catalog/catalog/search" + "/findByName?name=" + name.trim().replace(" ", "%20" );
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( true );
        conn.setRequestMethod( "GET" );
        conn.setUseCaches( false );
        String ret = "";
        try (DataInputStream rc = new DataInputStream( conn.getInputStream() ))
        {
            String  line;
            while   ( ( line = rc.readLine()) != null )
                ret += line;
        }
        JSONObject jsonObject = new JSONObject( ret );
        JSONObject embedded = jsonObject.getJSONObject( "_embedded" );
        JSONArray catalog = embedded.getJSONArray("catalog");
        
        if  ( catalog.length() > 0 )
            return  ( catalog.optJSONObject(0).getInt( "id") );
        return  ( -1 );
    }  
    
    /**
     * Liefert alle Artikel als HashMap
     * @param host
     * @return
     * @throws Exception
     */
    public static HashMap<String, Integer> getCatalog( String host ) throws Exception
    {
        String request = host + "/catalog/catalog";
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( true );
        conn.setRequestMethod( "GET" );
        conn.setUseCaches( false );
        String ret = "";
        try (DataInputStream rc = new DataInputStream( conn.getInputStream() ))
        {
            String  line;
            while   ( ( line = rc.readLine()) != null )
                ret += line;
        }
        JSONObject jsonObject = new JSONObject( ret );
        JSONObject embedded = jsonObject.getJSONObject( "_embedded" );
        JSONArray catalog = embedded.getJSONArray("catalog");
        
        HashMap<String, Integer> cat = new HashMap<String, Integer>();

        for ( int i = 0; i < catalog.length(); i++ )
            cat.put( catalog.optJSONObject( i ).getString( "name" ), catalog.optJSONObject( i ).getInt( "id" ) );
                       
        return  ( cat );
    }      
    
    /**
     * Artikel schreiben
     * @param host
     * @param name
     * @return
     * @throws Exception
     */
    public static int postCatalog( String host, String name ) throws Exception
    {
        int id = -1;
        if  ( (id = getCatalogId( host, name ) ) > 0 )
            return  ( id );
        
        String urlParameters = "name=" + name.trim() + "&price=1.0";

        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = host + "/catalog/form.html";
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( true );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
        conn.setRequestProperty( "charset", "utf-8" );
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ) );
        conn.setUseCaches( false );
        try (DataOutputStream wr = new DataOutputStream( conn.getOutputStream() ))
        {
            wr.write( postData );
        }
        try (DataInputStream rc = new DataInputStream( conn.getInputStream() ))
        {
            while   ( rc.readLine() != null )
                ;
        }
        return  ( getCatalogId( host, name ) );
    }    

    /**
     * Hauptprogramm
     * @param args
     */
    public static void main ( String[] args )
    {
        String host = "http://lernkube:32280";
        try
        {
            int id = RESTUtil.getCustomerId( host, "Wolff" );
            System.out.println( "Kunde " + id );
            id = RESTUtil.getCustomerId( host, "Test" );
            System.out.println( "Kunde " + id );
            System.out.println( RESTUtil.getCustomer( host ) ); 
            
            // Schreiben
            RESTUtil.postCustomer( host, "test1" );
            RESTUtil.postCustomer( host, "Hans" );
            System.out.println( RESTUtil.getCustomer( host ) );             
            
            id = RESTUtil.getCatalogId( host, "iPod" );
            System.out.println( "Artikel " + id ); 
            id = RESTUtil.getCatalogId( host, "Test" );
            System.out.println( "Artikel " + id );            
            System.out.println( RESTUtil.getCatalog( host ) );
            
            // Schreiben
            RESTUtil.postCatalog( host, "Afri Cola (Erfrischungsgetraenk)" );
            RESTUtil.postCatalog( host, "Heineken (Bier)" );
            RESTUtil.postCatalog( host, "Ariel (Waschmittel)" );            
            RESTUtil.postCatalog( host, "Nescafe (Kaffee)" );            
            RESTUtil.postCatalog( host, "Pampers (Windeln)" );            
            RESTUtil.postCatalog( host, "Pommery (Champagne)" );            
            RESTUtil.postCatalog( host, "Felix (Katzenfutter)" );            
            RESTUtil.postCatalog( host, "Nivea (Creme)" );            
            RESTUtil.postCatalog( host, "Sniffer (Putzmittel)" );            
            RESTUtil.postCatalog( host, "Jacobs (Kaffee)" );            
            System.out.println( RESTUtil.getCatalog( host ) );    
            
            // Bestellungen absetzen
            RESTUtil.postOrder( host, "Wolff", 1 );
            RESTUtil.postOrder( host, "test1", 2 );
            RESTUtil.postOrder( host, "Hans", 3 );
            RESTUtil.postOrder( host, "Muster", 4 );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
    
}
