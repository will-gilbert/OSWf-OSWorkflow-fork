package org.informagen.oswf.simulator.server;

/**
 * From: http://stackoverflow.com/questions/4553316/gwt-image-from-database
 *
 * Here is the solution. First you should encode the byte array by using
 * 
 *      com.google.gwt.user.server.Base64Utils.toBase64(byte[]). 
 *
 * But this method does not work for IE 7 and IE8 has 32kb limit. IE9 does 
 *    not have this limit.
 * 
 *     Here is the client method ;
 *
 *        public void onSuccess(String imageData) {     
 *            Image image = new Image(imageData);     
 *            RootPanel.get("image").add(image); 
 *        } 
 *
 */ 

// Application - RPC 
import org.informagen.oswf.simulator.rpc.GraphvizService;
import org.informagen.oswf.simulator.rpc.ServiceException;

// OSWf Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.util.Graphviz;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf Default Implementations & Service Providers
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;
import org.informagen.oswf.exceptions.WorkflowLoaderException;

import org.informagen.oswf.util.Base64;

// Nidi Graphviz For Java
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import guru.nidi.graphviz.engine.Format;

 // Java
import java.lang.StringBuffer;
import java.lang.Process;
import java.lang.Runtime;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

// Java IO
import java.io.File;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;

// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Google DI Annotation
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class GraphvizServiceImpl implements GraphvizService {

    private static Logger logger = LoggerFactory.getLogger(GraphvizService.class);
    private static Logger startupLogger = LoggerFactory.getLogger("StartupLogger");

    private final String dotExecutable;
    private OSWfConfiguration configuration;

    @Inject
    public GraphvizServiceImpl(@Named("executable.graphviz")String dotExecutable, OSWfConfiguration configuration) {
        
        startupLogger.info("Configuring GraphvizService");
        
        this.dotExecutable = dotExecutable;
        this.configuration = configuration;
    }


    public String renderAsGraphviz(String workflowName) throws ServiceException {
        return renderAsPNG(workflowName);
    }

    public String renderAsPNG(String workflowName) throws ServiceException {

        String base64Image = null;

        try {
            
            String dot = createDotNotation(workflowName);
            
            MutableGraph g = Parser.read(dot);
            BufferedImage bi = guru.nidi.graphviz.engine.Graphviz.fromGraph(g).render(Format.PNG).toImage();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", outputStream);

            byte[] imageByteArray = outputStream.toByteArray();

            base64Image = Base64.getInstance().encodeAsString(imageByteArray);
             
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        } 
               
        return base64Image;
    }

    public String renderAsSVG(String workflowName) throws ServiceException {

        String base64Image = null;

        try {
            
            String dot = createDotNotation(workflowName);
            
            MutableGraph g = Parser.read(dot);
            String svg = guru.nidi.graphviz.engine.Graphviz.fromGraph(g).render(Format.SVG).toString();
            
            byte[] imageByteArray = svg.getBytes();

            base64Image = Base64.getInstance().encodeAsString(imageByteArray);
             
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        } 
               
        return base64Image;
    }


    public String createDotNotation(String workflowName) throws ServiceException {
 
        String dot = null;
        
        try {
            WorkflowDescriptor wfd = configuration.getWorkflow(workflowName);
            dot = new Graphviz(wfd).create();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        } 
               
        return dot;
    }

/*
    public String renderAsGraphviz(String workflowName) throws ServiceException {

        String base64Image = null;
        File dotFile = null;

        try {
            
            String dot = createDotNotation(workflowName);
            dotFile = writeDotToFile(dot);
            
            // Render the DOT file as a PNG image; returned as a byte array
            // Sadly there is no Java 'dot' implementation so we will 
            //   have to rely on it being installed.
            
            byte[] imageByteArray = createImageAsByteArray(dotFile, "png");
            
            return Base64.getInstance().encodeAsString(imageByteArray);
             
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        } finally {
            if(dotFile != null)
                dotFile.delete();
       }
               
        return base64Image;
    }





    private File writeDotToFile(String dot) throws Exception {

        // Use a system temporary file; Set it to autodelete when
        //   the JVM exists in case it doesn't get deleted here.
    
        File file = File.createTempFile("tmp-", ".dot");
        file.deleteOnExit();

        Writer writer = new BufferedWriter(new FileWriter(file));

        try {
            writer.write(dot);
        }  finally {
            writer.close();
        } 
        
        return file;
    }




    private byte[] createImageAsByteArray(File dotFile, String imageType) throws Exception {
            
        // Build the 'dot' command line; 'dot -Tformat /path/to/file'
        String command = new StringBuffer()
            .append(dotExecutable)
            .append(" ").append("-T").append(imageType)
            .append(" ").append(dotFile.getAbsolutePath())
            .toString()
        ;
         
        // Execute the command, route stdout as an inputstream directly into a byte array
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        BufferedInputStream inputStream = new BufferedInputStream(process.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int c;
        while((c = inputStream.read()) != -1)
            outputStream.write(c);

        // Log any errors from the external process
        String line = null;
        while( (line = errReader.readLine()) != null)
            logger.error(line);
                    
        int result = process.waitFor();

        // We should end up with a normal termination and a non-zero length output stream
        if(result != 0 || outputStream.size() == 0) 
            logger.error("There was an error running Graphviz using " + dotFile.toString());
            
        // Garbage Collection hint  
        process = null;
        
        // Return the image as a byte array
        return outputStream.toByteArray();
    }
*/

}

