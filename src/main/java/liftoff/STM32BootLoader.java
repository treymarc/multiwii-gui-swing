
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

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;


/**
 * Class STM32
 * hold data on a particular sub-species within the STM32 chip family.
 */
class STM32
{
    int     id;
    String  name;
    long    ram_start;
    long    ram_end;
    long    fl_start;
    long    fl_end;
    int     fl_pps;         // pages per sector
    int     fl_ps;          // page size
    long    opt_start;
    long    opt_end;
    long    mem_start;
    long    mem_end;

    public STM32( int aid, String aname, long aram_start, long aram_end, long afl_start, long afl_end,
        int afl_pps, int afl_ps, long aopt_start, long aopt_end, long amem_start, long amem_end )
    {
        id        = aid;
        name      = aname;
        ram_start = aram_start;
        ram_end   = aram_end;
        fl_start  = afl_start;
        fl_end    = afl_end;
        fl_pps    = afl_pps;
        fl_ps     = afl_ps;
        opt_start = aopt_start;
        opt_end   = aopt_end;
        mem_start = amem_start;
        mem_end   = amem_end;
    }
};


/**
 * Class STM32BoatLoader
 * interacts via a serial port with the boot loader firmware in an STM32 CPU chip.
 * Downloading of new firmware is supported, as is erasing of flash memory.
 * Ideas were borrowed from project:
 * @see http://stm32flash.googlecode.com
 */
public class STM32BootLoader extends SerialTransact
{
    private final static boolean debug = true;
    // private final static boolean debug = false;

    int version;
    int option1;
    int option2;
    int productId;

    private final static int ACK = 0x79;
    private final static int NAK = 0x1f;

    /// retained reply to the GET command.
    private final byte[] get = new byte[12];

    /// commands advertised in reply to GET command, these are indices into
    /// get[] above;
    private final static int VERSION = 0;
    private final static int GET = 1;
    private final static int GVR = 2;       // get version and read protection status
    private final static int GID = 3;       // get ID
    private final static int RM  = 4;       // read memory
    private final static int GO  = 5;       // GO execute at address
    private final static int WM  = 6;       // write memory
    private final static int ER  = 7;       // erase or extended erase, these commands are exclusive
    private final static int WP  = 8;       // write protect
    private final static int UW  = 9;       // write unprotect
    private final static int RP  = 10;      // read out protect
    private final static int UR  = 11;      // read out unprotect

    /// various sub-species within the STM32 family
    private final static STM32[] devs = {
    new STM32( 0x412, "Low-density",        0x20000200, 0x20002800, 0x08000000, 0x08008000, 4,  1024,  0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 ),
    new STM32( 0x410, "Medium-density",     0x20000200, 0x20005000, 0x08000000, 0x08020000, 4,  1024,  0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 ),
    new STM32( 0x411, "STM32F2xx",          0x20002000, 0x20020000, 0x08000000, 0x08100000, 4,  16384, 0x1FFFC000, 0x1FFFC00F, 0x1FFF0000, 0x1FFF77DF ),
    new STM32( 0x413, "STM32F4xx",          0x20002000, 0x20020000, 0x08000000, 0x08100000, 4,  16384, 0x1FFFC000, 0x1FFFC00F, 0x1FFF0000, 0x1FFF77DF ),
    new STM32( 0x414, "High-density",       0x20000200, 0x20010000, 0x08000000, 0x08080000, 2,  2048,  0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 ),
    new STM32( 0x416, "Medium-density ULP", 0x20000800, 0x20004000, 0x08000000, 0x08020000, 16, 256,   0x1FF80000, 0x1FF8000F, 0x1FF00000, 0x1FF01000 ),
    new STM32( 0x418, "Connectivity line",  0x20001000, 0x20010000, 0x08000000, 0x08040000, 2,  2048,  0x1FFFF800, 0x1FFFF80F, 0x1FFFB000, 0x1FFFF800 ),
    new STM32( 0x420, "Medium-density VL",  0x20000200, 0x20002000, 0x08000000, 0x08020000, 4,  1024,  0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 ),
    new STM32( 0x428, "High-density VL",    0x20000200, 0x20008000, 0x08000000, 0x08080000, 2,  2048,  0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 ),
    new STM32( 0x430, "XL-density",         0x20000800, 0x20018000, 0x08000000, 0x08100000, 2,  2048,  0x1FFFF800, 0x1FFFF80F, 0x1FFFE000, 0x1FFFF800 ),
    };

