import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

/**
 * The {@code MyContextListener} class contains methods for
 * executing certain instructions for initialization and
 * (graceful) termination of the server.
 *
 * On termination all user files are deleted as required.
 * See {@link #contextDestroyed} for more details.
 *
 * @author Lee Taylor
 * */
@WebListener
public class MyContextListener implements ServletContextListener{

    /**
     * On initialization of the server this method is run.
     * */
    @Override
    public void contextInitialized(ServletContextEvent sce){
    }

    /**
     * On gracefully closing this server this method is run.
     *
     * All user files start with "$2a$10$" due to the hashing
     * algorithm used, bcrypt. These files store user numbers,
     * and are deleted upon gracefully stopping the server.
     *
     * The file named "keys.txt" containing user public and
     * private keys are also deleted upon graceful closing.
     * */
    @Override
    public void contextDestroyed(ServletContextEvent sce){

        // Acquire path to dir containing potential files to delete
        ServletContext servletcontext = sce.getServletContext();
        String fileDir = servletcontext.getRealPath("/");
        final File fdir = new File(fileDir);

        // Iterate through directory
        for (final File fileName : fdir.listFiles()){
            // Files with names that are hashes (storing user numbers) or
            // named 'keys' (storing public and private keys) are deleted
            if (fileName.toString().contains("$2a$10$") || fileName.toString().contains("keys")){
                fileName.delete();
            }
        }

    }

}
