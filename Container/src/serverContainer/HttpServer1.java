package serverContainer;



import java.net.Socket;

import javax.servlet.Servlet;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import serverContainer.Request;
import serverContainer.Response;
//import serverContainer.StaticResourceProcessor;

public class HttpServer1 {

    // 关闭服务命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    public static void main(String[] args) {
        HttpServer1 server = new HttpServer1();
        //等待连接请求
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 3333;
        try {
            //服务器套接字对象
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
            Mapping.mapJsp();
     	   Mapping.mapServlet();
		JspParser.getJspServlet();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待请求
        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                //等待连接，连接成功后，返回一个Socket对象
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // 创建Request对象并解析
                Request request = new Request(input);
                request.parse();
                // 检查是否是关闭服务命令
                /*if (request.getUri().equals(SHUTDOWN_COMMAND)) {
                    break;
                }*/
                
                // 创建 Response 对象
                Response response = new Response(output);
                response.setRequest(request);
              if("".equals(request.getUri())){
            	  continue;
              }else{
            	  if (request.getUri().indexOf(".jsp")!=-1) {
            		  System.out.println("request.getUri()"+request.getUri());
               	   String ServletName= null;
               	   ServletName=Mapping.getJspMap().get(request.getUri());
               	   ServletProcessor1 processor = new ServletProcessor1();
               	   Servlet servlet =processor.loadServlet(ServletName);
               	processor.callService(servlet, request, response);
                	  
                   }else if(request.getUri().indexOf(".ico")!= 0){
                	   continue;
                	  
                	  /* ServletProcessor1 processor = new ServletProcessor1();
                       processor.processServletRequest(request, response);*/
                   }
                   else{
                	  // System.out.println("request.getUri()"+request.getUri());
                	 //请求uri以/servlet/开头，表示servlet请求
                       ServletProcessor1 processor = new ServletProcessor1();
                       processor.processServletRequest(request, response);
                   }
                        socket.close();
              }
               

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}