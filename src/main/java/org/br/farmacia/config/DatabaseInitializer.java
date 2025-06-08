package org.br.farmacia.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


//Essa anotação faz com que quando o TomCat for inicializado ele executa automaticamente isso aqui.
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private Connection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");

            //Conexão foi realizada no docker.
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:8010/farmacia",
                    "docker",
                    "docker"
            );

            //InputStream é uma classe que lê dados bye por byte.
            //no caso ele acessa diretamente no resources, não sendo necessário colocar o caminho completo.
            InputStream is = getClass().getClassLoader().getResourceAsStream("db/scripts.sql");
            if (is == null) {
                System.err.println("Arquivo SQL não encontrado");
                return;
            }
            String scriptSQL = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            String[] comandos = scriptSQL.split(";");

            try (Statement stmt = connection.createStatement()) {
                for (String comando : comandos) {
                    comando = comando.trim();
                    if (!comando.isEmpty()) {
                        stmt.execute(comando + ";");
                    }
                }
            }

            sce.getServletContext().setAttribute("DBConnection", connection);
            System.out.println("Conexão com banco iniciada e script executado!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão com banco encerrada!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
