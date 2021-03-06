package exemplo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Servidor {

    public static void main(String[] args) throws IOException {
    	
    	List<OutputStream> saidas = new ArrayList<>();
    	
        ServerSocket server = new ServerSocket(9000);
        FileOutputStream fos = new FileOutputStream("chamada.txt");
        while(true){

            Socket socket = server.accept();
            OutputStream os = socket.getOutputStream();
            saidas.add(os);
            
            new Thread(){
                public void run() {
                    try {
                        System.out.println("Alguem conectou");
                        Scanner scan = new Scanner(socket.getInputStream(), "utf-8");
                        while(scan.hasNext()){
                            String linha = scan.nextLine();
                            if(linha.contains("presente")){
                                fos.write((linha + "\n").getBytes("utf-8"));
                                fos.flush();
                            }
                            System.out.println(linha);
                            List<OutputStream> toRemove = new ArrayList<>();
                            for(OutputStream osUsu : saidas ){
                            	try {
                                    osUsu.write(linha.getBytes("utf-8"));
                                    osUsu.flush();
                                } catch (Exception e) {
                                    toRemove.add(osUsu);
                                    e.printStackTrace();
                                }
                            }
                            for(OutputStream osRem : toRemove){
                                saidas.remove(osRem);
                            }
                            if("sair".equalsIgnoreCase(linha)){
                                break;
                            }
                        }
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }                
                }
            }.start();

        }



    }

}