    /// The STM32 chip which is found via the GET command if good working serial
    /// communications are in play.
    private STM32 cur;

    /// sleep without exception
    private static void sleep( int msecs )
    {
        try { Thread.sleep( msecs ); } catch( Exception ignore ) {}
    }

    private void stmInit()
        throws IOException
    {
        send( 0x7F );
        if( ACK != recv() )
            throw new IOException( "no reply to power on INIT command, serial cable wrong?" );
    }

/*
    private void sendCommand( int command, int maxAttempts )
        throws IOException
    {
        int reply = 0;

        for( int attempt=0; attempt < maxAttempts;  ++attempt )
        {
            send( command );
            send( ~command );

            reply = recv();
            if( reply == ACK )
                return;

            sleep( 500 );
        }

        if( reply == NAK )
        {
            String msg = String.format( "NAK to command %02x.", command );
            throw new IOException( msg );
        }
        else
        {
            String msg = String.format( "No ACK to command %02x.  (Timed out.)", command );
            throw new IOException( msg );
        }
    }
*/

    private int sendCommand( int command, boolean doThrowOnError )
        throws IOException
    {
        recvClear();

        send( command );
        send( ~command );

        int reply = recv();
        if( doThrowOnError && reply != ACK )
        {
            if( reply == NAK )
            {
                String msg = String.format( "NAK to command %02x.", command );
                throw new IOException( msg );
            }
            else
            {
                String msg = String.format( "No ACK to command %02x.  (Timed out.)", command );
                throw new IOException( msg );
            }
        }
        return reply;
    }

    private void stmGET()
        throws IOException
    {
        sendCommand( 0, true );

        int len = recv() + 1;
        int ask = len;

        if( len > get.length )
            ask = get.length;

        // save the reply permanently in this.get[]
        int r = recv( get, ask );
        if( r < ask )
            throw new IOException( "Reply length to GET command < advertised: " + r );

        len -= r;
        while( len-- > 0 )
            recv();             // toss as unknown

        if( ACK != recv() )
            throw new IOException( "No ACK to GET" );

        sendCommand( get[GVR], true );
        version = recv();
        option1 = recv();
        option2 = recv();
        if( ACK != recv() )
            throw new IOException( "No ACK to GVR" );

        sendCommand( get[GID], true );
        len = recv() + 1;
        if( len != 2 )
            throw new IOException( "More than 2 bytes sent in the ProductID, unknown/unsupported device" );

        int pid1 = recv();
        int pid2 = recv();
        productId = (pid1 << 8) | pid2;

        if( ACK != recv() )
            throw new IOException( "No ACK to GID" );

        for( STM32 dev : devs )
        {
            if( dev.id == productId )
            {
                cur = dev;
                break;
            }
        }

        if( cur == null )
            throw new IOException( "Unknown productId:" + productId );

        if( debug )
        {
            System.out.printf( "Version      : 0x%02x\n", version );
            System.out.printf( "Option 1     : 0x%02x\n", option1 );
            System.out.printf( "Option 2     : 0x%02x\n", option2 );
            System.out.printf( "Device ID    : 0x%04x (%s)\n", productId, cur.name );
            System.out.printf( "RAM          : %dKiB  (%db reserved by bootloader)\n", (cur.ram_end - 0x20000000) / 1024, cur.ram_start - 0x20000000 );
            System.out.printf( "Flash        : %dKiB (sector size: %dx%d)\n", (cur.fl_end - cur.fl_start ) / 1024, cur.fl_pps, cur.fl_ps );
            System.out.printf( "Option RAM   : %db\n", cur.opt_end - cur.opt_start);
            System.out.printf( "System RAM   : %dKiB\n", (cur.mem_end - cur.mem_start) / 1024);
        }
    }

