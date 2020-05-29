package site.purrbot.emoter;

import ch.qos.logback.classic.Logger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.LoggerFactory;
import site.purrbot.emoter.events.EmoteListener;
import site.purrbot.emoter.events.ReadyListener;
import site.purrbot.emoter.manager.DBManager;
import site.purrbot.emoter.manager.EmoteManager;
import site.purrbot.emoter.manager.FileManager;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

public class Emoter{
    
    private final Logger LOGGER = (Logger) LoggerFactory.getLogger(Emoter.class);
    
    private JDA jda;
    private FileManager fileManager;
    private DBManager dbManager;
    private EmoteManager emoteManager;
    
    private final Cache<EmoteType, String> messages = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    
    public static void main(String[] args){
        try{
            new Emoter().startBot();
        }catch(LoginException ex){
            new Emoter().LOGGER.warn("Could not start bot!", ex);
        }
    }
    
    private void startBot() throws LoginException{
        fileManager = new FileManager();
        fileManager.addFile("config", "/config.json", "./config.json");
        
        dbManager = new DBManager(this);
        emoteManager = new EmoteManager();
        
        
        jda = JDABuilder
                .createLight(
                        fileManager.getString("config", "token"), 
                        GatewayIntent.GUILD_EMOJIS, 
                        GatewayIntent.GUILD_MESSAGES
                )
                .enableCache(CacheFlag.EMOTE)
                .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Loading emotes"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(
                        new ReadyListener(this),
                        new EmoteListener(this)
                )
                .build();
    }
    
    public FileManager getFileManager(){
        return fileManager;
    }
    
    public String getId(EmoteType type){
        return messages.get(type, k -> dbManager.getId(type));
    }
    
    public void setId(EmoteType type, String value){
        messages.put(type, value);
        dbManager.setId(type, value);
    }
    
    public void updateMessage(EmoteType type, Guild guild){
        TextChannel tc = guild.getTextChannelById(fileManager.getString("config", "channel"));
        if(tc == null)
            return;
    
        String messageId = getId(type);
    
        if(messageId.equals("null")){
            tc.sendMessage(
                    emoteManager.getEmbed(guild, type)
            ).queue(message -> setId(type, message.getId()));
        }else{
            Message msg = tc.retrieveMessageById(messageId).complete();
            if(msg == null || !msg.getAuthor().equals(jda.getSelfUser())){
                tc.sendMessage(
                        emoteManager.getEmbed(guild, type)
                ).queue(message -> setId(type, message.getId()));
                return;
            }
        
            msg.editMessage(
                    emoteManager.getEmbed(guild, type)
            ).queue();
        }
    }
    
    public void sendLog(TextChannel channel, LogType type, Emote emote){
        int color;
        String title;
        
        switch(type){
            case ADDED:
                color = 0x00FF00;
                title = "Emote added";
                break;
            
            case REMOVED:
                color = 0xFF0000;
                title = "Emote removed";
                break;
            
            case EDITED:
                color = 0xF1C40F;
                title = "Emote changed";
                break;
            
            default:
                color = 0x802F3136;
                title = "Unknown Action";
        }
        
        MessageEmbed embed = new EmbedBuilder()
                .setColor(color)
                .setTitle(title)
                .addField(
                        "Name",
                        String.format(
                                "`:%s:`",
                                emote.getName()
                        ),
                        true
                )
                .addField(
                        "ID",
                        String.format(
                                "`%s`",
                                emote.getId()
                        ),
                        true
                )
                .addField(
                        "Format",
                        String.format(
                                "`%s`",
                                emote.getAsMention()
                        ),
                        true
                )
                .addField(
                        "Preview",
                        emote.getAsMention(),
                        true
                )
                .addField(
                        "URL",
                        String.format(
                                "[`Click here`](%s)",
                                emote.getImageUrl()
                        ),
                        true
                )
                .build();
        
        channel.sendMessage(embed).queue();
    }
    
    public enum LogType{
        ADDED,
        EDITED,
        REMOVED
    }
    
    public enum EmoteType{
        NORMAL,
        ANIMATED
    }
}
