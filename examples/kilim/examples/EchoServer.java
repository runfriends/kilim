package kilim.examples;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import kilim.Pausable;
import kilim.nio.EndPoint;
import kilim.nio.MultiNioSelectorThreadScheduler;
import kilim.nio.SessionTask;


/**
 * A echo server example
 * 
 * @author boyan
 * @Date 2010-8-21
 * 
 */
public class EchoServer extends SessionTask {
    static boolean server = false;
    static int port = 8007;
    static int workerCount = Runtime.getRuntime().availableProcessors() ;


    static private void usage() {
        System.err.println("Run java kilim.examples.EchoServer [port] [workerCount] in one window");
    }


    static void parsePort(String portstr) {
        try {
            port = Integer.parseInt(portstr);
        }
        catch (Exception e) {
            usage();
        }
    }


    static void parseWorkerCount(String portstr) {
        try {
            workerCount = Integer.parseInt(portstr);
        }
        catch (Exception e) {
            usage();
        }
    }


    public static void main(String args[]) throws Exception {

        usage();

        if (args.length > 0) {
            parsePort(args[0]);
        }
        if (args.length > 1) {
            parseWorkerCount(args[1]);
        }
        EchoServer.run();

    }

    private ByteBuffer readBuffer = ByteBuffer.allocate(16 * 1024);


    public static void run() throws IOException {

        MultiNioSelectorThreadScheduler nio = new MultiNioSelectorThreadScheduler(workerCount);

        nio.listen(port, EchoServer.class);
    }


    @Override
    public void execute() throws Pausable, Exception {
        // System.out.println("[" + this.id + "] Connection rcvd");
        try {
            while (true) {
                EndPoint ep = this.getEndPoint();
                this.readBuffer = ep.read(this.readBuffer); // Pauses until at
                // least
                this.readBuffer.flip();

                if (this.readBuffer.hasRemaining()) {
                    byte[] data = new byte[this.readBuffer.remaining()];
                    this.readBuffer.get(data);
                    // System.out.println("recv " + data.length);
                    ByteBuffer buff = ByteBuffer.allocate(data.length);
                    buff.put(data);
                    buff.flip();
                    ep.write(buff);
                }
                this.readBuffer.compact();
                // System.out.println("[" + this.id + "] Echoed pkt");
            }
        }
        catch (EOFException eofe) {
            // System.out.println("[" + this.id + "] Connection terminated");
        }
        catch (IOException ioe) {
            // System.out.println("[" + this.id + "] IO Exception: " +
            // ioe.getMessage());
        }
    }
}