    private void stmErase( int pageStart, int pageCount )
        throws IOException
    {
        /*
            The Erase Memory command allows the host to erase Flash memory pages. When the
            bootloader receives the Erase Memory command, it transmits the ACK byte to the host.
            After the transmission of the ACK byte, the bootloader receives one byte (number of
            pages to be erased), the Flash memory page codes and a checksum byte; if the checksum is
            correct then bootloader erases the memory and sends an ACK byte to the host, otherwise
            it sends a NACK byte to the host and the command is aborted.

            Erase Memory command specifications:

            1. The bootloader receives one byte that contains N, the number of pages to be erased -
            1. N = 255 is reserved for global erase requests. For 0 <= N <= 254, N + 1 pages are
            erased.

            2. The bootloader receives (N + 1) bytes, each byte containing a page
            number
        */

        sendCommand( get[ER], true );

        // The erase command reported by the bootloader is either 0x43 or 0x44.
        // 0x44 is Extended Erase, a 2 byte based protocol and needs to be handled differently.
        if( get[ER] == 0x44 )
        {
            /*
                Not all chips using Extended Erase support mass erase. Currently known as not
                supporting mass erase is the Ultra Low Power STM32L15xx range. So if someone has not
                overridden the default, but uses one of these chips, take it out of mass erase mode,
                so it will be done page by page. This maximum might not be correct either!
            */

            if( productId == 0x416 && pageCount == 0xff )
                pageCount = 0xf8;       // works for the STM32L152RB with 128Kb flash

            if( pageCount == 0xff )
            {
                send( 0xff );
                send( 0xff );   // 0xffff the magic number for mass erase
                send( 0x00 );   // 0x00 is the XOR of those two 0xff bytes as a checksum
                if( ACK != recv() )
                    throw new IOException( "Mass erase failed. Try specifying the number of pages to be erased." );
            }
            else
            {
                // Number of pages to be erased, two bytes, MSB first
                send( pageCount >> 8 );
                send( pageCount );

                int csum = 0;
                for( int pg_num = 0;  pg_num <= pageCount;  ++pg_num )
                {
                    send( pg_num >> 8 );
                    csum ^= pg_num >> 8;

                    send( pg_num );
                    csum ^= pg_num;
                }

                send( 0x00 );  // Ought to need to hand over a valid checksum here...but 0 seems to work!

                if( ACK != recv() )
                    throw new IOException( "Page-by-page erase failed. Check the maximum pages your device supports." );
            }
        }

        else    // Regular erase (0x43) for all other chips
        {
            if( pageCount == 0xFF )
            {
                send( 0xff );
                if( ACK != recv() )
                    throw new IOException( "No ACK to regular erase 0x43 command." );
            }
            else
            {
                send( pageCount-1 );

                int csum  = pageCount-1;
                for( int pg_num = pageStart;  pg_num < pageStart+pageCount;  ++pg_num )
                {
                    send( pg_num );
                    csum ^= pg_num;
                }

                send( csum );

                if( ACK != recv() )
                    throw new IOException( "No ACK to regular erase 0x43 command with page list." );
            }
        }
    }


