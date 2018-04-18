/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

/**
 *
 * @author elian
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ServidorWeb {

    public static void main(String[] args) throws IOException {
        System.out.println("Iniciando Servidor ");
        //Espera uma coneção do cliente na porta 8080
        ServerSocket servidor = new ServerSocket(8080);
        
        while(true){
           System.out.println("Aguardando Conexão ");
            //Escuta uma conexão e a aceita quando for encontrada, criando um cliente, ou seja, um objeto da classe Socket
            Socket socket = servidor.accept();
            
            //Se um cliente se conectar
            if (socket.isConnected()) {
                System.out.println("Conexão Estabelecida");
                //Exibe informações da conexão
                System.out.println(socket.getInetAddress().getHostName());
                try (
                    //Cria um leitor para ler as requisiçoes que o cliente está enviando   
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    System.out.println("Requisicão: ");
                    
                    //Ler a requisição e a salva em uma variavel
                    String linha = buffer.readLine();
                    //Particiona a requisição
                    String[] dadosReq = linha.split(" ");
                    //Abstrai o caminho do arquivo dos dados da requisição
                    String caminhoArquivo = dadosReq[1];
                    //Abstrai o protocolo usado na requisição
                    String protocolo = dadosReq[2];
                   
                    //Enquanto a requisição for diferente de vazia o programa continuará tentando ler alguma requisição
                    while (!linha.isEmpty()) {
                     
                        System.out.println(linha);
                        
                        linha = buffer.readLine();
                    }
                    
                    //Se o caminho for igual ao arquivo contido na pasta ele irá ser exibido
                    if (caminhoArquivo.equals("/sss")) {
                        caminhoArquivo = "D:\\4Sem\\Redes\\HTML\\sss.html";
                    }
                    File arquivo = new File(caminhoArquivo.replaceFirst("/", ""));
                    //Pega o arquivo na pasta e manda um protocolo de ok
                    String status = protocolo + " 200 OK\r\n";
                    
                    //Se o arquivo for vazio ou não existir exibirá uma página de error 404
                    if (!arquivo.exists()) {
                        status = protocolo + " 404 Not Found\r\n";
                        arquivo = new File("D:\\4Sem\\Redes\\HTML\\404.html");
                    }
                    //Transforma o conteúdo do arquivo em byte para poder exibi-lo
                    byte[] conteudo = Files.readAllBytes(arquivo.toPath());
                    //Formata a data da requisição
                    SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
                    formatador.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Date data = new Date();
                    
                    String dataFormatada = formatador.format(data) + " GMT";
                    //Define o header da requisição
                    String header = status
                            + "Location: http://localhost:8080/\r\n"
                            + "Date: " + dataFormatada + "\r\n"
                            + "Server: Elian/1.0\r\n"
                            + "Content-Type: text/html\r\n"
                            + "\r\n";
                    
                    //Envia dados para o servidor
                    OutputStream resposta = socket.getOutputStream();
                 
                    resposta.write(header.getBytes());
                  
                    resposta.write(conteudo);
                 
                    resposta.flush();
                    
                    buffer.close();
                    
                }
                socket.close();
            }
        }
    }

}