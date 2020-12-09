
package app.central;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.ConfigReader;

public class CentralServer {

        public static ConfigReader config = new ConfigReader();

        public static String parseMsg(String data){
            //cliente_username_residencia
            //district_residencia
            String res = "";
            String[] parts = data.split("_");
            String local;
            String port_local = "";
            if(parts[0].equals("cliente")){
                local = parts[2];
                port_local = config.getPort("local", local);
                
            }
            else if(parts[0].equals("district")){
                local = parts[1];
                port_local = config.getPort("local", local);

            }
            //centralserver_ok_PORTAPUSH_PORTAXPUB
            //centralserver_error
            if(port_local == null){

                res = "centralserver_error";
            }
            else{

                int port_final = Integer.parseInt(port_local);

                port_final += Integer.parseInt(config.getPort("ports", "CENTRAL_SERVER"));

                res = "centralserver_ok_" + port_final + "_10050";
            }

            return res;
        }
    
        public static void main(String[] args) {

            try (
                ZContext context = new ZContext();
                ZMQ.Socket socketRep = context.createSocket(SocketType.REP))
            {
                String port = config.getPort("ports", "CENTRAL_SERVER");
                socketRep.bind("tcp://*:" + port);

                System.out.println("Starting Central Server on port " + port + "...");

                while(true){
                    String data = new String(socketRep.recv());
                    System.out.println("Received: " + data);
                    String sndMsg = parseMsg(data.replace("\"", ""));
                    System.out.println("Sending: " + sndMsg);
                    socketRep.send(sndMsg);
                }
            }
        }
}