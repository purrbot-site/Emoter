package site.purrbot.emoter.manager;

import ch.qos.logback.classic.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.slf4j.LoggerFactory;
import site.purrbot.emoter.Emoter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class FileManager{
    
    private final Logger LOGGER = (Logger) LoggerFactory.getLogger(FileManager.class);
    private Map<String, File> files;
    
    public FileManager(){}
    
    public void addFile(String name, String internal, String external){
        createOrLoad(name, internal, external);
    }
    
    public void createOrLoad(String name, String internal, String external){
        if(files == null) files = new HashMap<>();
        
        File file = new File(external);
        String[] split = external.split("/");
        
        try{
            if(!file.exists()){
                if((split.length == 2 && !split[0].equals(".")) || (split.length >= 3 && split[0].equals("."))){
                    if(!file.getParentFile().mkdirs() && !file.getParentFile().exists()){
                        LOGGER.warn(String.format(
                                "Failed to create directory %s",
                                split[1]
                        ));
                        return;
                    }
                }
                if(file.createNewFile()){
                    if(export(Emoter.class.getResourceAsStream(internal), external)){
                        LOGGER.info(String.format(
                                "Successfully created %s!",
                                name
                        ));
                        files.put(name, file);
                    }else{
                        LOGGER.warn(String.format(
                                "Failed to create %s",
                                name
                        ));
                    }
                }
            }else{
                LOGGER.info(String.format(
                        "Loaded %s",
                        name
                ));
                
                files.put(name, file);
            }
        }catch(IOException ex){
            LOGGER.warn(String.format(
                    "Couldn't create or load %s.",
                    name
            ), ex);
        }
    }
    
    private boolean export(InputStream inputStream, String destination){
        boolean success = true;
        try{
            Files.copy(inputStream, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            success = false;
        }
        
        return success;
    }
    
    public String getString(String name, String path){
        File file = files.get(name);
        
        if(file == null)
            return "";
        
        try{
            JsonReader reader = new JsonReader(new FileReader(file));
            JsonElement json = JsonParser.parseReader(reader);
            
            for(String key : path.split("\\.")){
                if(!json.isJsonObject())
                    break;
                
                json = json.getAsJsonObject().get(key);
            }
            
            if(json == null || json.isJsonNull())
                return "";
            
            return json.getAsString();
        }catch(FileNotFoundException ex){
            LOGGER.warn("Could not find file " + name + ".json", ex);
            return "";
        }
    }
}