    /**
     * Method stmWrite
     * writes to any kind of memory within the STM32.
     *
     * @param address is where to start writing within the CPU device.
     * @param image contains the bytes to write.
     * @param start is the startying byte index within @a image to copy bytes from.
     * @param count is the number of bytes to write starting at @a start, from @a image.
     */
    private void stmWrite( long address, byte[] image, int start, int count )
        throws IOException
    {
        final int MAXTRIES = 100;

        // must be 32 bit aligned
        if( (address & 0x3) != 0 )
            throw new IOException( "address must be 4 byte aligned" );

        if( count <=0 || count > 256 )
            throw new IOException( "count must be 1-256" );

        int addr = (int) address;   // might go negative, but 32 bits are enough going forward.

        System.out.printf( "address:%08x start:%d count:%d\n", addr, start, count );

        int csum = addr ^ (addr >> 8) ^ (addr >> 16) ^ (addr >> 24);

        // Because of erasing and writing previous blocks, this can sometimes return a NAK.
        // So we keep trying until the device is freed up.
        int attempt;
        for( attempt = 0;  attempt < MAXTRIES;  )
        {
            int reply = sendCommand( get[WM], false );

            if( reply == ACK )
            {
                send( addr >> 24 );
                send( addr >> 16 );
                send( addr >> 8 );
                send( addr >> 0 );
                send( csum );

                reply = recv();
                if( reply == ACK )
                    break;

                ++attempt;
            }
            else if( reply == NAK )      // NOT a timeout
            {
                sleep( 100 );
                ++attempt;
            }
            else    // timeout
            {
                // try only 2 times if timing out.
                attempt += MAXTRIES / 2;
            }

            if(debug) System.out.printf( "Write Memory attempt:%d\n", attempt );
        }

        if( attempt == MAXTRIES )
            throw new IOException( "No ACK to Write Memory command." );

        // setup the csum and encode the length, which by coincidence are initially one and the same
        int extra = count % 4;
        csum = count - 1 + extra;
        send( csum );

        // write the data and build the checksum
        for( int i = start;  i < start + count; ++i )
            csum ^= image[i];

        send( image, start, count );

        // write the alignment padding, if any
        for( int i = 0; i < extra; ++i )
        {
            send( 0xff );
            csum ^= 0xff;
        }

        send( csum );
        if( ACK != recv() )
            throw new IOException( "No ACK to Write Memory command." );
    }

    /**
     * Method proram
     * erases and then programs the flash with the given @a image.
     *
     * @param image is the exact binary image to program, in big endian format.
     *
     * @param eraseAll if true, means first erase the entire flash image, if false, means erase
     *   just enough for the given image.
     *
     * @param startingPage is the index of the first page to write to within the flash, normally zero.
     */
    void program( byte[] image, boolean eraseAll, int startingPage )
        throws IOException
    {
        stmInit();
        stmGET();

        if( image.length > cur.fl_end - cur.fl_start )
        {
            String msg = String.format( "File too big for flash.  File is %d bytes, flash is %d bytes.",
                                image.length, cur.fl_end - cur.fl_start );
            throw new IOException( msg );
        }

        int pageCount = eraseAll ? 0xff : (image.length + cur.fl_ps) / cur.fl_ps;

        stmErase( startingPage, pageCount );

        // starting memory address at which to write
        long address = cur.fl_start + startingPage * cur.fl_ps;

        for( int blockz, offset = 0;  offset<image.length;  offset += blockz, address += blockz )
        {
            blockz = Math.min( 256, image.length - offset );
            stmWrite( address, image, offset, blockz );
        }
    }

    public static void main( String[] args )
        throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException,
                IOException, ParseException
    {
        if( args.length != 2 )
        {
            System.err.println( "Usage: <prog> portDevice hexfile.hex" );
            System.exit(1);
        }

        STM32BootLoader me = new STM32BootLoader();

        CommPortIdentifier comident = CommPortIdentifier.getPortIdentifier( args[0] );

        SerialPort sp = (SerialPort) comident.open( "STM32BootLoader", 200 );

        sp.setSerialPortParams( 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN );

        sp.setFlowControlMode( SerialPort.FLOWCONTROL_NONE );

        // Setup serial port and reply timeout.  Found that such a long timeout IS necessary.
        me.setSerialPort( sp, 5000 );

        try
        {
            File f = new File( args[1] );

            byte[] image = HexFileParser.parseFile( f );

            me.program( image, false, 0 );
        }
        finally
        {
            me.close();
        }
    }
}
