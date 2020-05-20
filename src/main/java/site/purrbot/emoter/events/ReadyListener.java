package site.purrbot.emoter.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import site.purrbot.emoter.Emoter;
import site.purrbot.emoter.manager.DBManager;

import javax.annotation.Nonnull;

public class ReadyListener extends ListenerAdapter{
    
    private final Emoter bot;
    
    public ReadyListener(Emoter bot){
        this.bot = bot;
    }
    
    @Override
    public void onReady(@Nonnull ReadyEvent event){
        JDA jda = event.getJDA();
        
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Emotes"));
        Guild guild = jda.getGuildById(bot.getFileManager().getString("config", "guild"));
        
        if(guild == null)
            return;
    
        bot.updateMessage(DBManager.EmoteType.NORMAL, guild);
        bot.updateMessage(DBManager.EmoteType.ANIMATED, guild);
    }
}
