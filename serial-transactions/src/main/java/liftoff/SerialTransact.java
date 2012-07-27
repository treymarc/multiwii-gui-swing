
/**
 * Copyright 2012 Dick Hollenbeck <dick@softplc.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */


package liftoff;

import gnu.io.*;
import java.util.Enumeration;
import java.io.*;

/**
 * Class SerialTransact
 * is a wrapper around a SerialPort which provides an efficient _timed_ read.
 */
public class SerialTransact
{
    //private final static boolean debug = false;
    private final static boolean debug = true;

    /**
     * Method setSerialPort
     * must be called before any other method in this class is called.
     *
     * @param anOpenSerialPort which is already setup with proper baudrate, etc.
     * @param aReceiveTimeoutMsecs
     */
    public void setSerialPort( SerialPort anOpenSerialPort, int aReceiveTimeoutMsecs )
    {
        serport = anOpenSerialPort;
        setRecvTimeout( aReceiveTimeoutMsecs );
    }

    void close()
    {
        if( serport != null )
        {
            serport.close();
            serport = null;
            recvEnd = 0;
            recvNdx = 0;
        }
    }

    /**
     * Method recv
     * waits for up to @a recvTimeout msecs for a byte and returns it in the lower
     * 8 bits.  If there are no bytes available or a timeout occurs, return -1.
     * You should be able to tune setRecvTimeout() to get the results you want.
     *
     * @return int - the received byte in lower 8 bits with upper 24 zeroed, or -1 if timeout.
     * @throws IOException if the serial port is closed.
     */
    public int recv()
        throws IOException
    {
        if( recvNdx >= recvEnd )    // is recvBuf empty?
        {
            InputStream is = serport.getInputStream();

            // read a bunch at a time if available, wait at most recvTimeout msecs.  By getting
            // several bytes at once we minimize the number of OS calls being made.
            recvEnd = is.read( recvBuf, 0, recvBuf.length );

            // if( debug ) System.out.printf( "recvEnd:%d recvNdx:%d\n", recvEnd, recvNdx );

            if( recvEnd <= 0 )
                return -1;          // no bytes are available, i.e. timed out waiting for one.

            recvNdx = 0;
        }

        int ret = 0xff & recvBuf[recvNdx++];   // return byte in lower 8 bits of int

        if(debug)
        {
            System.out.printf( "recv: %02x", ret );
            System.out.println();
        }

        return ret;
    }

    /**
     * Method recv
     * reads a number of bytes into @a userbuf, and returns how many were read.
     * @return int - the number of bytes read.  If less than count, this means there was a timeout
            before all could be read.  The return value is always >= 0.
     */
    public int recv( byte[] userbuf, int count )
        throws IOException
    {
        int received = 0;

        while( count-- > 0 )
        {
            int cc = recv();
            if( cc == -1 )
                break;

            userbuf[received++] = (byte) cc;
        }

        if(false && received > 0 )
        {
            System.out.print( "recv[]:" );
            for( int i=0; i<received; ++i )
                System.out.printf( " %02x", 0xff & userbuf[i] );
            System.out.println();
        }

        return received;
    }

    /**
     * Method setRecvTimeout
     * causes recv() to wait up to @a msecs before returning.
     * @param msecs the timeout in milliseconds, or -1 to mean infinite.
     * @param boolean - true if the operation succeeded, else false.
     */
    boolean setRecvTimeout( int msecs )
    {
        try
        {
            if( msecs < 0 )
                serport.disableReceiveTimeout();
            else
                serport.enableReceiveTimeout( msecs );
        }
        catch( Exception e )
        {
            if(debug) System.out.println( "setRecvTimeout not supported" );
            return false;
        }
        return true;
    }

    /**
     * Method recvAvailable
     * return true if there is at least one byte available.
     */
    boolean recvAvailable()
    {
        try
        {
            return recvNdx < recvEnd || serport.getInputStream().available() > 0;
        }
        catch( IOException ioe ) { return false; }
    }

    /**
     * Method recvClear
     * drains the serial ports input buffers to empty.
     */
    public void recvClear()
        throws IOException
    {
        // drain the input stream dry.
        while( recvAvailable() )
        {
            if( debug ) System.out.print( " recvClear" );
            recv();
        }
    }

    /**
     * Method send
     * sends a single byte.
     */
    public void send( int aByte )
        throws IOException
    {
        OutputStream os = serport.getOutputStream();
        os.write( aByte );

        if( debug )
        {
            System.out.printf( "send: %02x", 0xff & aByte );
            System.out.println();
        }
    }


    /**
     * Method send
     * sends multiple bytes from an array.
     * @param buffer is the array to send from.
     * @param start is the starting byte index within buffer to start sending from.
     * @param count is the number of bytes starting at index @a start to send.
     */
    public void send( byte[] buffer, int start, int count )
        throws IOException
    {
        OutputStream os = serport.getOutputStream();
        os.write( buffer, start, count );

        if( debug )
        {
            System.out.print( "send[]:" );
            for( int i=start; i<start+count;  ++i )
            {
                if( i>start && ((i-start)%16)==0 )
                    System.out.print( "\n       " );
                System.out.printf( " %02x", 0xff & buffer[i] );
            }
            System.out.println();
        }
    }

    protected SerialPort serport;

    // recv() support:
    protected int       recvTimeout;                  // msecs
    protected byte[]    recvBuf = new byte[512];
    protected int       recvNdx;
    protected int       recvEnd;
};
