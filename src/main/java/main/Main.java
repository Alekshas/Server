package main;

import dao.HibernateUserDAO;
import dao.JDBC_UserDAO;
import dao.UserDAO;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import servlets.MirrorServlet;
import servlets.info.ServerInfo;
import servlets.info.ServerInfoServlet;
import servlets.signs.SignInServlet;
import servlets.signs.SignUpServlet;
import websockets.chat.WebSocketChatServlet;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) throws Exception {

        UserDAO userDao = new JDBC_UserDAO();


        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new MirrorServlet()), "/mirror");
        context.addServlet(new ServletHolder(new SignUpServlet(userDao)), "/signup");
        context.addServlet(new ServletHolder(new SignInServlet(userDao)), "/signin");
        context.addServlet(new ServletHolder(new ServerInfoServlet()), "/info");

        context.addServlet(new ServletHolder(new WebSocketChatServlet(userDao)), "/chat");


        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newResource("src\\main\\resources\\public_html\\"));

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{resourceHandler,context});

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("ServerInfo:type=UsersInfo");
        mBeanServer.registerMBean(ServerInfo.getInstance(),name);

        Server server = new Server(8080);
        server.setHandler(handlerList);
        server.start();
        System.out.println("Server started");
        server.join();

    }
}
