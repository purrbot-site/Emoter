package site.purrbot.emoter.manager;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import site.purrbot.emoter.Emoter;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class DBManager{
    
    private final RethinkDB r;
    private final Connection connection;
    
    public DBManager(Emoter bot){
        this.r = RethinkDB.r;
        this.connection = r.connection()
                .hostname(bot.getFileManager().getString("config", "database.ip"))
                .port(28015)
                .db(bot.getFileManager().getString("config", "database.name"))
                .connect();
    }
    
    public String getId(EmoteType type){
        check();
        Map map = getTable();
        
        return map.get(type.toString().toLowerCase()).toString();
    }
    
    public void setId(EmoteType type, String value){
        check();
        
        r.table("messages").get("0").update(r.hashMap(type.toString().toLowerCase(), value)).run(connection);
    }
    
    private void check(){
        Map map = getTable();
    
        if(map == null)
            addEntry();
    }
    
    private Map getTable(){
        return r.table("messages").get("0").run(connection, Map.class).single();
    }
    
    private void addEntry(){
        r.table("messages").insert(
                r.array(
                        r.hashMap("id", "0")
                         .with(EmoteType.NORMAL.toString().toLowerCase(), "null")
                         .with(EmoteType.ANIMATED.toString().toLowerCase(), "null")
                )
        ).optArg("conflict", "update").run(connection);
    }
    
    public enum EmoteType{
        NORMAL,
        ANIMATED
    }
}
