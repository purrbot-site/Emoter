package site.purrbot.emoter.events;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import site.purrbot.emoter.Emoter;
import site.purrbot.emoter.manager.DBManager;

import javax.annotation.Nonnull;

public class EmoteListener extends ListenerAdapter{
    
    private final Emoter bot;
    
    public EmoteListener(Emoter bot){
        this.bot = bot;
    }
    
    @Override
    public void onEmoteAdded(@Nonnull EmoteAddedEvent event){
        Guild guild = event.getGuild();
        Emote emote = event.getEmote();
        
        if(emote.isAnimated()){
            bot.updateMessage(DBManager.EmoteType.ANIMATED, guild);
        }else{
            bot.updateMessage(DBManager.EmoteType.NORMAL, guild);
        }
    }
    
    @Override
    public void onEmoteRemoved(@Nonnull EmoteRemovedEvent event){
        Guild guild = event.getGuild();
        Emote emote = event.getEmote();
    
        if(emote.isAnimated()){
            bot.updateMessage(DBManager.EmoteType.ANIMATED, guild);
        }else{
            bot.updateMessage(DBManager.EmoteType.NORMAL, guild);
        }
    }
}
